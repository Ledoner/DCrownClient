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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;

public class DcModuleSelector extends JComponent implements IComponent {

    private JPanel panel = new JPanel();
    
    public DcModuleSelector() {
        buildComponent();
    }
    
    @Override
    public Object getValue() {
        return "";
    }    
    
    @Override
    public void setValue(Object value) {
    }
    
    @Override
    public void clear() {
        panel = null;
    } 
    
    @Override
    public void setEditable(boolean b) {}
    
    private void buildComponent() {
        setLayout(Layout.getGBL());
        panel.setLayout(Layout.getGBL());

        int y = 0;
        int x = 0;
        
        JCheckBox checkBox;
        JLabel label;
        for (DcModule module : DcModules.getAllModules()) {
            if (DcModules.isTopModule(module.getIndex())) {
                checkBox = ComponentFactory.getCheckBox("");
                checkBox.setSelected(module.isEnabled());
                checkBox.addActionListener(new ToggleModuleAction(module.getIndex()));
                label = ComponentFactory.getLabel(module.getLabel(), module.getIcon32());
                panel.add(checkBox,   Layout.getGBC( x, y, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 0, 5, 5, 5), 0, 0));
                panel.add(label,      Layout.getGBC( x + 1, y, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 0, 5, 5, 5), 0, 0));
                y++;
                
                if (y == 8) {
                    y = 0;
                    x += 2;
                }
            }
        }
        
        add(panel, Layout.getGBC( 1, y, 1, 1, 1.0, 1.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets( 0, 5, 5, 5), 0, 0));
    }   
    
    @Override
    public void refresh() {}

    private static class ToggleModuleAction implements ActionListener {
        
        private final int module;
        
        public ToggleModuleAction(int module) {
            this.module = module;
        }
        
        @Override
        public void actionPerformed(ActionEvent ev) {
            if (ev.getSource() instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) ev.getSource();
                DcModules.get(module).isEnabled(checkbox.isSelected());
            }
        }
    }
}
