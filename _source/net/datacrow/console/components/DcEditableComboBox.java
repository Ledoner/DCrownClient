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

package net.datacrow.console.components;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.renderers.ComboBoxRenderer;
import net.datacrow.core.resources.DcResources;

public class DcEditableComboBox extends DcComboBox {

    public DcEditableComboBox() {
        super(new Object[] {DcResources.getText("lblIsFilled"),DcResources.getText("lblIsEmpty")});
        setFont(ComponentFactory.getStandardFont());
        setRenderer(ComboBoxRenderer.getInstance());        
        setEditable(true);
    }
    
    @Override
    public void setValue(Object val) {
        insertItemAt(val, 0);
        setSelectedIndex(0);
    }
    
    public void storeValue() {
        Object selectedItem = getSelectedItem();
        if (selectedItem.toString().trim().length() > 0) {
            boolean unique = true;
            Object o;
            for (int i = 1; i < getItemCount(); i++) {
                o = getItemAt(i);
                if (o.equals(selectedItem)) {
                    unique = false;
                    break;
                }
            }
            if (unique) insertItemAt(selectedItem, 2);
        }
    }
}