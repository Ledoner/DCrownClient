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
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.console.windows.itemforms.DcMinimalisticItemView;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.resources.DcResources;

public class DcPropertyViewPopupMenu extends DcPopupMenu  implements ActionListener {

    public static final int _INSERT = 0;
    public static final int _SEARCH = 1;
    
    private DcMinimalisticItemView form;
    
    public DcPropertyViewPopupMenu(DcMinimalisticItemView form) {
        this.form = form;

        JMenuItem menuOpen = new JMenuItem(DcResources.getText("lblOpenItem", ""), IconLibrary._icoOpen);
        JMenuItem menuDelete = new JMenuItem(DcResources.getText("lblDelete"), IconLibrary._icoDelete);
        JMenuItem menuMerge = new JMenuItem(DcResources.getText("lblMergeItems", form.getModule().getObjectNamePlural()), IconLibrary._icoMerge);
        
        menuOpen.addActionListener(this);
        menuOpen.setActionCommand("open");
        
        menuDelete.addActionListener(this);
        menuDelete.setActionCommand("delete");
        
        menuMerge.addActionListener(this);
        menuMerge.setActionCommand("merge");

        add(menuOpen);
        add(menuDelete);
        
        if (form.getModule().getType() == DcModule._TYPE_PROPERTY_MODULE) {
            addSeparator();
            add(menuMerge);
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("delete"))
            form.delete();
        else if (e.getActionCommand().equals("open"))
            form.open();
        else if (e.getActionCommand().equals("merge"))
            form.mergeItems();
    }
}