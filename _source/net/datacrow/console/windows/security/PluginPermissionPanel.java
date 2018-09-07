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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.renderers.CheckBoxTableCellRenderer;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.objects.helpers.Permission;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.core.plugin.RegisteredPlugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class PluginPermissionPanel extends JPanel implements ActionListener {

    private static Logger logger = Logger.getLogger(PluginPermissionPanel.class.getName());
    
    private static final int _COLUMN_LABEL = 0;
    private static final int _COLUMN_AUTHORIZED = 1;
    private static final int _COLUMN_PERMISSION = 2;
    
    private DcTable table;
    private DcObject user;
    
    private boolean update;
    
    public PluginPermissionPanel(DcObject user, boolean update) {
        this.user = user;
        this.update = update;
        
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
        for (RegisteredPlugin plugin : Plugins.getInstance().getRegistered()) {
            permission = null;
            if (!plugin.isAuthorizable())
                continue;
            
            if (update) {
                for (DcObject child : user.getChildren()) {
                    p = (Permission) child;
                    if (p.getPlugin() != null &&  plugin.getKey().equals(p.getPlugin())) {
                        permission = p;
                        break;
                    }
                }
            }
            
            if (permission == null) {
                permission = DcModules.get(DcModules._PERMISSION).getItem();
                permission.setIDs();
                permission.setValue(Permission._A_PLUGIN, plugin.getKey());
                permission.setValue(Permission._F_USER, user);
                
                if (update) {
                    try {
                        // create the missing permission
                        connector.saveItem(permission);
                    } catch (ValidationException ve) {
                        logger.error(ve, ve);
                    }
                } 
            }
            
            Object[] row = new Object[] {plugin.getKey(), 
                                         permission.getValue(Permission._D_VIEW), 
                                         permission};
            table.addRow(row);
            permission.markAsUnchanged();
        }
    }
    
    public void clear() {
        table.clear();
        table = null;
        user = null;
    }

    private void build() {
        setLayout(Layout.getGBL());

        table = ComponentFactory.getDCTable(false, false);
        table.setColumnCount(3);

        TableColumn cModule = table.getColumnModel().getColumn(_COLUMN_LABEL);
        cModule.setHeaderValue(DcResources.getText("lblPlugin"));
        
        TableColumn cView = table.getColumnModel().getColumn(_COLUMN_AUTHORIZED);
        JCheckBox cbView = ComponentFactory.getCheckBox("");
        cbView.addActionListener(this);
        cbView.setActionCommand("applyRight");
        cView.setCellEditor(new DefaultCellEditor(cbView));
        cView.setCellRenderer(CheckBoxTableCellRenderer.getInstance());
        cView.setHeaderValue(DcResources.getText("lblAuthorized"));

        TableColumn cHidden = table.getColumnModel().getColumn(_COLUMN_PERMISSION);
        table.removeColumn(cHidden);
        
        // table
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        add(scroller,  Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        
        table.applyHeaders();
    }

    private void applyRight() {
        table.cancelEdit();
        int row = table.getSelectedRow();
        if (row > -1) {
            Permission permission = (Permission) table.getValueAt(row, _COLUMN_PERMISSION, true);
            permission.setValue(Permission._D_VIEW, table.getValueAt(row, _COLUMN_AUTHORIZED, true));
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
    
    private void enableAll() {
        for (int row = 0; row < table.getRowCount(); row++)
            table.setValueAt(Boolean.FALSE, row, _COLUMN_AUTHORIZED);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("applyRight"))
            applyRight();
        else if (ae.getActionCommand().equals("enabledAll"))
            enableAll();
    }
}
