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

package net.datacrow.console.wizards.module;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLabel;
import net.datacrow.console.components.renderers.CheckBoxTableCellRenderer;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.core.modules.DcAssociateModule;
import net.datacrow.core.modules.DcMediaModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.resources.DcResources;

public class PanelFields extends ModuleWizardPanel implements ActionListener {

    private JButton buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAddField"));
    private JButton buttonAlter = ComponentFactory.getButton(DcResources.getText("lblAlterField"));
    private JButton buttonRemove = ComponentFactory.getButton(DcResources.getText("lblRemoveField"));
    
    private boolean canHaveReferences;
    private DcTable table;
    private DcTable tableSysFields;
    
    private final boolean update;
    
    public PanelFields(Wizard wizard, boolean update) {
        super(wizard);
        
        this.update = update;
        table = ComponentFactory.getDCTable(true, false);
        tableSysFields = ComponentFactory.getDCTable(true, false);
        build();
    }
    
    @Override
    public void setModule(XmlModule module) {
        super.setModule(module);
        canHaveReferences = !getModule().getModuleClass().equals(DcPropertyModule.class); 
        
        addDefaultFields();
        
        table.clear();
        for (XmlField field : module.getFields()) 
            addFieldToTable(field);
    }
    
    @Override
    public Object apply() {
        XmlModule module = getModule();

        Collection<XmlField> fields = new ArrayList<XmlField>();
        XmlField field;
        for (int i = 0; i < table.getRowCount(); i++) {
            field = (XmlField) table.getValueAt(i, 0);
            fields.add(field);
        }
        
        module.setFields(fields);
        
        return module;
    }
    
    public void addFieldToTable(XmlField field) {
        table.addRow(new Object[] {field, 
                update ? Boolean.valueOf(field.isOverwritable()) : true, 
                update ? Boolean.valueOf(field.canBeConverted()) : true});
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgModuleFields");
    }
    
    private Collection<String> getCurrentFieldNames() {
        Collection<String> names = new ArrayList<String>();
        XmlField xmlField;
        for (int i = 0; i < table.getRowCount(); i++) {
            xmlField = (XmlField) table.getValueAt(i, 0);
            names.add(xmlField.getColumn());
        }

        DcField field;
        for (int i = 0; i < tableSysFields.getRowCount(); i++) {
            field = (DcField) tableSysFields.getValueAt(i, 0);
            names.add(field.getDatabaseFieldName());
            names.add(field.getLabel());
        }
        
        return names;
    }
    
    private void createField() {
        DefineFieldDialog dlg = new DefineFieldDialog(getModule().getIndex(),
                                                      getWizard(), 
                                                      null, 
                                                      getCurrentFieldNames(), 
                                                      canHaveReferences,
                                                      update);
        dlg.setVisible(true);
        
        XmlField field = dlg.getField();
        if (field != null)
            addFieldToTable(field);
        
        revalidate();
        repaint();
    }

    private void alterField() {
        int row = table.getSelectedRow(); 
        if (row == -1) return;
        
        XmlField oldField = (XmlField) table.getValueAt(table.getSelectedRow(), 0);
        
        if (oldField.canBeConverted() ||
            DcModules.get(getModule().getIndex()) == null || // module does not exist
            DcModules.get(getModule().getIndex()).getField(oldField.getIndex()) == null) {
            
            DefineFieldDialog dlg = new DefineFieldDialog(getModule().getIndex(),
                                                          getWizard(), 
                                                          oldField,
                                                          getCurrentFieldNames(), 
                                                          canHaveReferences,
                                                          update);
            dlg.setVisible(true);
            
            XmlField newField = dlg.getField();
            if (newField != null && !dlg.isCanceled()) {
                table.removeRow(table.getSelectedIndex());
                addFieldToTable(newField);
                table.setSelected(table.getColumnCount() - 1);
            }
            
            revalidate();
            repaint();
        } else {
            GUI.getInstance().displayWarningMessage("msgFieldCannotBeAltered");
            return;
        }
    }
    
    private Collection<DcField> getDefaultFields() {
        XmlModule xmlModule = getModule();

        DcModule module = null;
        if (xmlModule.getModuleClass() != null) { 
            if (xmlModule.getModuleClass().equals(DcMediaModule.class))
                module = new DcMediaModule(10, false, "", "", "", "", "", "");
            else if (xmlModule.getModuleClass().equals(DcPropertyModule.class))
                module = new DcPropertyModule(10, "", "", "", "", "");
            else if (xmlModule.getModuleClass().equals(DcAssociateModule.class))
                module = new DcAssociateModule(10, "", "", "", "", "", "");
        }
        
        return module != null ? module.getFields() : new ArrayList<DcField>();
    }
    
