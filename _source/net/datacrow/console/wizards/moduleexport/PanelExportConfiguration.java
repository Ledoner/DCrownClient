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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.settings.SettingsPanel;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsGroup;
import net.datacrow.core.utilities.CoreUtilities;

public class PanelExportConfiguration extends ModuleExportWizardPanel {

	private static final String _EXPORT_DATA_RELATED_MODULES = "export_data_related_modules";
	private static final String _EXPORT_DATA_MAIN_MODULE = "export_data_main_module";
	private static final String _PATH = "export_path";
	
	private SettingsGroup group;
	private SettingsPanel settingsPanel;
	
    public PanelExportConfiguration() {
        build();
    }
    
    @Override
    public Object apply() throws WizardException {
    	ExportDefinition definition = getDefinition();
    	
    	settingsPanel.saveSettings();
    	
    	String path = group.getSettings().get(_PATH).getValueAsString();
    	
    	if (CoreUtilities.isEmpty(path)) {
    		throw new WizardException(DcResources.getText("msgSelectDirFirst"));
    	} else {
	    	definition.setExportDataRelatedModules(((Boolean) group.getSettings().get(_EXPORT_DATA_RELATED_MODULES).getValue()).booleanValue());
	    	definition.setExportDataMainModule(((Boolean) group.getSettings().get(_EXPORT_DATA_MAIN_MODULE).getValue()).booleanValue());
	    	definition.setPath(path);
    	}
    	
        return definition;
    }

    @Override
    public void onActivation() {
		ExportDefinition definition = getDefinition();
		
		if (definition != null) {
			group.getSettings().get(_EXPORT_DATA_RELATED_MODULES).setValue(definition.isExportDataRelatedModules());
			group.getSettings().get(_EXPORT_DATA_MAIN_MODULE).setValue(definition.isExportDataMainModule());
		}
	}

	@Override
    public String getHelpText() {
        return DcResources.getText("msgExportConfigurationHelp");
    }
    
    @Override
    public void destroy() {
    	group = null;
    	settingsPanel = null;
    }    
    
    private void build() {
        setLayout(Layout.getGBL());

        group = new SettingsGroup("", "");
        group.add(new Setting(DcRepository.ValueTypes._BOOLEAN,
        		PanelExportConfiguration._EXPORT_DATA_MAIN_MODULE, Boolean.FALSE, ComponentFactory._CHECKBOX,
                "",  DcResources.getText("lblExportModuleItemsMain"), false, false, -1));
        group.add(new Setting(DcRepository.ValueTypes._BOOLEAN,
        		PanelExportConfiguration._EXPORT_DATA_RELATED_MODULES, Boolean.TRUE, ComponentFactory._CHECKBOX,
                "", DcResources.getText("lblExportModuleItemsSub"), false, false, -1));     
        group.add(new Setting(DcRepository.ValueTypes._STRING, PanelExportConfiguration._PATH, null, ComponentFactory._DIRECTORYFIELD,
                "", DcResources.getText("lblExportModulePath"), true, true, -1));         
        
        settingsPanel = new SettingsPanel(group, true);
        settingsPanel.setVisible(true);
        settingsPanel.initializeSettings();
        
        add(settingsPanel, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 5, 5, 5, 5), 0, 0));
    }
}
