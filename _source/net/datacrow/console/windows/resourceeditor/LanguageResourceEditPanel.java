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

package net.datacrow.console.windows.resourceeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcButton;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.resources.DcLanguageResource;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;

public class LanguageResourceEditPanel extends JPanel implements KeyListener, ActionListener {
    
    private DcTable table = ComponentFactory.getDCTable(false, false);
    private String filterKey;
    
    private Map<String, String> allValues = new HashMap<String, String>();
    private DcShortTextField txtFilter = ComponentFactory.getShortTextField(255);
    private DcShortTextField txtReplace = ComponentFactory.getShortTextField(255);
    
    public LanguageResourceEditPanel(String key) {
        this.filterKey = key;
        this.txtFilter.addKeyListener(this);
        
        build();
    }
    
    public void load(DcLanguageResource resources) {
        Set<String> keys = resources.getResourcesMap().keySet();
        ArrayList<String> list = new ArrayList<String>(keys);
        Collections.sort(list);

        for (String resourceKey : list) {
            if (resourceKey.startsWith(filterKey))
                table.addRow(new Object[] {resourceKey, resources.get(resourceKey)});
        }
        
        if (filterKey.equals("sys")) {
            for (DcModule module : DcModules.getAllModules()) {
                if (module.isTopModule() || 
                    module.isChildModule() || 
                    module.getType() == DcModule._TYPE_PROPERTY_MODULE || 
                    module.isAbstract()) {
                    
                    if (!CoreUtilities.isEmpty(module.getLabel()) && CoreUtilities.isEmpty(resources.get( module.getModuleResourceKey())))
                        table.addRow(new Object[] {module.getModuleResourceKey(), module.getLabel()});
                    
                    if (!CoreUtilities.isEmpty(module.getObjectName()) && CoreUtilities.isEmpty(resources.get(module.getItemResourceKey())))
                        table.addRow(new Object[] {module.getItemResourceKey(), module.getObjectName()});

                    if (!CoreUtilities.isEmpty(module.getObjectNamePlural()) && CoreUtilities.isEmpty(resources.get(module.getItemPluralResourceKey())))
                        table.addRow(new Object[] {module.getItemPluralResourceKey(), module.getObjectNamePlural()});

                    for (DcField field : module.getFields()) {
                        if (!CoreUtilities.isEmpty(field.getLabel()) && CoreUtilities.isEmpty(resources.get(field.getResourceKey())))
                            table.addRow(new Object[] {field.getResourceKey(),field.getLabel()});
                    }
                }
            }
        }
        
        for (int i = 0; i < table.getRowCount(); i++) {
            allValues.put(  (String) table.getValueAt(i, 0, true), 
                            (String) table.getValueAt(i, 1, true));
        }
    }
    
    public void replace() {
        String replacement = txtReplace.getText();
        String txt = txtFilter.getText();
        
        if (CoreUtilities.isEmpty(replacement)) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgValueNotFilled", DcResources.getText("lblReplaceWith")));
        } else if (CoreUtilities.isEmpty(txt)) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgValueNotFilled", DcResources.getText("lblFilter")));
        } else {
            String value;
            for (int row = 0; row < table.getRowCount(); row++) {
                value = ((String) table.getValueAt(row, 1, true)).replaceAll(txt, replacement);
                allValues.put( (String) table.getValueAt(row, 0, true), value);
                table.setValueAt(value, row, 1);
            }
        }
    }
    
    private void filter() {
        String filter = txtFilter.getText().toLowerCase();
        
        Map<String, String> current = getValues();
        for (String key : current.keySet()) { 
            allValues.put(key, current.get(key));
        }
        
        if (filter.trim().length() == 0) {
            setValues(allValues);
        } else {
            Map<String, String> filteredValues = new HashMap<String, String>();
            String value;
            for (String key : allValues.keySet()) {
                value = allValues.get(key);
                if (value.toLowerCase().contains(filter))
                    filteredValues.put(key, value);
            }
            setValues(filteredValues);
        }
    }
    
    public void save(DcLanguageResource dlr) {
        Map<String, String> values = getValues();
        for (String key : values.keySet())
            dlr.put(key, values.get(key));
    }
    
    private void build() {
        
        //**********************************************************
        //Table Panel
        //**********************************************************
        JScrollPane sp = new JScrollPane(table);
        table.setColumnCount(2);
    
        TableColumn columnKey = table.getColumnModel().getColumn(0);
        columnKey.setMaxWidth(300);
        columnKey.setPreferredWidth(150);
        columnKey.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));       
        columnKey.setHeaderValue(DcResources.getText("lblKey"));
        
        TableColumn columnValue = table.getColumnModel().getColumn(1);
        columnValue.setCellEditor(new DefaultCellEditor(ComponentFactory.getShortTextField(1000)));
        columnValue.setHeaderValue(DcResources.getText("lblValue"));
        
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);        
        
        table.applyHeaders();
        
        ComponentFactory.setBorder(sp);
        
        //**********************************************************
        //Main Panel
        //**********************************************************
        setLayout(Layout.getGBL());
        
        DcButton btFilter = ComponentFactory.getIconButton(IconLibrary._icoAccept);
        btFilter.setActionCommand("filter");
        btFilter.addActionListener(this);
        
        add(ComponentFactory.getLabel(DcResources.getText("lblFilter")), Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 0, 5), 0, 0));
        add(txtFilter, Layout.getGBC( 1, 0, 2, 1, 100.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 0, 5), 0, 0));

        DcButton btReplace = ComponentFactory.getIconButton(IconLibrary._icoAccept);
        btReplace.setActionCommand("replace");
        btReplace.addActionListener(this);
        
        add(ComponentFactory.getLabel(DcResources.getText("lblReplaceWith")), Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 5, 5, 5), 0, 0));
        add(txtReplace, Layout.getGBC( 1, 1, 1, 1, 100.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));
        add(btReplace, Layout.getGBC( 2, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 5, 5), 0, 0));
        
        add(sp, Layout.getGBC( 0, 2, 3, 1, 100.0, 100.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 5, 5, 5), 0, 0));
    }
    
    private void setValues(Map<String, String> values) {
        table.clear();
        for (String key : values.keySet()) 
            table.addRow(new Object[] {key, values.get(key)});
    }
    
    private Map<String, String> getValues() {
        Map<String, String> m = new HashMap<String, String>();
        table.cancelEdit();
        for (int i = 0; i < table.getRowCount(); i++) {
            m.put( (String) table.getValueAt(i, 0, true), 
                   (String) table.getValueAt(i, 1, true));
        }
        return m;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("replace")) {
            replace();
        } else if (ae.getActionCommand().equals("filter")) {
            filter();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        filter();
    }
}
