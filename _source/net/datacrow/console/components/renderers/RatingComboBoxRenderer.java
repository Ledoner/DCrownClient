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
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcLabel;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Rating;

public class RatingComboBoxRenderer extends DcLabel implements ListCellRenderer {
    
    private static final RatingComboBoxRenderer instance = new RatingComboBoxRenderer();
    private static final Long empty = Long.valueOf(-1);

    private RatingComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    
    public static RatingComboBoxRenderer getInstance() {
        return instance;
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        setText("");
        setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        
        String emptyValue = DcResources.getText("lblIsEmpty");
        if (value == null || value.equals(empty)) {
            setText(" ");
            setIcon(null);
        } else if (emptyValue.equals(value)) {
            setText(emptyValue);
            setIcon(null);
        } else if (value instanceof String) {    
            setIcon(null);
        } else {
            setIcon(Rating.getIcon((Long) value));
        }
        
        return this;
    }
}
