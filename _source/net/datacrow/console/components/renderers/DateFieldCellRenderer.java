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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;

public class DateFieldCellRenderer  extends DcTableCellRenderer {
    
    private static final DateFieldCellRenderer instance = new DateFieldCellRenderer();

    private DateFieldCellRenderer() {
        setOpaque(true);

        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    
    public static DateFieldCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        if (value instanceof Date) {
            String s = sdf.format(((Date) value));
            setText(s);
            setToolTipText(s);
        } else if (value instanceof String) {
        	setText((String) value);
        	setToolTipText((String) value);
        }
        
        return renderer;
    }
}
