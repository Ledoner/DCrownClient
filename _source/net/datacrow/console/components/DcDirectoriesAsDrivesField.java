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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.console.windows.BrowserDialog;
import net.datacrow.core.resources.DcResources;

public class DcDirectoriesAsDrivesField extends JComponent implements IComponent, ActionListener {
        
	private DcLongTextField textHelp = ComponentFactory.getHelpTextField();
    private DcTable tableDirectoriesAsDrives = ComponentFactory.getDCTable(true, false);
    private JButton buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAdd"));
    private JButton buttonRemove = ComponentFactory.getButton(DcResources.getText("lblRemove"));

    /**
     * Initializes this field
     */
    public DcDirectoriesAsDrivesField() {
        buildComponent();
    }
    
    @Override
    public void setFont(Font font) {
    	textHelp.setFont(ComponentFactory.getStandardFont());
        tableDirectoriesAsDrives.setFont(ComponentFactory.getStandardFont());
        buttonAdd.setFont(ComponentFactory.getSystemFont());
        buttonRemove.setFont(ComponentFactory.getSystemFont());
    }    
    
    @Override
    public void clear() {
        textHelp = null;
        tableDirectoriesAsDrives = null;
        buttonAdd = null;
        buttonRemove = null;
    }     
    
    @Override
    public Object getValue() {
    	int nbDirs = tableDirectoriesAsDrives.getRowCount();
        String[] dirs = new String[nbDirs];
        for (int i = 0; i < nbDirs; i++) {
            dirs[i] = (String) tableDirectoriesAsDrives.getValueAt(i, 0);
        }
        return dirs;
    }
    
    /**
     * Applies a value to this field
     */
    @Override
    public void setValue(Object o) {
        if (o == null) return;
        for (String dir : (String[])o) {
        	tableDirectoriesAsDrives.addRow(new Object[] {dir});
        }
    }
    
    /**
     * Builds this component
     */
    protected void buildComponent() {
        setLayout(Layout.getGBL());
        
        // directories
        JPanel panelDirs = new JPanel();
        panelDirs.setLayout(Layout.getGBL());

        textHelp.setText(DcResources.getText("msgDirectoriesAsDrivesHelp"));
        textHelp.setPreferredSize(new Dimension(100, 30));
        textHelp.setMinimumSize(new Dimension(100, 30));
        textHelp.setMaximumSize(new Dimension(800, 30));
        add(textHelp, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));

        tableDirectoriesAsDrives.setColumnCount(1);
        TableColumn columnDir = tableDirectoriesAsDrives.getColumnModel().getColumn(0);
        JTextField textField = ComponentFactory.getTextFieldDisabled();
        columnDir.setCellEditor(new DefaultCellEditor(textField));
        columnDir.setHeaderValue(DcResources.getText("lblDirectory"));
        
        JPanel panelActions = new JPanel();
        
        buttonAdd.addActionListener(this);
        buttonRemove.addActionListener(this);
        buttonAdd.setActionCommand("addDirectory");
        buttonRemove.setActionCommand("removeDirectory");
        
        panelActions.add(buttonAdd);
        panelActions.add(buttonRemove);
        
        panelDirs.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblDirectories")));        
        panelDirs.add(new JScrollPane(tableDirectoriesAsDrives), 
                Layout.getGBC(0, 0, 1, 1, 10.0, 10.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        panelDirs.add(panelActions, 
                Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        
        // main
        add(panelDirs, Layout.getGBC(0, 3, 1, 1, 30.0, 30.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        
        tableDirectoriesAsDrives.applyHeaders();
        
    }
    
    @Override
    public void setEditable(boolean b) {}
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("addDirectory")) {
            BrowserDialog dlg = new BrowserDialog(DcResources.getText("msgSelectDirectory"));
            File directory = dlg.showSelectDirectoryDialog(this, null);
            
            if (directory != null) { 
                tableDirectoriesAsDrives.addRow(new Object[] {directory.toString()});
            }
            
        } else if (e.getActionCommand().equals("removeDirectory")) {
            if (tableDirectoriesAsDrives.getSelectedRow() != -1) {
                for (int i = tableDirectoriesAsDrives.getSelectedRows().length; i > 0; i--)
                    tableDirectoriesAsDrives.removeRow(tableDirectoriesAsDrives.getSelectedRows()[i - 1]);
            } else {
                GUI.getInstance().displayMessage("msgSelectItemBeforeDelete");
            }
        }
    }
    
    @Override
    public void refresh() {}
}