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

package net.datacrow.console.wizards.itemexport;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.filefilters.DcFileFilter;

public class ItemExporterSettingsPanel extends ItemExporterWizardPanel {

    private DcFileField target = ComponentFactory.getFileField(false, false);
    private ItemExporterImageSettingsPanel settingsPanel = new ItemExporterImageSettingsPanel();
    
    public ItemExporterSettingsPanel(ItemExporterWizard wizard) {
        super(wizard);
        build();
    }
    
    @Override
    public Object apply() throws WizardException {

        if (target.getFile() == null || CoreUtilities.isEmpty(target.getFilename()))
            throw new WizardException(DcResources.getText("msgNoFileSelected"));
        
        
        String filename = target.getFilename();
        filename = filename.endsWith(definition.getExporter().getFileType()) ? filename : filename + "." + definition.getExporter().getFileType();
        File file = new File(filename);
        target.setFile(file);
        
        if (file.exists()) {
            file.delete();
            if (file.exists())
                throw new WizardException(DcResources.getText("msgFileCannotBeUsed"));
        }
        
        definition.setFile(file);
        settingsPanel.saveSettings(definition.getSettings(), false);
            
        return definition;
    }

    @Override
    public void onActivation() {
        if (definition != null && definition.getExporter() != null) {
            settingsPanel.applySettings(definition.getSettings());
            target.setFileFilter(new DcFileFilter(definition.getExporter().getFileType()));
            target.setFile(definition.getFile());
        }
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgExportSettings");
    }

    private void build() {
        setLayout(Layout.getGBL());
        add(target,         Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
            new Insets( 0, 5, 5, 5), 0, 0));
        add(settingsPanel,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
    }
}
