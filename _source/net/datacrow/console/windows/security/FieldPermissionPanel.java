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

package net.datacrow.console.windows.security;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcMenuBar;
import net.datacrow.console.components.renderers.CheckBoxTableCellRenderer;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.objects.helpers.Permission;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class FieldPermissionPanel extends JPanel implements ActionListener {

    private static Logger logger = Logger.getLogger(FieldPermissionPanel.class.getName());
    
    private static final int _COLUMN_MODULE = 0;
    private static final int _COLUMN_LABEL = 1;
    private static final int _COLUMN_VIEW = 2;
    private static final int _COLUMN_EDIT = 3;
    private static final int _COLUMN_PERMISSION = 4;
    
    private DcModule module;
    private DcTable table;
    private DcObject user;
    
    private ModulePermissionMenu menu;
    
    private boolean update;
    
    public FieldPermissionPanel(DcModule module, DcObject user, boolean update) {
        this.user = user;
        this.update = update;
        this.module = module;
        this.menu = new ModulePermissionMenu(this);
        
        build();
        initialize();
    }
    
    public boolean isChanged() {
        for (int row = 0; row < table.getRowCount(); row++) {
            if (((Permission) table.getValueAt(row, _COLUMN_PERMISSION, true)).isChanged())
                return true;
        }
        return false;
    }
    
    private void initialize() {
        user.loadChildren(null);

        DcObject permission = null;
        Permission p;
        
        Connector connector = DcConfig.getInstance().getConnector();
        for (DcField field : module.getFields()) {
            
            permission = null;
            
            if (field.getIndex() == DcObject._ID) continue;
            
            if (update) {
                for (DcObject child : user.getChildren()) {
                    p = (Permission) child;
                    if (p.getFieldIdx() == field.getIndex() && p.getPermittedModuleIdx() == module.getIndex()) {
                        permission = p;
                        break;
                    }
                }
            }
            
            if (permission == null) {
                permission = DcModules.get(DcModules._PERMISSION).getItem();
                permission.setIDs();
                permission.setValueLowLevel(Permission._B_FIELD, Long.valueOf(field.getIndex()));
                permission.setValue(Permission._C_MODULE, Long.valueOf(field.getModule()));
                permission.setValue(Permission._D_VIEW, Boolean.TRUE);
                permission.setValue(Permission._E_EDIT, Boolean.FALSE);
                permission.setValue(Permission._F_USER, user);
                
                if (update) {
                    // create the missing permission
                    try {
                    	connector.saveItem(permission);
                    } catch (ValidationException ve) {
                        logger.error(ve, ve);
                    }
                } 
            }
            
            Object[] row = new Object[] {DcModules.get(field.getModule()), 
                                         field.getLabel(), 
                                         permission.getValue(Permission._D_VIEW), 
                                         permission.getValue(Permission._E_EDIT), 
                                         permission};
            table.addRow(row);
            permission.markAsUnchanged();
        }
    }
    
    public void setMenuBar(JMenuBar mnu) {
        add(mnu, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));     
    }
    
    public void clear() {
        table.clear();
        table = null;
        user = null;
        module = null;
        
        menu.clear();
        menu = null;
    }

    private void build() {
        setLayout(Layout.getGBL());

        table = ComponentFactory.getDCTable(false, false);
        table.setColumnCount(5);

        TableColumn cModule = table.getColumnModel().getColumn(_COLUMN_MODULE);
        cModule.setHeaderValue(DcResources.getText("lblModule"));
        
        TableColumn cLabel = table.getColumnModel().getColumn(_COLUMN_LABEL);
        cLabel.setHeaderValue(DcResources.getText("lblField"));

        TableColumn cView = table.getColumnModel().getColumn(_COLUMN_VIEW);
        JCheckBox cbView = new JCheckBox();
        cbView.addActionListener(this);
        cbView.setActionCommand("applyRight");
        cView.setCellEditor(new DefaultCellEditor(cbView));
        cView.setCellRenderer(CheckBoxTableCellRenderer.getInstance());
        cView.setHeaderValue(DcResources.getText("lblViewRights"));

        TableColumn cEdit = table.getColumnModel().getColumn(_COLUMN_EDIT);
        JCheckBox cbEdit = new JCheckBox();
        cEdit.setCellEditor(new DefaultCellEditor(cbEdit));
        cbEdit.addActionListener(this);
        cbEdit.setActionCommand("applyRight");
        cEdit.setCellRenderer(CheckBoxTableCellRenderer.getInstance());
        cEdit.setHeaderValue(DcResources.getText("lblEditRights"));

        TableColumn cHidden = table.getColumnModel().getColumn(_COLUMN_PERMISSION);
        
        table.removeColumn(cHidden);
        table.removeColumn(cModule);
        
        // table
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        setMenuBar(menu);
        
        add(scroller,  Layout.getGBC(0, 1, 1, 1, 80.0, 80.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        
        table.applyHeaders();
    }

    private void applyRight() {
        table.cancelEdit();
        int row = table.getSelectedRow();
        
        if (row > -1) {
            boolean view = ((Boolean) table.getValueAt(row, _COLUMN_VIEW, true)).booleanValue();
            boolean edit = ((Boolean) table.getValueAt(row, _COLUMN_EDIT, true)).booleanValue();
            
            if (edit && !view) 
            	table.getDcModel().setValueAt(Boolean.TRUE, row, _COLUMN_VIEW);
            else if (!view) 
            	table.getDcModel().setValueAt(Boolean.FALSE, row, _COLUMN_EDIT);
            
            Permission permission = (Permission) table.getValueAt(row, _COLUMN_PERMISSION, true);
            permission.setValue(Permission._D_VIEW, table.getValueAt(row, _COLUMN_VIEW, true));
            permission.setValue(Permission._E_EDIT, table.getValueAt(row, _COLUMN_EDIT, true));
        }
    }

    public Collection<Permission> getPermissions() {
        Collection<Permission> permissions = new ArrayList<Permission>();
        Permission permission;
        for (int row = 0; row < table.getRowCount(); row++) {
            permission = (Permission) table.getValueAt(row, _COLUMN_PERMISSION, true);
            
            if (!permission.isFilled(Permission._F_USER))
            	permission.setValue(Permission._F_USER, user);
            	
            permissions.add(permission);
        }    
        return permissions;
    }
    
    @Override
    public void setEnabled(boolean b) {
        table.setEnabled(b);
    }
    
    protected void denyAll() {
        for (int row = 0; row < table.getRowCount(); row++) {
            table.getModel().setValueAt(Boolean.FALSE, row, _COLUMN_VIEW);
            table.getModel().setValueAt(Boolean.FALSE, row, _COLUMN_EDIT);
            table.setSelected(row);
            applyRight();
        }
    }
    
    protected void enableAll(int idx) {
        for (int row = 0; row < table.getRowCount(); row++) {
        	table.getModel().setValueAt(Boolean.TRUE, row, idx);
            table.setSelected(row);
            applyRight();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("applyRight"))
            applyRight();
    }
    
    private static class ModulePermissionMenu extends DcMenuBar implements ActionListener {

    	private FieldPermissionPanel panel;
    	
    	protected ModulePermissionMenu(FieldPermissionPanel panel) {
    		this.panel = panel;
    		
    		JMenu mnuEdit = ComponentFactory.getMenu(DcResources.getText("lblEdit"));
    		
    		JMenuItem miAllowViewAll = ComponentFactory.getMenuItem(DcResources.getText("lblAllowViewAll"));
    		JMenuItem miAllowEditAll = ComponentFactory.getMenuItem(DcResources.getText("lblAllowEditAll"));
    		JMenuItem miDenyAccess = ComponentFactory.getMenuItem(DcResources.getText("lblDenyModuleAccess"));
    		
    		miAllowViewAll.setActionCommand("viewAll");
    		miAllowEditAll.setActionCommand("editAll");
    		miDenyAccess.setActionCommand("denyAll");
    		
    		miAllowViewAll.addActionListener(this);
    		miAllowEditAll.addActionListener(this);
    		miDenyAccess.addActionListener(this);
    		
    		mnuEdit.add(miAllowViewAll);
    		mnuEdit.add(miAllowEditAll);
    		mnuEdit.add(miDenyAccess);
    		
    		add(mnuEdit);
    	}
    	
    	public void clear() {
    		panel = null;
    	}
    	
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getActionCommand().equals("viewAll"))
                panel.enableAll(_COLUMN_VIEW);
            else if (ae.getActionCommand().equals("editAll"))
            	panel.enableAll(_COLUMN_EDIT);
            else if (ae.getActionCommand().equals("denyAll"))
            	panel.denyAll();
        }
    }
}
