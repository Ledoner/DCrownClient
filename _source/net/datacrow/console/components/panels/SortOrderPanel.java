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

package net.datacrow.console.components.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.resources.DcResources;

public class SortOrderPanel extends JPanel {
    
    private JComboBox cbOrder = ComponentFactory.getComboBox();
    
    public SortOrderPanel() {
        build();
    }
    
    public int getSortDirection() {
        return cbOrder.getSelectedIndex();
    }
    
    public void setSortOrder(int order) {
        if (order > -1) cbOrder.setSelectedIndex(order);
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        
        cbOrder.addItem(DcResources.getText("lblAscending"));
        cbOrder.addItem(DcResources.getText("lblDescending"));
        
        add(ComponentFactory.getLabel(DcResources.getText("lblSortOrder")),
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));
        add(cbOrder,
                Layout.getGBC( 1, 0, 1, 1, 20.0, 20.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));        
    }
}
