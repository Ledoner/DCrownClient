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

package net.datacrow.console.wizards.moduleexport;

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.console.wizards.module.CreateModuleWizard;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
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
public class ExportModuleWizard extends Wizard {
    
    public ExportModuleWizard() {
        super();
        
        setTitle(DcResources.getText("lblModuleExportWizard"));
        setHelpIndex("dc.modules.export");

        setSize(DcSettings.getDimension(DcRepository.Settings.stModuleExportWizardFormSize));
        setCenteredLocation();
    }
    
    @Override
    protected void initialize() {}
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new PanelSelectModuleToExport(this));
        panels.add(new PanelExportConfiguration());
        panels.add(new PanelExportTask());
        return panels;
    }

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stModuleExportWizardFormSize, getSize());
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
            
            ExportDefinition definition;
            if (getCurrent() instanceof PanelSelectModuleToExport) {
                DcModule module = (DcModule) getCurrent().apply();
                
                if (module == null) return;
                
                definition = new ExportDefinition();
                definition.setModule(module);
            } else {
                definition = (ExportDefinition) getCurrent().apply();
            }
            
            current += 1;
            if (current <= getStepCount()) {
                IWizardPanel panel;
                for (int i = 0; i < getStepCount(); i++) {
                    panel = getWizardPanel(i);
                    
                    if (panel instanceof ModuleExportWizardPanel)
                        ((ModuleExportWizardPanel) panel).setDefinition(definition);

                    panel.setVisible(i == current);
                }
            } else {
                current -= 1;
            }

            applyPanel();
        } catch (WizardException wzexp) {
            if (wzexp.getMessage().length() > 1)
                GUI.getInstance().displayWarningMessage(wzexp.getMessage());
        }
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("msgExportModuleWizard",
                                   new String[] {String.valueOf(current + 1), String.valueOf(getStepCount())});
    }

    @Override
    protected void restart() {
        try {
            finish();
            saveSettings();
            CreateModuleWizard wizard = new CreateModuleWizard();
            wizard.setVisible(true);
        } catch (WizardException exp) {
            GUI.getInstance().displayWarningMessage(exp.getMessage());
        }
    }
}
