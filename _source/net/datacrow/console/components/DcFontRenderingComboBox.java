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

import net.datacrow.core.resources.DcResources;


public class DcFontRenderingComboBox extends DcComboBox {

    public DcFontRenderingComboBox() {
        super();
        
        addItem(DcResources.getText("lblDefault"));
        addItem("LCD HRGB");
        addItem("LCD HBGR");
        addItem("LCD VRGB");
        addItem("LCD VBGR");
    }
    
    @Override
    public void setValue(Object value) {
        if (value instanceof Long)
            setSelectedIndex(((Long) value).intValue());
        else
            setSelectedItem(value);
    }

    @Override
    public Object getValue() {
        int index = getSelectedIndex();
        index = index == -1 ? 0 : index;
        return Long.valueOf(index);
    }
}
