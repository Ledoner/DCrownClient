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

package net.datacrow.console.menu;

import java.awt.event.ActionEvent;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcMenu;
import net.datacrow.console.components.DcMenuItem;
import net.datacrow.console.components.panels.tree.TreePanel;
import net.datacrow.console.windows.GroupByDialog;
import net.datacrow.core.resources.DcResources;

public class FieldTreePanelMenuBar extends TreePanelMenuBar {

    public FieldTreePanelMenuBar(int modIdx, TreePanel treePanel) {
        super(modIdx, treePanel);
        
        DcMenu menuFields = ComponentFactory.getMenu(DcResources.getText("lblGroupBy"));
        
        DcMenuItem miGroupBy = ComponentFactory.getMenuItem(DcResources.getText("msgSelectFields"));
        miGroupBy.setActionCommand("groupBy");
        miGroupBy.addActionListener(this);
        menuFields.add(miGroupBy);
        
        add(menuFields);
    }
    
    private void groupBy(String s) {
        GroupByDialog dlg = new GroupByDialog(modIdx);
        dlg.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("groupBy")) {
            groupBy(ae.getActionCommand());
        } else {
            super.actionPerformed(ae);
        }
    }
}
