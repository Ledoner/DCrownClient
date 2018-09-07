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
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

public class AvailabilityComboBoxRenderer extends DcLabel implements ListCellRenderer {
    
    private static final AvailabilityComboBoxRenderer instance = new AvailabilityComboBoxRenderer();
    
    private AvailabilityComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    public static AvailabilityComboBoxRenderer getInstance() {
        return instance;
    }    
    
    @Override
    public Component getListCellRendererComponent(
                                       JList list,
                                       Object value,
                                       int index,
                                       boolean isSelected,
                                       boolean cellHasFocus) {
        
        setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        } 

        setIcon(null);
        setText(value == null ? "" : value.toString());
        if (value instanceof Boolean) {
            boolean b = ((Boolean) value).booleanValue();
            if (b) {
                setIcon(IconLibrary._icoChecked);
                setText(DcResources.getText("lblAvailable"));
            } else {
                setIcon(IconLibrary._icoUnchecked);
                setText(DcResources.getText("lblUnavailable"));
            }
        }
        
        return this;
    }
}
