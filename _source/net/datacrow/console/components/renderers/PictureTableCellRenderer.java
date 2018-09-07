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

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ToolTipManager;

import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.Picture;

public class PictureTableCellRenderer extends DcTableCellRenderer {

    private static final PictureTableCellRenderer instance = new PictureTableCellRenderer();

    private PictureTableCellRenderer() {}
    
    public static PictureTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    protected boolean allowTooltips() {
        return false;
    }
    
    @Override
    public void setText(String str) {
        super.setText(str);

    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (value instanceof Picture && ((Picture) value).hasImage()) {
            setIcon(IconLibrary._icoPicture);
            setForeground(this.getBackground());
        } else {
            setIcon(null);
        }
        
        
        ToolTipManager.sharedInstance().setDismissDelay(0);
        setForeground(this.getBackground());
        return c;
    } 
}
