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
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.datacrow.core.modules.xml.XmlField;

public class XmlFieldCellRenderer extends DcTableCellRenderer {
    
    private static final XmlFieldCellRenderer instance = new XmlFieldCellRenderer();

    private XmlFieldCellRenderer() {}   
    
    public static XmlFieldCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (value instanceof XmlField) {
            XmlField field = (XmlField) value;
            if (!field.isOverwritable()) {
                Font font = c.getFont();
                font = new Font(font.getName(), Font.ITALIC, font.getSize());
                c.setFont(font);
            } else {
                Font font = c.getFont();
                font = new Font(font.getName(), Font.BOLD, font.getSize());
                c.setFont(font);
            }
        }
        
        return c;
    }
}
