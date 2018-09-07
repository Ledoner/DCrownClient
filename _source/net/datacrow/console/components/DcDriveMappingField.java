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
import java.util.StringTokenizer;

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
import net.datacrow.core.utilities.CoreUtilities;

public class DcDriveMappingField extends JComponent implements IComponent, ActionListener {
        
    private DcTable mappingTable = ComponentFactory.getDCTable(true, false);
    private DcShortTextField fldDrive = ComponentFactory.getShortTextField(255);
    private DcShortTextField fldMapsTo = ComponentFactory.getShortTextField(255);
    
    private JLabel lblDriveLetter = ComponentFactory.getLabel(DcResources.getText("lblDriveLetter"));
    private JLabel lblMapsTo = ComponentFactory.getLabel(DcResources.getText("lblMapsTo"));
    private JButton buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAdd"));
    private JButton buttonRemove = ComponentFactory.getButton(DcResources.getText("lblRemove"));

    /**
     * Initializes this field
     */
    public DcDriveMappingField() {
        buildComponent();
    }
    
    @Override
    public void setFont(Font font) {
        fldDrive.setFont(ComponentFactory.getStandardFont());
        fldMapsTo.setFont(ComponentFactory.getStandardFont());
        
        lblDriveLetter.setFont(ComponentFactory.getSystemFont());
        lblMapsTo.setFont(ComponentFactory.getSystemFont());
        buttonAdd.setFont(ComponentFactory.getSystemFont());
        buttonRemove.setFont(ComponentFactory.getSystemFont());
    }    
    
    @Override
    public void clear() {
        mappingTable.clear();
        mappingTable = null;
        fldDrive = null;
        fldMapsTo = null;
        lblDriveLetter = null;
        lblMapsTo = null;
        buttonAdd = null;
        buttonRemove = null;
    }     
    
    /**
     * Returns the selected Font (with the chosen size, thickness)
     * Unless the user has chosen otherwise, Arial font size 11 is returned.
     */
    @Override
    public Object getValue() {
        String[] mappings = new String[mappingTable.getRowCount()];
        for (int row = 0; row < mappingTable.getRowCount(); row++) 
            mappings[row] = mappingTable.getValueAt(row, 0) + "/&/" +  mappingTable.getValueAt(row, 1);

        return mappings;
    }
    
    /**
     * Applies a value to this field
     */
    @Override
    public void setValue(Object o) {
        if (o == null) return;
        
        String[] values = (String[]) o;
        StringTokenizer st;
        String[] row;
        for (String value : values) {
            st = new StringTokenizer(value, "/&/");
            row = new String[2];
            row[0] = (String) st.nextElement();
            row[1] = (String) st.nextElement();
            mappingTable.addRow(row);
        }
    }
    
    private void remove() {
        int row = mappingTable.getSelectedRow();
        if (row > -1)
            mappingTable.removeRow(row);
    }
    
    private void addMapping(String drive, String mapsTo) {
        if (CoreUtilities.isEmpty(drive)) {
            GUI.getInstance().displayMessage(DcResources.getText("msgEnterValueFor", DcResources.getText("lblDriveLetter")));
        } else if (CoreUtilities.isEmpty(mapsTo)) {
            GUI.getInstance().displayMessage(DcResources.getText("msgEnterValueFor", DcResources.getText("lblMapsTo")));
        } else {
            mappingTable.addRow(new String[] {drive, mapsTo});
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

        panelInput.add(lblDriveLetter,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 1, 0, 0, 5), 0, 0));
        panelInput.add(lblMapsTo,    Layout.getGBC( 1, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));        
        panelInput.add(fldDrive,  Layout.getGBC( 0, 1, 1, 1, 5.0, 5.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 1, 0, 0, 5), 0, 0));
        panelInput.add(fldMapsTo,    Layout.getGBC( 1, 1, 1, 1, 5.0, 5.0
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
        
        panelActions.add(buttonAdd,     Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                         new Insets( 0, 0, 0, 5), 0, 0));
        panelActions.add(buttonRemove,  Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                         new Insets( 0, 0, 0, 0), 0, 0));
        
        //**********************************************************
        //Defined Programs List
        //**********************************************************           
        JScrollPane scroller = new JScrollPane(mappingTable);
        mappingTable.setColumnCount(2);

        TableColumn columnExtension = mappingTable.getColumnModel().getColumn(0);
        columnExtension.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));       
        columnExtension.setHeaderValue(DcResources.getText("lblDriveLetter"));
        
        TableColumn columnProgram = mappingTable.getColumnModel().getColumn(1);
        columnProgram.setCellEditor(new DefaultCellEditor(ComponentFactory.getTextFieldDisabled()));
        columnProgram.setHeaderValue(DcResources.getText("lblMapsTo"));
        
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);        
        
        mappingTable.applyHeaders();
        
        
        //**********************************************************
        //Main panel
        //**********************************************************
        
        add(panelInput,      Layout.getGBC( 0, 0, 2, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                             new Insets( 0, 0, 0, 0), 0, 0));        
        add(panelActions,    Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                             new Insets( 0, 0, 0, 0), 0, 0));        
        add(scroller,        Layout.getGBC( 0, 2, 2, 1, 10.0, 10.0
                            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                             new Insets( 0, 0, 0, 0), 0, 0));
        
    }
    
    @Override
    public void setEditable(boolean b) {}
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("add"))
            addMapping(fldDrive.getText(), fldMapsTo.getText());
        else if (e.getActionCommand().equals("remove"))
            remove();
            
    }
    
    @Override
    public void refresh() {}
}