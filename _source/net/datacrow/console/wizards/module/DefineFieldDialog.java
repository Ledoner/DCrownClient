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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcNumberField;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.definitions.DcFieldDefinition;

public class DefineFieldDialog extends DcDialog implements ActionListener {
    
    private boolean canHaveReferences;
    
    private DcShortTextField textName;
    private DcCheckBox checkSearchable;
    private DcNumberField numberMaxLength;

    private JComboBox comboTabs;
    private DcCheckBox checkRequired;
    private DcCheckBox checkDescriptive;
    private DcCheckBox checkKeyField;
    
    private JLabel lblReferences = ComponentFactory.getLabel(DcResources.getText("lblReferencedModule"));
    private JLabel lblMaxLength = ComponentFactory.getLabel(DcResources.getText("lblMaxTextLength"));
    
    private JComboBox comboReference;
    private JComboBox comboFieldType;
    
    private Collection<String> excludedNames;
    private XmlField field;
    private int module;

    boolean canceled = false;
    boolean existingField = true;
    boolean update;
    
    public DefineFieldDialog(int module,
                             Wizard parent,
                             XmlField oldField,
                             Collection<String> excludedNames, 
                             boolean canHaveReferences,
                             boolean update) {
        
        super(parent);
        
        this.update = update;
        this.module = module;
        this.canHaveReferences = canHaveReferences;
        this.field = oldField;
        this.existingField = oldField != null;
        this.excludedNames = excludedNames;
        
        this.setModal(true);
        this.setTitle(DcResources.getText("lblDefineField"));
        setHelpIndex("dc.modules.fields");
        
        build();
        applyField();
        
        setResizable(false);
        pack();
        setSize(new Dimension(500, 400));
        setCenteredLocation();
    }
    
    public boolean isCanceled() {
        return canceled;
    }

    private void applyField() {
        
        if (field == null) return;

        textName.setText(field.getName());
        
        checkSearchable.setSelected(field.isSearchable());
        numberMaxLength.setValue(field.getMaximumLength());
        
        FieldType ft;
        for (int i = 0; i < comboFieldType.getItemCount(); i++) {
            ft = (FieldType) comboFieldType.getItemAt(i);
            if (ft.getIndex() == field.getFieldType()) {
                comboFieldType.setSelectedItem(ft);
                break;
            }
        }

        if (field.getFieldType() == ComponentFactory._REFERENCESFIELD ||
            field.getFieldType() == ComponentFactory._REFERENCEFIELD) {
            DcModule m;
            for (int i = 0; i < comboReference.getItemCount(); i++) {
                m = (DcModule) comboReference.getItemAt(i);
                if (m.getIndex() == field.getModuleReference())
                    comboReference.setSelectedIndex(i);
            }
        }
        
        if (field.getDefinition() != null) {
            checkDescriptive.setSelected(field.getDefinition().isDescriptive());
            checkKeyField.setSelected(field.getDefinition().isUnique());
            checkRequired.setSelected(field.getDefinition().isRequired());
            comboTabs.setSelectedItem(field.getDefinition().getTab());
        }
        
        if (existingField)
            textName.setEditable(false);
    }
    
    public XmlField getField() {
        return field;
    }
    
