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

import net.datacrow.core.utilities.CoreUtilities;

public class FileSizeTableCellRenderer extends DcTableCellRenderer {
    
    private static final FileSizeTableCellRenderer instance = new FileSizeTableCellRenderer();
    
    private FileSizeTableCellRenderer() {}   
    
    public static FileSizeTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    protected String getTipText() {
        return super.getTipText();
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(JLabel.RIGHT); // Reset right justify again
        
        if (value instanceof Long) {
            String s = CoreUtilities.toFileSizeString((Long) value); 
            setText(s);
            setToolTipText(s);
        }
        
        return renderer;
    }
}
