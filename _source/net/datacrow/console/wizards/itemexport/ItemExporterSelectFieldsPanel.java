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

import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFieldSelectorField;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;

public class ItemExporterSelectFieldsPanel extends ItemExporterWizardPanel {

    private DcFieldSelectorField fldFields;
    
    public ItemExporterSelectFieldsPanel(ItemExporterWizard wizard) {
        super(wizard);
        build();
    }
    
    @Override
    public Object apply() throws WizardException {
        definition.setFields(fldFields.getSelectedFieldIndices());
        wizard.getModule().getSettings().set(DcRepository.ModuleSettings.stExportFields, definition.getFields());
        return definition;
    }

    @Override
    public void onActivation() {
        if (definition != null && definition.getExporter() != null) {
            if (definition.getFields() != null) {
                fldFields.setSelectedFields(definition.getFields());
            } else {
                int[] fields = wizard.getModule().getSettings().getIntArray(DcRepository.ModuleSettings.stExportFields);
                
                if (fields != null)
                    fldFields.setSelectedFields(fields);
                else 
                    fldFields.setSelectedFields(wizard.getModule().getFieldIndices());
            }
        }
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgExportFieldSelect");
    }

    private void build() {
        fldFields = new DcFieldSelectorField(wizard.getModule().getIndex(), true, true);
        setLayout(Layout.getGBL());
        add(fldFields,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
             new Insets( 5, 5, 5, 5), 0, 0));
    }
}
