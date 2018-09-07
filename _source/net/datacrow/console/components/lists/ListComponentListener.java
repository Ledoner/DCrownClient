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

package net.datacrow.console.components.lists;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JViewport;

import net.datacrow.console.components.lists.elements.DcObjectListElement;
import net.datacrow.core.modules.DcModule;

public class ListComponentListener implements ComponentListener {

    public ListComponentListener() {}

    @Override
    public void componentResized(ComponentEvent e) {
        DcObjectList list = (DcObjectList) e.getSource();
        if (list.getItemCount() > 0 && 
            list.getModule() != null &&  
            list.getModule().getType() != DcModule._TYPE_PROPERTY_MODULE) {
            
            DcObjectListElement elem = (DcObjectListElement) list.getDcModel().getElementAt(0);
            Dimension elemSize = elem.getPreferredSize();

            list.setFixedCellHeight(elemSize.height);
            list.setFixedCellWidth(elemSize.width);

            int width = ((JViewport) list.getParent()).getWidth();
            list.setColumnsPerRow((int) Math.floor(width / elemSize.width));
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {}
    @Override
    public void componentHidden(ComponentEvent e) {}
    @Override
    public void componentMoved(ComponentEvent e) {}
}
