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

import javax.swing.JToolTip;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.plugin.Plugin;

public class DcToolBarButton extends DcButton {

    private final String text;
    
    public DcToolBarButton(Plugin plugin) {
        super(plugin.getIcon());
        
        text = plugin.getLabelShort();
        
        setFont(ComponentFactory.getSystemFont());
        setText(plugin.getLabelShort());
        setVerticalTextPosition(BOTTOM);
        setHorizontalTextPosition(CENTER);
        setToolTipText(plugin.getHelpText() == null ? getText() : plugin.getHelpText());
        
        addActionListener(plugin);
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    public void hideText() {
        setText("");
    }
    
    public void showText() {
        setText(text);
    }
}
