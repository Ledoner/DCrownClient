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

package net.datacrow.console.wizards.moduleimport;

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.console.wizards.module.RestartDataCrowDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

/**
 * The Module Export Wizard exports a custom module. The wizard is capable of exporting
 * not only the module itself but also its related custom modules. The data of the module can 
 * also be exported. Everything is exported to module archive which can be imported using
 * the Module Import Wizard. 
 *  
 * @author Robert Jan van der Waals
 */
public class ImportModuleWizard extends Wizard {
    
    public ImportModuleWizard() {
        super();
        
        setTitle(DcResources.getText("lblModuleImportWizard"));
        setHelpIndex("dc.modules.import");

        setSize(DcSettings.getDimension(DcRepository.Settings.stModuleImportWizardFormSize));
        setCenteredLocation();
    }
    
    @Override
    protected void initialize() {}
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new PanelImportConfiguration());
        panels.add(new PanelImportTask());
        return panels;
    }

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stModuleImportWizardFormSize, getSize());
    }

    @Override
    public void finish() throws WizardException {
        close();
    }

    @Override
    protected boolean isRestartSupported() {
        return false;
    }

    @Override
    public void next() {
        try {
            ImportDefinition definition = (ImportDefinition) getCurrent().apply();
            
            current += 1;
            if (current <= getStepCount()) {
                ModuleImportWizardPanel panel;
                for (int i = 0; i < getStepCount(); i++) {
                    panel = (ModuleImportWizardPanel) getWizardPanel(i);
                    panel.setDefinition(definition);
                    panel.setVisible(i == current);
                }
            } else {
                current -= 1;
            }

            applyPanel();
        } catch (WizardException wzexp) {
            GUI.getInstance().displayWarningMessage(wzexp.getMessage());
        }
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("msgImportModuleWizard",
                                   new String[] {String.valueOf(current + 1), String.valueOf(getStepCount())});
    }

    @Override
    public void close() {
        if (!isCancelled() && !isRestarted())
            new RestartDataCrowDialog(this);
        
        super.close();
    }

    @Override
    protected void restart() {}
}
