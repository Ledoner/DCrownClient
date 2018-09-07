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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;

public class DcFieldSelectorField extends JComponent implements IComponent, ActionListener {

    private Map<DcField, JCheckBox> componentMap = new LinkedHashMap<DcField, JCheckBox>();
    private final int module;

    public DcFieldSelectorField(int module, boolean includeID, boolean showMenu) {
        this.module = module;
        buildComponent(includeID, showMenu);
    }

    @Override
    public Object getValue() {
        return getSelectedFields();
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof int[]) {
            setSelectedFields((int[]) o);
        }
    }
    
    @Override
    public void setEditable(boolean b) {
        for (JCheckBox cb : componentMap.values()) 
            cb.setEnabled(b);
    }
    
    @Override
    public void clear() {
        componentMap.clear();
        componentMap = null;
    }    

    public void invertSelection() {
        JCheckBox checkBox;
        for (DcField field : componentMap.keySet()) {
            checkBox = componentMap.get(field);
            checkBox.setSelected(!checkBox.isSelected());
        }
    }
    
    public void selectAll() {
        for (DcField field : componentMap.keySet())
            componentMap.get(field).setSelected(true);
    }

    public void unselectAll() {
        for (DcField field : componentMap.keySet())
            componentMap.get(field).setSelected(false);
    }

    public int[] getSelectedFieldIndices() {
        Collection<DcField> fields = getSelectedFields();
        int[] indices = new int[fields.size()];
        int counter = 0;
        for (DcField field : fields)
            indices[counter++] = field.getIndex();
        
        return indices;
    }    

    public Collection<DcField> getSelectedFields() {
        Collection<DcField> fields = new ArrayList<DcField>();
        JCheckBox checkBox;
        for (DcField field : componentMap.keySet()) {
            checkBox = componentMap.get(field);
            if (checkBox.isSelected())
                fields.add(field);
        }
        return fields;
    }

    public void setSelectedFields(int[] fields) {
        JCheckBox checkBox;
        for (DcField field : componentMap.keySet()) {
            checkBox = componentMap.get(field);
            if (fields.length == 0) {
                checkBox.setSelected(true);
            } else {
                for (int j = 0; j < fields.length; j++) {
                    if (field.getIndex() == fields[j])
                        checkBox.setSelected(true);
                }
            }
        }
    }

    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    private void buildComponent(boolean includeID, boolean showMenu) {
        setLayout(Layout.getGBL());

        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());

        int x = 0;
        int y = 0;

        JCheckBox checkBox;
        for (DcField field : DcModules.get(module).getFields()) {
            
            if ((field.getIndex() == DcObject._ID && !includeID) ||
                 field.getSystemName().endsWith("_persist") ||
                 field.getIndex() == DcObject._SYS_EXTERNAL_REFERENCES)
                continue;
             
            checkBox = ComponentFactory.getCheckBox(field.getLabel());
            componentMap.put(field, checkBox);

            panel.add(checkBox, Layout.getGBC(x, y++, 1, 1, 1.0, 1.0
                     ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                      new Insets( 0, 10, 0, 0), 0, 0));

            if (y == 12) {
                x++;
                y = 0;
            }
        }

        if (showMenu) {
            JMenu editMenu = createMenu();
            JMenuBar mb = ComponentFactory.getMenuBar();
            mb.add(editMenu);
            
            mb.setMinimumSize(new Dimension(100, 23));
            mb.setPreferredSize(new Dimension(100, 23));
            
            add(mb, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets( 0, 0, 0, 0), 0, 0));
        }
        
        add(panel, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                  ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                   new Insets( 0, 0, 0, 0), 0, 0));
    }
    
    private JMenu createMenu() {
        JMenu menu = ComponentFactory.getMenu("Edit");
        
        JMenuItem menuSelectAll = ComponentFactory.getMenuItem(DcResources.getText("lblSelectAll"));
        JMenuItem menuUnselectAll = ComponentFactory.getMenuItem(DcResources.getText("lblUnselectAll"));
        JMenuItem menuInvertSelection = ComponentFactory.getMenuItem(DcResources.getText("lblInvertSelection"));

        menuSelectAll.addActionListener(this);
        menuSelectAll.setActionCommand("selectAll");
        menuUnselectAll.addActionListener(this);
        menuUnselectAll.setActionCommand("unselectAll");
        menuInvertSelection.addActionListener(this);
        menuInvertSelection.setActionCommand("invertSelection");
        
        menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuUnselectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        menuInvertSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        
        menu.add(menuSelectAll);
        menu.add(menuUnselectAll);
        menu.addSeparator();
        menu.add(menuInvertSelection);
        
        return menu;
    }    
    
    @Override
    public void refresh() {}
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("unselectAll"))
            unselectAll();
        else if (ae.getActionCommand().equals("selectAll"))
            selectAll();
        else if (ae.getActionCommand().equals("invertSelection"))
            invertSelection();
    }
}
