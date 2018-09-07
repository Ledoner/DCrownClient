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

package net.datacrow.console.components.renderers;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTable;


public class ReferencesTableCellRenderer extends DcTableCellRenderer {

    private static final ReferencesTableCellRenderer instance = new ReferencesTableCellRenderer();

    private ReferencesTableCellRenderer() {}
    
    public static ReferencesTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);           
        
        String txt = "";
        if (value instanceof Collection) {
            Collection c = (Collection) value;
            boolean first = true;
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                if (!first) txt += ", ";
                
                first = false;
                txt += iter.next();
            }
        } else if (value instanceof String) {
            txt = (String) value;
        }
        
        setText(txt);
        setToolTipText(txt);
        
        return this;
    }
}