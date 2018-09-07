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

import javax.swing.JMenuItem;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.console.windows.filerenamer.FileRenamerPreviewDialog;
import net.datacrow.core.resources.DcResources;

public class DcFileRenamerPreviewPopupMenu extends DcPopupMenu {
    
    public DcFileRenamerPreviewPopupMenu(FileRenamerPreviewDialog listener) {
        JMenuItem menuRemove = ComponentFactory.getMenuItem(DcResources.getText("lblRemove"));
        menuRemove.setActionCommand("remove");
        menuRemove.addActionListener(listener);
        add(menuRemove);
    }
}
