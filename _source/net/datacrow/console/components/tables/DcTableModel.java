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

package net.datacrow.console.components.tables;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class DcTableModel extends DefaultTableModel {
    
    private boolean readonly = false;
    
    public DcTableModel(boolean readonly) {
        super();
        this.readonly = readonly;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        return !readonly;
    }
    
    @Override
    public void setValueAt(Object o, int row, int column) {
        if (getRowCount() > 0 && row != -1 && column != -1) {
            Object old = getValueAt(row, column);
            
            if (old instanceof Boolean && o instanceof String)
                o = Boolean.valueOf((String) o);

            if (old instanceof Long && o instanceof String)
                o = Long.valueOf((String) o);
            
            if ((old != null && old instanceof List) && o != null && !(o instanceof List)) 
                return;
            
            super.setValueAt(o, row, column);
        }
    }
}