    private void addDefaultFields() {
        tableSysFields.clear();
        for (DcField field : getDefaultFields())
            tableSysFields.addRow(new Object[] {field});
    }

    @Override
    public void destroy() {
        buttonAdd = null;
        buttonRemove = null;
        buttonAlter = null;
        
        if (table != null) {
            table.clear();
            table = null;
        }

        if (tableSysFields != null) {
            tableSysFields.clear();
            tableSysFields = null;
        }
    }
    
    private void delete() {
        if (table.getSelectedRow() == -1)
            return;
        
        if (update) {
            XmlField field = (XmlField) table.getValueAt(table.getSelectedRow(), 0);
            
            if (field.isOverwritable()) {
                if (GUI.getInstance().displayQuestion("msgDeleteField"))
                    table.removeRow(table.getSelectedRow());
            } else {
                GUI.getInstance().displayWarningMessage("msgFieldCannotBeRemoved");
                return;
            }
        } else {
            table.removeRow(table.getSelectedRow());
        }
    }
    
    private void build() {
        // Actions
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("createField");
        buttonRemove.addActionListener(this);
        buttonRemove.setActionCommand("delete");
        buttonAlter.addActionListener(this);
        buttonAlter.setActionCommand("alterField");
        
        panelActions.add(buttonRemove);
        panelActions.add(buttonAdd);
        panelActions.add(buttonAlter);
        
        // Fields
        JPanel panelFields = new JPanel();
        panelFields.setLayout(Layout.getGBL());

        DcLabel labelPredefined = ComponentFactory.getLabel(DcResources.getText("lblPredefinedFields"));
        DcLabel lebelFields = ComponentFactory.getLabel(update ? DcResources.getText("lblFields") : DcResources.getText("lblNewFields"));
        
        tableSysFields.setColumnCount(1);
        TableColumn colSysField = tableSysFields.getColumnModel().getColumn(0);
        colSysField.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));
        colSysField.setHeaderValue(DcResources.getText("lblName"));

        JScrollPane scrollerSysFields = new JScrollPane(tableSysFields);
        scrollerSysFields.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollerSysFields.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        
        table.setColumnCount(3);
        TableColumn colField = table.getColumnModel().getColumn(0);
        JTextField textField = ComponentFactory.getTextFieldDisabled();
        colField.setCellEditor(new DefaultCellEditor(textField));
        colField.setHeaderValue(DcResources.getText("lblName"));

        TableColumn colCanRemove = table.getColumnModel().getColumn(1);
        
        JCheckBox cb1 = ComponentFactory.getCheckBox("");
        cb1.setEnabled(false);
        colCanRemove.setCellEditor(new DefaultCellEditor(cb1));
        colCanRemove.setHeaderValue(DcResources.getText("lblCanBeRemoved"));
        colCanRemove.setCellRenderer(CheckBoxTableCellRenderer.getInstance());

        JCheckBox cb2 = ComponentFactory.getCheckBox("");
        cb2.setEnabled(false);
        TableColumn colCanChange = table.getColumnModel().getColumn(2);
        colCanChange.setCellEditor(new DefaultCellEditor(cb2));
        colCanChange.setHeaderValue(DcResources.getText("lblCanBeAltered"));
        colCanChange.setCellRenderer(CheckBoxTableCellRenderer.getInstance());
        
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        
        panelFields.add(labelPredefined, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panelFields.add(scrollerSysFields, Layout.getGBC(0, 1, 1, 1, 50.0, 50.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        panelFields.add(lebelFields, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 0, 0));
        panelFields.add(scroller, Layout.getGBC(0, 3, 1, 4, 50.0, 50.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        table.applyHeaders();
        tableSysFields.applyHeaders();
        
        setLayout(Layout.getGBL());
        add(panelFields,    Layout.getGBC(0, 1, 1, 1, 50.0, 50.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0)); 
        add(panelActions,   Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0)); 
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("createField"))
            createField();
        else if (ae.getActionCommand().equals("alterField"))
            alterField();
        else if (ae.getActionCommand().equals("delete"))
            delete();
        else if (ae.getActionCommand().equals("moveUp"))
            table.moveRowUp();
        else if (ae.getActionCommand().equals("moveDown"))
            table.moveRowDown();
    }
}