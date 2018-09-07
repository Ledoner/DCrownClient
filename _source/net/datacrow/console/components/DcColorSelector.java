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

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.datacrow.settings.DcSettings;

public class DcColorSelector extends JColorChooser implements IComponent, ChangeListener {
    
    private Color color;
    private String settingsKey;

    public DcColorSelector(String settingsKey) {
        super();
        getSelectionModel().addChangeListener(this);
        this.settingsKey = settingsKey;
    }

    @Override
    public Object getValue() {
        return color;
    }

    @Override
    public void setValue(Object o) {
        color = (Color) o;
        setColor(color);
    }

    @Override
    public void clear() {
        color = null;
        settingsKey = null;        
    }
    
    @Override
    public void setEditable(boolean b) {
        super.setEnabled(b);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        Color temp = getColor();
        if (color != null && !color.equals(temp)) {
            color = temp;
            DcSettings.set(settingsKey, color);
        }
    }
    
    @Override
    public void refresh() {}
}
