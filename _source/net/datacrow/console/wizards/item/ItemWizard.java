/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.console.wizards.item;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.datacrow.console.GUI;
import net.datacrow.console.clients.UIClient;
import net.datacrow.console.windows.ItemTypeDialog;
import net.datacrow.console.windows.SelectItemDialog;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.wf.tasks.SaveItemTask;
import net.datacrow.settings.DcSettings;

public class ItemWizard extends Wizard implements IClient {

    private DcObject dco;
    private DcObject container;

    public ItemWizard() {
        super();
        
        if (!closed) {
            setTitle(DcResources.getText("lblItemWizard"));
            setHelpIndex("dc.items.wizard");

            setSize(DcSettings.getDimension(DcRepository.Settings.stItemWizardFormSize));
            setCenteredLocation();
        }
    }
    
    @Override
    protected void initialize() {
        if (getModule().isAbstract()) {
            ItemTypeDialog dialog = new ItemTypeDialog(DcResources.getText("lblSelectModuleHelp"));
            dialog.setVisible(true);

            if (dialog.getSelectedModule() < 0) {
                closed = true;
                close();
            } else {
                moduleIdx = DcModules.get(dialog.getSelectedModule()).getIndex();
            }
        }
        
        if (getModule().getIndex() == DcModules._ITEM &&
            DcModules.get(DcSettings.getInt(DcRepository.Settings.stModule)).getIndex() == DcModules._CONTAINER) {
            
            SelectItemDialog dlg = new SelectItemDialog(DcModules.get(DcModules._CONTAINER), "Select a container");
            dlg.setVisible(true);
            container = dlg.getItem();
        }
        
        if (getModule() != null)
            dco = getModule().getItem();
    }
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        
        if (getModule().hasOnlineServices())
            panels.add(new InternetWizardPanel(this, getModule()));

        panels.add(new ItemDetailsWizardPanel(dco));
        return panels;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b && !closed);
        if (!closed && b && getCurrent() instanceof InternetWizardPanel)
            ((InternetWizardPanel) getCurrent()).setFocus();
    }

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stItemWizardFormSize, getSize());
    }

    @Override
    public void finish() throws WizardException {
        
        dco = (DcObject) getCurrent().apply();
        
        SaveItemTask task = new SaveItemTask();
        task.addClient(this);
        task.addClient(new UIClient(UIClient._INSERT, dco, true, true));
        task.addItem(dco);
        
        Connector connector = DcConfig.getInstance().getConnector();
        connector.executeTask(task);
    }

    @Override
    public void next() {
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        dco = (DcObject) getCurrent().apply();
                        
                        if (container != null && dco.getModule().isContainerManaged())
                            dco.setValue(DcObject._SYS_CONTAINER, container);
                        
                        SwingUtilities.invokeLater(
                                new Thread(new Runnable() { 
                                    @Override
                                    public void run() {
                                        current += 1;
                                        if (current <= getStepCount()) {
                                            ItemWizardPanel panel;
                                            for (int i = 0; i < getStepCount(); i++) {
                                                panel = (ItemWizardPanel) getWizardPanel(i);
                                                panel.setObject(dco);
                                                panel.setVisible(i == current);
                                            }
                                        } else {
                                            current -= 1;
                                        }
                
                                        applyPanel();
                                    }
                                }));
                    } catch (WizardException wzexp) {
                        if (wzexp.getMessage().length() > 1)
                            GUI.getInstance().displayWarningMessage(wzexp.getMessage());
                    }
                }
            }).start();
    }

    @Override
    public void close() {
        dco = null;
        super.close();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("msgNewItemWizard",
                                   new String[] {dco.getModule().getObjectName(),
                                   String.valueOf(current + 1),
                                   String.valueOf(getStepCount())});
    }

    @Override
    protected void restart() {
        try {
            finish();
            saveSettings();
            ItemWizard wizard = new ItemWizard();
            wizard.setVisible(true);
        } catch (WizardException exp) {
            GUI.getInstance().displayWarningMessage(exp.getMessage());
        }
    }

    @Override
    public void notify(String msg) {
        GUI.getInstance().displayMessage(msg);
    }

    @Override
    public void notifyError(Throwable t) {
        GUI.getInstance().displayErrorMessage(t.getMessage());
    }

    @Override
    public void notifyWarning(String msg) {
        GUI.getInstance().displayWarningMessage(msg);
    }

    @Override
    public void notifyTaskStarted(int taskSize) {}

    @Override
    public void notifyProcessed() {}

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {
        if (success)
            close();
    }
}
