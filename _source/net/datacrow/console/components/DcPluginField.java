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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.plugin.Plugin;

public class DcPluginField extends JComponent implements ActionListener {
    
    private DcButton bt;
    private Plugin plugin;
    
    public DcPluginField(Plugin plugin) {
        this.plugin = plugin;
        build();
    }
    
    public void addActionListener(ActionListener al) {
        bt.addActionListener(al);
    }
    
    private void build() {
        DcLongTextField fldHelp = ComponentFactory.getHelpTextField();
        fldHelp.setText(plugin.getHelpText());
        
        bt = ComponentFactory.getIconButton(plugin.getIcon());
        bt.addActionListener(this);
        
        setLayout(Layout.getGBL());
        
        add(bt, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        add(fldHelp, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        plugin.actionPerformed(ae);
    }
}
