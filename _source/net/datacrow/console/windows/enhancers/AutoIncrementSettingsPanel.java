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

package net.datacrow.console.windows.enhancers;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.renderers.CheckBoxTableCellRenderer;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcRepository;
import net.datacrow.core.enhancers.AutoIncrementer;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.settings.definitions.DcFieldDefinition;

public class AutoIncrementSettingsPanel extends JPanel {

    private DcModule module;
    private final DcTable table;

    public AutoIncrementSettingsPanel() {
        super();

        table = ComponentFactory.getDCTable(false, false);
        setModule(DcSettings.getInt(DcRepository.Settings.stModule));
        buildComponent();
    }

    public void save() {
        Collection<AutoIncrementer> enhancers = getEnhancers();
        module.removeEnhancers();
        
        for (AutoIncrementer incrementer : enhancers) {
            if (incrementer.isEnabled()) {
                for (DcFieldDefinition definition : module.getFieldDefinitions().getDefinitions()) {                
                    if (definition.getIndex() == incrementer.getField())
                        definition.isEnabled(true);
                }
            }
            
            GUI gui = GUI.getInstance();
            if (module.hasSearchView())
                gui.getSearchView(module.getIndex()).applySettings();

            if (module.hasInsertView())
                gui.getInsertView(module.getIndex()).applySettings();
            
            ValueEnhancers.registerEnhancer(module.getField(incrementer.getField()), incrementer);
        }
        ValueEnhancers.save();
    }
    
    public void setModule(int index) {
        module = DcModules.get(index);
    }

    public void setEnhancers(Collection<AutoIncrementer> enhancers) {
        table.clear();
        
        if (enhancers.size() == 0) {
            enhancers.add(new AutoIncrementer(DcMediaObject._U4_USER_NUMERIC1));
            enhancers.add(new AutoIncrementer(DcMediaObject._U5_USER_NUMERIC2));
        }
        
        int field;
        Object[] row;
        for (AutoIncrementer incrementer : enhancers) {            
            field = incrementer.getField();
            row = new Object[] {  module.getField(field),  
                                           incrementer.isEnabled(), 
                                           incrementer.isFillGaps(), 
                                           incrementer.getStep()};
            table.addRow(row);
        }
        table.applyHeaders();
    }

    public Collection<AutoIncrementer> getEnhancers() {
        Collection<AutoIncrementer> enhancers = new ArrayList<AutoIncrementer>(); 
        DcField field;
        boolean enabled;
        boolean fillGaps;
        int step;
        for (int i = 0; i < table.getRowCount(); i++) {
            field = (DcField) table.getValueAt(i, 0, true);
            if (field != null) {
                enabled = ((Boolean) table.getValueAt(i, 1, true)).booleanValue();
                fillGaps = ((Boolean) table.getValueAt(i, 2, true)).booleanValue();
                step = ((Integer) table.getValueAt(i, 3, true)).intValue();
                
                enhancers.add(new AutoIncrementer(field.getIndex(), enabled, fillGaps, step));
            }
        }
        return enhancers;
    }

    @SuppressWarnings("unchecked")
    private void buildComponent() {
        setLayout(Layout.getGBL());

        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());

        table.setColumnCount(4);

        TableColumn cField = table.getColumnModel().getColumn(0);
        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setEnabled(false);
        textField.setFont(ComponentFactory.getSystemFont());
        textField.setForeground(ComponentFactory.getDisabledColor());
        cField.setCellEditor(new DefaultCellEditor(textField));

        TableColumn columnEnabled = table.getColumnModel().getColumn(1);
        columnEnabled.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        columnEnabled.setCellRenderer(CheckBoxTableCellRenderer.getInstance());

        TableColumn cFillGaps = table.getColumnModel().getColumn(2);
        cFillGaps.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        cFillGaps.setCellRenderer(CheckBoxTableCellRenderer.getInstance());

        TableColumn cStep = table.getColumnModel().getColumn(3);
        JComboBox comboWeight = ComponentFactory.getComboBox();
        for (int i = 1; i < 101; i++)
            comboWeight.addItem(i);

        cStep.setCellEditor(new DefaultCellEditor(comboWeight));

        cField.setHeaderValue(DcResources.getText("lblField"));
        columnEnabled.setHeaderValue(DcResources.getText("lblEnabled"));
        cFillGaps.setHeaderValue(DcResources.getText("lblFillGaps"));
        cStep.setHeaderValue(DcResources.getText("lblStep"));

        Collection<AutoIncrementer> enhancers = (Collection<AutoIncrementer>)
            ValueEnhancers.getEnhancers(module.getIndex(), ValueEnhancers._AUTOINCREMENT);
        
        setEnhancers(enhancers);

        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        add(scroller,  Layout.getGBC( 0, 0, 1, 4, 50.0, 50.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
    }
}
