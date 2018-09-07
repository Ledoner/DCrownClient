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
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;

public class ComboBoxRenderer extends DcLabel implements ListCellRenderer {
    
    private static final ComboBoxRenderer instance = new ComboBoxRenderer();

    private ComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    
    public static ComboBoxRenderer getInstance() {
        return instance;
    }    
    
    @Override
    public Component getListCellRendererComponent(
                                       JList list,
                                       Object value,
                                       int index,
                                       boolean isSelected,
                                       boolean cellHasFocus) {
        
    	setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
    	setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
    	
    	setFont(ComponentFactory.getStandardFont());
    	
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        } 

        if (value instanceof DcObject) {
            DcObject o = (DcObject) value;
            setIcon(o.getIcon());
            setText(o.toString());
        } else if (value instanceof DcModule) {
        	DcModule module = (DcModule) value;
            setIcon(module.getIcon16());
            setText(module.getLabel());
        } else if (value instanceof String) {
            setIcon(null);
            setText((String) value);
        } else if (value != null){
            setIcon(null);
            setText(value.toString());
        } else  {
            setIcon(null);
            setText("");
        }
        
        return this;
    }
}
