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

package net.datacrow.console.windows.drivemanager;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcMultiLineToolTip;
import net.datacrow.console.components.IComponent;
import net.datacrow.core.utilities.CoreUtilities;

public class DriveSelectorField extends JComponent implements IComponent {

    private Map<File, JCheckBox> componentMap = new HashMap<File, JCheckBox>();

    public DriveSelectorField() {
        buildComponent();
    }

    @Override
    public Object getValue() {
        return getDrives();
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof String[]) {
            setSelectedDrives((String[]) o);
        }
    }
    
    @Override
    public void setEnabled(boolean b) {
        for (JCheckBox cb : componentMap.values())
            cb.setEnabled(b);
    }
    
    @Override
    public void clear() {
        componentMap.clear();
        componentMap = null;
    }    

    public Collection<File> getDrives() {
        Collection<File> drives = new ArrayList<File>();
        for (File drive: componentMap.keySet()) {
            if (componentMap.get(drive).isSelected())
                drives.add(drive);
        }
        return drives;
    }    
    
    @Override
    public void setEditable(boolean b) {}

    public void setSelectedDrives(String[] drives) {
        JCheckBox cb;
        for (File drive : componentMap.keySet()) {
            cb = componentMap.get(drive);
            if (drives.length == 0) {
                cb.setSelected(true);
            } else {
                for (String drv : drives) {
                    if (drv.equals(drive.toString()))
                         cb.setSelected(true);
                }
            }
        }
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        for (JCheckBox cb : componentMap.values())
             cb.setFont(ComponentFactory.getSystemFont());
    }

    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    private void buildComponent() {
        setLayout(Layout.getGBL());

        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());

        int x = 0;
        int y = 0;

        String name;
        JCheckBox checkBox;
        for (File drive : CoreUtilities.getDrives()) {
            name = CoreUtilities.getSystemName(drive);
            name = name == null || name.length() == 0 ? drive.toString() : name;
            
            checkBox = ComponentFactory.getCheckBox(name);
            componentMap.put(drive, checkBox);

            panel.add(checkBox, Layout.getGBC(x, y++, 1, 1, 1.0, 1.0
                     ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                      new Insets( 0, 5, 0, 0), 0, 0));

            if (y == 5) {
                x++;
                y = 0;
            }
        }

        add(panel, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                  ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                   new Insets( 0, 0, 0, 0), 0, 0));
    }

    @Override
    public void refresh() {}
}
