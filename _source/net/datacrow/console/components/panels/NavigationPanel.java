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

package net.datacrow.console.components.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.views.ISortableComponent;
import net.datacrow.core.IconLibrary;

public class NavigationPanel extends JPanel implements ActionListener {
    
    private ISortableComponent component;
    
    public NavigationPanel(ISortableComponent sc) {
        this.component = sc; 
        build();
    }
    
    public void clear() {
    	component = null; 
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        
        JButton buttonTop = ComponentFactory.getIconButton(IconLibrary._icoArrowTop);
        JButton buttonUp = ComponentFactory.getIconButton(IconLibrary._icoArrowUp);
        JButton buttonDown = ComponentFactory.getIconButton(IconLibrary._icoArrowDown);
        JButton buttonBottom = ComponentFactory.getIconButton(IconLibrary._icoArrowBottom);

        buttonTop.addActionListener(this);
        buttonTop.setActionCommand("rowToTop");
        buttonUp.addActionListener(this);
        buttonUp.setActionCommand("rowUp");
        buttonDown.addActionListener(this);
        buttonDown.setActionCommand("rowDown");
        buttonBottom.addActionListener(this);
        buttonBottom.setActionCommand("rowToBottom");
        
        add(buttonTop, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(buttonUp,  Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(buttonDown,Layout.getGBC(0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(buttonBottom,Layout.getGBC(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("rowUp"))
            component.moveRowUp();
        else if (ae.getActionCommand().equals("rowDown"))
        	component.moveRowDown();
        else if (ae.getActionCommand().equals("rowToTop"))
        	component.moveRowToTop();
        else if (ae.getActionCommand().equals("rowToBottom"))
        	component.moveRowToBottom();
    }
}
