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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import net.datacrow.settings.DcSettings;

public class DcViewDivider extends JSplitPane {
    
    private final String settingKey;
    
    public DcViewDivider(JComponent left, JComponent right, String settingKey) {
        super(JSplitPane.HORIZONTAL_SPLIT, left, right);
        this.settingKey = settingKey;
        
        Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        setBorder(border);
    }
    
    public void deactivate() {
        DcSettings.set(settingKey, getDividerLocation());
    }
    
    public void applyDividerLocation() {
        if (isEnabled()) {
            setDividerLocation(DcSettings.getInt(settingKey));
        }
    }
}