    private void createField() {
        field = field == null ? new XmlField() : field;
        
        String name = textName.getText();
        
        try {
            checkValue(name, DcResources.getText("lblName"));
            if (numberMaxLength.isVisible()) {
                Long value = (Long) numberMaxLength.getValue();
                if (value == null || value.longValue() <= 0)
                    checkValue(null, DcResources.getText("lblMaxTextLength"));
                else
                	field.setMaximumLength(value.intValue());
            }
            
            if (!existingField) {
                String column = CoreUtilities.getDatabaseColumnName();

                if (!existingField) {
                    for (String excludedName : excludedNames) {
                        if (excludedName != null && 
                            excludedName.toLowerCase().equals(column.toLowerCase()))
                            throw new WizardException(DcResources.getText("msgFieldWithSameNameExists"));
                    }
                }
                
                field.setColumn(column);
            }
            
            field.setName(name);
            field.setSearchable(checkSearchable.isSelected());
            
            FieldType ft = (FieldType) comboFieldType.getSelectedItem();
            if (ft.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                ft.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                
                DcModule m = (DcModule) comboReference.getSelectedItem();
                field.setModuleReference(m.getIndex());
                
                if (m.getXmlModule() != null)
                    m.getXmlModule().setHasDependingModules(true);
            }

            field.setUiOnly(ft.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                            ft.getValueType() == DcRepository.ValueTypes._PICTURE);
            
            field.setValueType(ft.getValueType());
            field.setFieldType(ft.getIndex());
            field.setOverwritable(true);
            
            if (field.getDefinition() == null) {
                field.setDefinition(new DcFieldDefinition(
                        field.getModule(),
                        field.getIndex(), 
                        null, 
                        true, 
                        checkRequired.isSelected(), 
                        checkDescriptive.isSelected(), 
                        checkKeyField.isSelected(), 
                        (String) comboTabs.getSelectedItem()));
            }
            
            close();
            
        } catch (WizardException we) {
        	field = null;
        	GUI.getInstance().displayWarningMessage(we.getMessage());
        }
    }
    
    private void checkValue(Object o, String desc) throws WizardException { 
        if (CoreUtilities.isEmpty(o))
            throw new WizardException(DcResources.getText("msgXNotEntered", desc));
    }
    
    @Override
    public void close() {
        textName = null;
        checkSearchable = null;
        numberMaxLength = null;
        comboReference = null;
        comboFieldType = null;

        super.close();
    }
    
    public void clear() {
        field = null;
    }
    
    private void applyFieldType() {
        FieldType ft = (FieldType) comboFieldType.getSelectedItem();
        
        if (ft.getIndex() == ComponentFactory._SHORTTEXTFIELD) {
            lblMaxLength.setVisible(true);
            numberMaxLength.setVisible(true);
            numberMaxLength.setValue(existingField && field.getMaximumLength() != 0 ? field.getMaximumLength() : 255);
        } else {
            lblMaxLength.setVisible(false);
            numberMaxLength.setVisible(false);
            numberMaxLength.setValue(existingField && field.getMaximumLength() != 0 ? field.getMaximumLength() : 255);
        }
        
        if (ft.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                ft.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
            lblReferences.setVisible(true);
            comboReference.setVisible(true);
        } else {
            lblReferences.setVisible(false);
            comboReference.setVisible(false);
        }
        
        boolean uiOnly = ft.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                         ft.getValueType() == DcRepository.ValueTypes._PICTURE;
        
        
        checkDescriptive.setToolTipText(DcResources.getText("tpFieldDescriptive"));
        checkDescriptive.setEnabled(true);
        checkKeyField.setToolTipText(DcResources.getText("tpFieldKey"));
        checkKeyField.setEnabled(true);
        checkRequired.setToolTipText(DcResources.getText("tpFieldRequired"));
        checkRequired.setEnabled(true);

        if (uiOnly) {
            checkKeyField.setEnabled(false);
            checkKeyField.setSelected(false);
            
            checkRequired.setEnabled(false);
            checkRequired.setSelected(false);

            if (ft.getValueType() == DcRepository.ValueTypes._PICTURE) {
                checkDescriptive.setEnabled(false);
                checkDescriptive.setSelected(false);
            }
        }
    }
    
    private void build() {
        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());
        
        textName = ComponentFactory.getShortTextField(255);
        numberMaxLength = ComponentFactory.getNumberField();

        checkSearchable = ComponentFactory.getCheckBox("");
        checkDescriptive = ComponentFactory.getCheckBox("");
        checkKeyField = ComponentFactory.getCheckBox("");
        checkRequired = ComponentFactory.getCheckBox("");

        comboTabs = ComponentFactory.getComboBox();
        comboReference = ComponentFactory.getComboBox();
        comboFieldType = ComponentFactory.getComboBox();
        
