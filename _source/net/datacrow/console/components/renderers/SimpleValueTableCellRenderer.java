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

import javax.swing.JLabel;
import javax.swing.JTable;

import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.objects.DcSimpleValue;

public class SimpleValueTableCellRenderer extends DcTableCellRenderer {
    
    private static final SimpleValueTableCellRenderer instance = new SimpleValueTableCellRenderer();
    
    private SimpleValueTableCellRenderer() {}   
    
    public static SimpleValueTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (((DcTable) table).isIgnoringPaintRequests())
            return renderer;
        
        setHorizontalAlignment(JLabel.LEFT);
        
        if (value instanceof DcSimpleValue) {
            DcSimpleValue sv = (DcSimpleValue) value;
            setText(sv.getName());
            setToolTipText(sv.getName());
            setIcon(sv.getIcon());
        }
        
        return renderer;
    }
}
