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

import javax.swing.JTable;

import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Rating;

public class RatingTableCellRenderer extends DcTableCellRenderer {

    private static final RatingTableCellRenderer instance = new RatingTableCellRenderer();
    
    private static final Long empty = Long.valueOf(-1);
    
    private RatingTableCellRenderer() {}
    
    public static RatingTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
	public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);           
        
        
        setText("");
        if (value == null || value.equals(empty)) {
            setText(DcResources.getText("lblRatingNotRated"));
            setIcon(null);
        } else {
            setIcon(Rating.getIcon((Long) value));
        }        
        
		return this;
	}
    
    @Override
    protected String getTipText() {
        int integer = -1;
        try {
            integer = Integer.parseInt(getText());
        } catch (Exception e) {}
        
        return Rating.getLabel(integer);
    }    
}