        checkDescriptive.setToolTipText(DcResources.getText("tpFieldDescriptive"));
        checkKeyField.setToolTipText(DcResources.getText("tpFieldKey"));
        checkRequired.setToolTipText(DcResources.getText("tpFieldRequired"));

        initializeTypes();
        
        DcLongTextField textHelp = ComponentFactory.getHelpTextField();
        textHelp.setText(DcResources.getText("msgHelpAterField"));
        textHelp.setPreferredSize(new Dimension(100, 60));
        textHelp.setMinimumSize(new Dimension(100, 60));
        textHelp.setMaximumSize(new Dimension(800, 60));

        int y = 0;
        panel.add(ComponentFactory.getLabel(DcResources.getText("lblName")),
                Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(textName,
                Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        
        panel.add(ComponentFactory.getLabel(DcResources.getText("lblFieldType")),
                Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(comboFieldType,
                Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        
        if (canHaveReferences) {
            panel.add(lblReferences,Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(comboReference,
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            
            List<DcModule> modules = new ArrayList<DcModule>();
            modules.addAll(DcModules.getPropertyBaseModules());
            for (DcModule module : DcModules.getModules()) {
                
                if (modules.contains(module))
                    continue;
                
                if (module.isServingMultipleModules()) {
                    modules.add(module);
 
                } else if (  
                    module.isTopModule() && !module.hasDependingModules() &&
                   !module.isParentModule() && !module.isChildModule() &&
                   !DcModules.isUsedInMapping(module.getIndex())) { 
                    
                    modules.add(module);
                }
            }

            Collections.sort(modules, new ModuleComparator());
            for (DcModule module : modules)
                comboReference.addItem(module);
        }
        
        panel.add(ComponentFactory.getLabel(DcResources.getText("lblSearchable")),
                Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(checkSearchable,
                Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        if (!update) {
            panel.add(ComponentFactory.getLabel(DcResources.getText("lblRequired")),
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(checkRequired,
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(ComponentFactory.getLabel(DcResources.getText("lblDescriptive")),
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(checkDescriptive,
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(ComponentFactory.getLabel(DcResources.getText("lblIsKey")),
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(checkKeyField,
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));        
            panel.add(ComponentFactory.getLabel(DcResources.getText("lblItemFormTab")),
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            panel.add(comboTabs,
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            
            comboTabs.addItem(DcResources.getText("lblSummary"));
            comboTabs.addItem(DcResources.getText("lblInformation"));
            comboTabs.addItem(DcResources.getText("lblTechnicalInfo"));
        }
        
        panel.add(lblMaxLength, Layout.getGBC(0, y, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(numberMaxLength, Layout.getGBC(1, y++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        
        
        checkSearchable.setSelected(true);
        
        comboFieldType.addActionListener(this);
        comboFieldType.setActionCommand("fieldTypeSelected");
        comboFieldType.setSelectedIndex(0);
        
        if (existingField && field.getFieldType() == ComponentFactory._REFERENCESFIELD)
            comboFieldType.setEnabled(false);
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        panelActions.add(buttonOk);
        panelActions.add(buttonClose);
        
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        buttonOk.addActionListener(this);
        buttonOk.setActionCommand("createField");
        
        getContentPane().setLayout(Layout.getGBL());
        
        if (existingField && update)
            add(textHelp, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        
        add(panel, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(panelActions, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    private void filterTypesForAlteration(List<FieldType> fieldTypes) {
        
        // new fields can change to anything they like
        if (field == null || field.isNew())
            return;
        
        List<FieldType> remove = new ArrayList<FieldType>();
        for (FieldType fieldType : fieldTypes) {
            if ( DcModules.get(module).getField(field.getIndex()) == null || // do not allow changing new fields!
                !field.canConvertTo(fieldType.getIndex(), fieldType.getValueType())) {
                remove.add(fieldType);
            }
        }
        
        fieldTypes.removeAll(remove);
    }
    
    private void initializeTypes() {

        List<FieldType> fieldTypes = new ArrayList<FieldType>();

        fieldTypes.add(new FieldType(ComponentFactory._CHECKBOX, 
                                     DcRepository.ValueTypes._BOOLEAN, 
                                     DcResources.getText("lblCheckbox")));
        fieldTypes.add(new FieldType(ComponentFactory._DATEFIELD, 
                                     DcRepository.ValueTypes._DATE, 
                                     DcResources.getText("lblDateField")));
        fieldTypes.add(new FieldType(ComponentFactory._FILEFIELD, 
                                     DcRepository.ValueTypes._STRING, 
                                     DcResources.getText("lblFileField")));
        fieldTypes.add(new FieldType(ComponentFactory._FILELAUNCHFIELD, 
                                     DcRepository.ValueTypes._STRING, 
                                     DcResources.getText("lblFileLauncherField")));
        fieldTypes.add(new FieldType(ComponentFactory._LONGTEXTFIELD, 
                                     DcRepository.ValueTypes._STRING, 
                                     DcResources.getText("lblLongTextField")));
        fieldTypes.add(new FieldType(ComponentFactory._NUMBERFIELD, 
                                     DcRepository.ValueTypes._LONG, 
                                     DcResources.getText("lblNumberField")));
        if (canHaveReferences) {
            fieldTypes.add(new FieldType(ComponentFactory._PICTUREFIELD, 
                                         DcRepository.ValueTypes._PICTURE, 
                                         DcResources.getText("lblPictureField")));
        }
        
        fieldTypes.add(new FieldType(ComponentFactory._RATINGCOMBOBOX, 
                                     DcRepository.ValueTypes._LONG, 
                                     DcResources.getText("lblRatingField")));
        fieldTypes.add(new FieldType(ComponentFactory._SHORTTEXTFIELD, 
                                     DcRepository.ValueTypes._STRING, 
                                     DcResources.getText("lblShortTextField")));
        fieldTypes.add(new FieldType(ComponentFactory._URLFIELD, 
                                     DcRepository.ValueTypes._STRING, 
                                     DcResources.getText("lblUrlField")));
        fieldTypes.add(new FieldType(ComponentFactory._DECIMALFIELD, 
                                     DcRepository.ValueTypes._DOUBLE, 
                                     DcResources.getText("lblDecimalField")));
            
        if (canHaveReferences) {
            fieldTypes.add(new FieldType(ComponentFactory._REFERENCESFIELD, 
                                         DcRepository.ValueTypes._DCOBJECTCOLLECTION, 
                                         DcResources.getText("lblMultiReferenceField")));
            fieldTypes.add(new FieldType(ComponentFactory._REFERENCEFIELD, 
                                         DcRepository.ValueTypes._DCOBJECTREFERENCE, 
                                         DcResources.getText("lblSingleReferenceField")));
        }            
        
        if (existingField)
            filterTypesForAlteration(fieldTypes);
        
        Collections.sort(fieldTypes, new FieldTypeComparator());
        
        for (FieldType ft : fieldTypes) 
            comboFieldType.addItem(ft);
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("fieldTypeSelected")) {
            applyFieldType();
        } else if (e.getActionCommand().equals("close")) {
            canceled = true;
            close();
        } else if (e.getActionCommand().equals("createField")) {
            createField();
        }
    }
    
    private static class FieldType {
        private int index;
        private int valueType;
        private String name;
        
        public FieldType(int index, int valueType, String name) {
            this.index = index;
            this.name = name;
            this.valueType = valueType;
        }

        @Override
        public String toString() {
            return getName();
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public int getValueType() {
            return valueType;
        }
    }

    private static class FieldTypeComparator implements Comparator<FieldType>, Serializable {
        @Override
        public int compare(FieldType ft1, FieldType ft2) {
            return ft1.getName().compareTo(ft2.getName());
        }
    }
    
    private static class ModuleComparator implements Comparator<DcModule>, Serializable {
        @Override
        public int compare(DcModule m1, DcModule m2) {
            return m1.getLabel().compareTo(m2.getLabel());
        }
    }
}