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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.definitions.ProgramDefinition;
import net.datacrow.settings.definitions.ProgramDefinitions;

public class DcProgramDefinitionsField extends JComponent implements IComponent, ActionListener, MouseListener {
	    
    private DcTable programTable = ComponentFactory.getDCTable(true, false);
	private DcShortTextField extensionField = ComponentFactory.getShortTextField(10);
	private DcFileField programField = ComponentFactory.getFileField(false, false);
	private DcShortTextField parametersFields = ComponentFactory.getShortTextField(255);
	
    private JLabel labelExtention = ComponentFactory.getLabel(DcResources.getText("lblFileExtension"));
    private JLabel labelProgram = ComponentFactory.getLabel(DcResources.getText("lblProgram"));
    private JLabel labelParameters = ComponentFactory.getLabel(DcResources.getText("lblParameters"));
    
    private JButton buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAdd"));
    private JButton buttonRemove = ComponentFactory.getButton(DcResources.getText("lblRemove"));
    

    /**
     * Initializes this field
     */
    public DcProgramDefinitionsField() {
        buildComponent();
    }
    
    @Override
    public void setFont(Font font) {
        extensionField.setFont(ComponentFactory.getStandardFont());
        programField.setFont(ComponentFactory.getStandardFont());
        
        labelExtention.setFont(ComponentFactory.getSystemFont());
        labelProgram.setFont(ComponentFactory.getSystemFont());
        buttonAdd.setFont(ComponentFactory.getSystemFont());
        buttonRemove.setFont(ComponentFactory.getSystemFont());
    }    
    
    public ProgramDefinitions getDefinitions() {
        return (ProgramDefinitions) getValue();
    }
    
    @Override
    public void clear() {
        programTable.clear();
        programTable = null;
        extensionField = null;
        programField = null;
        
        labelExtention = null;
        labelProgram = null;
        buttonAdd = null;
        buttonRemove = null;
    }     
    
    /**
     * Returns the selected Font (with the chosen size, thickness)
     * Unless the user has chosen otherwise, Arial font size 11 is returned.
     */
    @Override
    public Object getValue() {
    	ProgramDefinitions definitions = new ProgramDefinitions();
    	String extension;
    	String program;
    	String parameters;
        for (int i = 0; i < programTable.getRowCount(); i++) {
            extension = (String) programTable.getValueAt(i, 0, true);
            program = (String) programTable.getValueAt(i, 1, true);
            parameters = (String) programTable.getValueAt(i, 2, true);
            
            ProgramDefinition definition = new ProgramDefinition(extension, program, parameters);
    		definitions.add(definition);
    	}

        return definitions;
    }
    
    /**
     * Applies a value to this field
     */
    @Override
    public void setValue(Object o) {
    	if (o instanceof ProgramDefinitions) {
    		ProgramDefinitions definitions = (ProgramDefinitions) o;
    		Object[] row;
    		for (ProgramDefinition definition : definitions.getDefinitions()) {
                row = new Object[] {definition.getExtension(), definition.getProgram(), definition.getParameters()}; 
                programTable.addRow(row);
    		}
    	}
    }
    
    private void remove() {
        int row = programTable.getSelectedRow();
        if (row > -1)
            programTable.removeRow(row);
    }
    
    private void edit() {
        int row = programTable.getSelectedRow();
        if (row > -1) {
            String extension = (String) programTable.getValueAt(row, 0, true);
            String program = (String) programTable.getValueAt(row, 1, true);
            String parameters = (String) programTable.getValueAt(row, 2, true);
            
            extensionField.setText(extension);
            programField.setValue(program);
            parametersFields.setText(parameters);
            
            programTable.cancelEdit();
            remove();
        }
    }
    
    private void addDefinition(String extension, String program, String parameters) {
        if (extension.trim().length() > 0 && program.trim().length() > 0) {
            ProgramDefinitions definitions = getDefinitions();

            if (definitions.getDefinition(extension) != null) {
                GUI.getInstance().displayWarningMessage("msgProgramAlreadyDefined");
            } else {
                Object[] row = {extension, program, parameters}; 
                programTable.addRow(row);

                extensionField.setText("");
                parametersFields.setText("");
                programField.setValue(null);
            }
        } else {
            GUI.getInstance().displayWarningMessage("msgFileOrExtensionNotFilled");
        }
    }    
    
    /**
     * Builds this component
     */
    private void buildComponent() {
        setLayout(Layout.getGBL());
        
        //**********************************************************
        //Input panel
        //**********************************************************          
        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());

        panelInput.add(labelExtention,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 1, 0, 0, 5), 0, 0));
        panelInput.add(labelProgram,    Layout.getGBC( 1, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));        
        panelInput.add(extensionField,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 1, 0, 0, 5), 0, 0));
        panelInput.add(programField,    Layout.getGBC( 1, 1, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0)); 
        
        
        panelInput.add(labelParameters,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 1, 0, 0, 5), 0, 0));
        panelInput.add(parametersFields, Layout.getGBC( 1, 2, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0)); 

        //**********************************************************
        //Action panel
        //**********************************************************   
        JPanel panelActions = new JPanel();
        panelActions.setLayout(Layout.getGBL());
        
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("add");

        buttonRemove.addActionListener(this);
        buttonRemove.setActionCommand("remove");
        
        panelActions.add(buttonAdd,  	Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                		,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                		 new Insets( 0, 0, 0, 5), 0, 0));
        panelActions.add(buttonRemove,  Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
		        		,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		        		 new Insets( 0, 0, 0, 0), 0, 0));
        
        //**********************************************************
        //Defined Programs List
        //**********************************************************           
        JScrollPane scroller = new JScrollPane(programTable);
        programTable.addMouseListener(this);
        programTable.setColumnCount(3);

        TableColumn columnExtension = programTable.getColumnModel().getColumn(0);
        columnExtension.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));       
        columnExtension.setHeaderValue(DcResources.getText("lblFileExtension"));
        
        TableColumn columnProgram = programTable.getColumnModel().getColumn(1);
        columnProgram.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));
        columnProgram.setHeaderValue(DcResources.getText("lblProgram"));
        
        TableColumn columnParameters = programTable.getColumnModel().getColumn(2);
        columnParameters.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));
        columnParameters.setHeaderValue(DcResources.getText("lblParameters"));
        
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);        
        
        programTable.applyHeaders();
        
        //**********************************************************
        //Main panel
        //**********************************************************
        
        add(panelInput,      Layout.getGBC( 0, 0, 2, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets( 0, 0, 0, 0), 0, 0));        
        add(panelActions,    Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                			,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                			 new Insets( 0, 0, 0, 0), 0, 0));        
        add(scroller,    	 Layout.getGBC( 0, 2, 2, 1, 20.0, 20.0
                			,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                			 new Insets( 0, 0, 0, 0), 0, 0));
    }
    
    @Override
    public void setEditable(boolean b) {}
    
	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("add"))
            addDefinition(extensionField.getText(), programField.getFilename(), parametersFields.getText());
        else if (e.getActionCommand().equals("remove"))
            remove();
	}
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            edit();
        }            
    }
    
    @Override
    public void refresh() {}
}