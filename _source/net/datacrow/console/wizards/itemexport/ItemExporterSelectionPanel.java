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
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.migration.itemexport.ItemExporter;
import net.datacrow.core.migration.itemexport.ItemExporters;
import net.datacrow.core.resources.DcResources;

public class ItemExporterSelectionPanel extends ItemExporterWizardPanel {

    private ButtonGroup bg = new ButtonGroup();
    private Collection<ItemExporter> exporters = new ArrayList<ItemExporter>();
    
    public ItemExporterSelectionPanel(ItemExporterWizard wizard) {
        super(wizard);
        build();
    }
    
    @Override
    public Object apply() throws WizardException {
        String command = bg.getSelection().getActionCommand();
        for (ItemExporter exporter : exporters) {
            if (exporter.getKey().equals(command))
                wizard.getDefinition().setExporter(exporter);
        }
        
        return definition;
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectExportMethod");
    }
    
    @Override
    public void destroy() {
        bg  = null;
        if (exporters != null) exporters.clear();
        exporters = null;
    }  
    
    private void build() {
        setLayout(Layout.getGBL());
        
        int y = 0;
        int x = 0;
        
        JRadioButton rb;
        for (ItemExporter exporter : ItemExporters.getInstance().getExporters(wizard.getModuleIdx())) {
            exporters.add(exporter);
            rb = ComponentFactory.getRadioButton(exporter.getName(), exporter.getIcon(), exporter.getKey());
            bg.add(rb);
            add(rb, Layout.getGBC( x, y++, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 0, 5, 5, 5), 0, 0));

            if (y == 1)
                rb.setSelected(true);
        }
    }
}
