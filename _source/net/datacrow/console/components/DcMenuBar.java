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

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolTip;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;

public class DcMenuBar extends JMenuBar {
    
    public DcMenuBar() {
        super();
    }

    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
 
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        JMenu menu;
        for (int i = 0; i < getMenuCount(); i++) {
            
            try {
                menu = getMenu(i);
                menu.setFont(ComponentFactory.getSystemFont());
                for (int j = 0; j < menu.getMenuComponents().length; j++) {
                    menu.getMenuComponents()[j].setFont(ComponentFactory.getStandardFont());
                }
            } catch (Exception e) {}
        }
        revalidate();
    }    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    } 
}
