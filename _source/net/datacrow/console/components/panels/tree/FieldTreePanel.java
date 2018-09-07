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

package net.datacrow.console.components.panels.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.menu.FieldTreePanelMenuBar;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.util.PollerTask;

import org.apache.log4j.Logger;

public class FieldTreePanel extends TreePanel {

	private static Logger logger = Logger.getLogger(FieldTreePanel.class.getName());
    private int[] fields;
    private String empty = DcResources.getText("lblEmpty");
    
    private TreeHugger treeHugger;
    
    public FieldTreePanel(GroupingPane gp) {
        super(gp);
    }
    
    public int[] getGroupBy() {
        return fields;
    }
    
    @Override
    public String getName() {
        return DcResources.getText("lblDefault");
    }
    
    public void groupBy(int[] fields) {
        this.fields = fields;
        DcModules.get(getModule()).setSetting(DcRepository.ModuleSettings.stGroupedBy, fields);
        createTree();
    }
    
    @Override
    public void groupBy() {
        fields = (int[]) DcModules.get(getModule()).getSetting(DcRepository.ModuleSettings.stGroupedBy);
        groupBy(fields);
    }

    @Override
    protected JMenuBar getMenu() {
        return new FieldTreePanelMenuBar(getModule(), this);
    }
    
    @Override
    public void reset() {
        DcModules.get(getModule()).setSetting(DcRepository.ModuleSettings.stGroupedBy, null);
        fields = null;
        super.reset();
    } 
    
    @Override
    public DcDefaultMutableTreeNode getFullPath(DcObject dco) {
        DcDefaultMutableTreeNode node = new DcDefaultMutableTreeNode("top");
        add(dco, 0, node);
        return node;
    }
    
    @Override
	public boolean isChanged(DcObject dco) {
    	boolean changed = false;
    	if (fields != null) {
    		for (int field : fields)
    			changed |= dco.isChanged(field);
    	}
    	return changed;
	}

	/************************************************************************
     * Initialization
     ************************************************************************/
    
    @Override
    protected void createTree() {
        if (treeHugger != null) {
            treeHugger.cancel();
        }
        
        activated = true;
        treeHugger = new TreeHugger();
        treeHugger.start();
    }
    
    private class TreeHugger extends Thread {
        
    	private PollerTask poller;
    	
        private boolean stop = false;
        
        @Override
        public void run() {
            if (poller != null) poller.finished(true);
            
            poller = new PollerTask(this, DcResources.getText("lblGroupingItems"));
            poller.start();

        	createTree();
        	
            poller.finished(true);
            poller = null;
        }
        
        public void cancel() {
            stop = true;
        }
        
        protected void createTree() {

        	fields = (int[]) DcModules.get(getModule()).getSetting(DcRepository.ModuleSettings.stGroupedBy);
            
            build();
            
            if (fields != null) 
                createTree(fields);
            
            SwingUtilities.invokeLater(
                    new Thread(new Runnable() { 
                        @Override
                        public void run() {
                            expandAll();
                            setDefaultSelection();
                        }
                    }));
        }
        
        /**
         * Creates a flat structure of the tree to be build.
         * The tree is created based on the result one SQL statement.
         * @param fields
         */
        private void createTree(int[] fields) {
            DcModule m = DcModules.get(getModule());

            StringBuffer sql = new StringBuffer("");
            
            List<String> joinOn = new ArrayList<String>();
            
            DcField field;
            DcModule reference;
            DcModule main;
            
            Collection<DcModule> modules = new ArrayList<DcModule>();
            
            if (m.isAbstract()) {
            	modules = DcModules.getPersistentModules(m);
            } else {
            	modules.add(m);
            }
            
            int moduleCounter = 0;
            int fieldCounter = 0;
            
            StringBuffer columns;
            StringBuffer joins;
            
            for (DcModule module : modules) {
            	
            	fieldCounter = 0;

            	columns = new StringBuffer("select ");
                joins = new StringBuffer("from ");
                joins.append(module.getTableName());
                
            	if (moduleCounter > 0)
            		sql.append(" UNION ");
            	
                columns.append(module.getTableName());
                columns.append(".ID, ");
                columns.append(module.getIndex());
                columns.append(" AS MODULEIDX");      
            	
	            for (int idx : fields) {
	                field = module.getField(idx);
	                if (field == null ||
	                    (field.isUiOnly() &&
	                     field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
	                     field.getValueType() != DcRepository.ValueTypes._DCOBJECTREFERENCE)) continue;
	                
                    columns.append(",");
	                
	                joinOn.add(module.getTableName() + fieldCounter + ".ID");
	                
	                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
	                    field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
	
	                    reference = DcModules.get(field.getReferenceIdx());
	                    main = field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ? module : 
	                           DcModules.get(DcModules.getMappingModIdx(module.getIndex(), reference.getIndex(), field.getIndex()));
	
	                    if (    field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
	                            field.getFieldType() == ComponentFactory._REFERENCEFIELD) {
	                        
	                        columns.append("subselect");
	                        columns.append(fieldCounter);
	                        columns.append(".ID AS ");
	                        columns.append("ID");
	                        columns.append(fieldCounter);
	                        columns.append(", ");
	                            
	                        if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
	                            
	                            columns.append("subselect");
	                            columns.append(fieldCounter);
	                            columns.append(".name AS ");
	                            columns.append("NAME");
	                            columns.append(fieldCounter);
	                            columns.append(", ");
	                            
	                            columns.append("subselect");
	                            columns.append(fieldCounter);                            
	                            columns.append(".icon AS ");
	                            columns.append("ICON");
	                            columns.append(fieldCounter);
	                            
	                            joins.append(" left outer join ");
	                            joins.append("(select ");
	                            joins.append(reference.getTableName());
	                            joins.append(".ID as ID,");
	                            joins.append(main.getTableName());
	                            joins.append(".");
	                            joins.append(main.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName());
	                            joins.append(" as parentID, ");
	                            
	                            
	                            joins.append(reference.getTableName());
	                            joins.append(".");
	                            joins.append(reference.getField(reference.getSystemDisplayFieldIdx()).getDatabaseFieldName());
	                            joins.append(" as name,");
	                            
	                            if (reference.getIconField() != null) {
	                                joins.append(reference.getTableName());
	                                joins.append(".");
	                                joins.append(reference.getIconField().getDatabaseFieldName());
	                            } else {
	                                joins.append(" NULL");
	                            }
	                            
	                            joins.append(" as icon");
	                            joins.append(" from ");
	                            joins.append(reference.getTableName());
	                            joins.append(" inner join ");
	                            joins.append(main.getTableName());
	                            joins.append(" on ");
	                            joins.append(reference.getTableName());
	                            joins.append(".ID = ");
	                            joins.append(main.getTableName());
	                            joins.append(".");
	                            joins.append(main.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName());
	                            joins.append(") subselect");
	                            joins.append(fieldCounter);
	                            joins.append(" on ");
	                            joins.append(" subselect");
	                            joins.append(fieldCounter);
	                            joins.append(".parentID = ");
	                            joins.append(module.getTableName());
	                            joins.append(".ID ");
	
	                        } else {
	                            
	                            columns.append("subselect");
                                columns.append(fieldCounter);
                                columns.append(".");
                                columns.append(reference.getField(reference.getSystemDisplayFieldIdx()).getDatabaseFieldName());
                                columns.append(" as name,");
                          
                                if (reference.getIconField() != null) {
                                    columns.append("subselect");
                                    columns.append(fieldCounter);  
                                    columns.append(".");
                                    columns.append(reference.getIconField().getDatabaseFieldName());
                                } else {
                                    columns.append(" NULL");
                                }
                                
                                columns.append(" as icon");

	                            joins.append(" left outer join ");
	                            joins.append(reference.getTableName());
	                            joins.append(" ");
	                            joins.append(" subselect");
	                            joins.append(fieldCounter);
	                            joins.append(" on ");
	                            joins.append(module.getTableName());
	                            joins.append(".");
	                            joins.append(field.getDatabaseFieldName());
	                            joins.append("=");
	                            joins.append(" subselect");
	                            joins.append(fieldCounter);
	                            joins.append(".ID");
	                        }
	                    }
	                } else {
	                    columns.append(module.getTableName());
	                    columns.append(".");
	                    columns.append(field.getDatabaseFieldName());
	                    columns.append(",");
	                    columns.append(module.getTableName());
	                    columns.append(".");
	                    columns.append(field.getDatabaseFieldName() + " as df");
	                    columns.append(fieldCounter);
	                    columns.append(",NULL");
	                    
	                    joinOn.add(module.getTableName() + ".ID");
	                }
	                fieldCounter++;
	            }
	            
	            sql.append(columns.toString() + " " + joins.toString());
	            sql.append(" ");
	            moduleCounter++;
            }

            if (m.isAbstract()) {
            	sql.insert(0, "select * from (");
            	sql.append(") ");
        	}

            // index based order by
            int level = 0;
            boolean ordered = false;
            for (int idx : fields) { 
            	field = DcModules.get(getModule()).getField(idx);
                if (field == null ||
                    (field.isUiOnly() &&
                     field.getIndex() != DcObject._SYS_MODULE &&
                     field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
                     field.getValueType() != DcRepository.ValueTypes._DCOBJECTREFERENCE)) continue;

                if (!ordered) {
                	 sql.append(" order by ");
                	 ordered = true;
                } else {
            		sql.append(",");
                }
            		
            	if (field.getIndex() == DcObject._SYS_MODULE) {
            	    sql.append("2");
            	} else {
            	    sql.append(String.valueOf((level * 3) + 4));
            	    level++;
            	}
            }
            createTree(sql.toString());
        }
        
        /**
         * Creates a tree from the result of an SQL statement. 
         * The SQL statement should query the values in a flat hierarchy for each level of the tree:
         * key1-value-icon
         * key1-value-icon / key1-value-icon
         * key1-value-icon / key2-value-icon
         * key1-value-icon / key1-value-icon / key1-value-icon
         * key1-value-icon / key2-value-icon / key2-value-icon
         * @param sql
         */
        private void createTree(String sql) {
            try {
                logger.debug(sql);
                
                Connector connector = DcConfig.getInstance().getConnector();
                DcResultSet rs = connector.executeSQL(sql);
                
                NodeElement existingNe;
                NodeElement ne;
                String id = null;
                String value = null;
                Object key = null;
                DcImageIcon icon = null;
                String iconBase64 = null;
                DcField field = null;
                int module;

                final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                
                DefaultMutableTreeNode current;
                DefaultMutableTreeNode parent;
                DefaultMutableTreeNode previous;
                boolean exists = false;
                
                final Map<String, Integer> items = connector.getKeys(
                		DataFilters.getCurrent(getModule()));
                
                Collection<String> keys = null;
                if (DataFilters.isFilterActive(getModule())) {
                    keys = new ArrayList<String>(items.keySet());
                }
                
                for (int row = 0; row < rs.getRowCount(); row++) {
                    int level = 0;
                    parent = top;
                    
                    for (int idx = 0; idx < fields.length; idx++) {
                    	
                    	field = DcModules.get(getModule()).getField(fields[idx]);
                    	if (field == null ||
                    	    (field.isUiOnly() &&
                    	     field.getIndex() != DcObject._SYS_MODULE &&
                    	     field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
	                         field.getFieldType() != ComponentFactory._REFERENCEFIELD)) 
                    		continue;
                    	
                        
                        if (stop) break;
                        
                        // for each level the field index is shifted to the end.
                        id = rs.getString(row, 0);
                        module = rs.getInteger(row, 1);
                        key = field.getIndex() == DcObject._SYS_MODULE ? DcModules.get(module).getLabel() : rs.getValue(row, (level * 3) + 2);
                        value =  field.getIndex() == DcObject._SYS_MODULE ? DcModules.get(module).getLabel() : rs.getString(row, (level * 3) + 3);
                        iconBase64 = field.getIndex() != DcObject._SYS_MODULE ? rs.getString(row, (level * 3) + 4) : null;
                        
                        icon = field.getIndex() == DcObject._SYS_MODULE ? 
                               new DcImageIcon(DcModules.get(module).getIcon16().getImage()) : 
                               iconBase64 != null ? DcIconCache.getInstance().addIcon(String.valueOf(key), iconBase64) : null;
                        
                        if (keys != null && !keys.contains(id)) continue;
                        
                        previous = parent.getChildCount() == 0 ? null : ((DefaultMutableTreeNode) parent.getChildAt(parent.getChildCount() - 1));
                        exists = previous == null || (((NodeElement)  previous.getUserObject()).getKey() == null && key != null) ? false : 
                                ((NodeElement)  previous.getUserObject()).getKey() == key || // empty key
                                ((NodeElement)  previous.getUserObject()).getKey().equals(key);
                        
                        if (!exists) { 
                            if (key == null) {
                                ne = new NodeElement(null, empty, null);
                            } else {
                                ne = new NodeElement(key, value, icon);
                            }
                            
                            ne.addItem(id, module);
                            current = new DcDefaultMutableTreeNode(ne);
                            parent.add(current);
                            parent = current;
                           
                        } else { // exists
                            existingNe =(NodeElement) previous.getUserObject();
                            existingNe.addItem(id, module);
                            model.nodeChanged(previous);
                            parent = previous;    
                        }
                        
                        if (field.getIndex() != DcObject._SYS_MODULE)
                            level++;
                    }
                }
                
                SwingUtilities.invokeLater(
                        new Thread(new Runnable() { 
                            @Override
                            public void run() {
                                NodeElement topElem = (NodeElement) top.getUserObject();
                                topElem.setItems(items);
                                model.nodeChanged(top);
                            }
                        }));
                
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
    }
    
    @Override
    public void applySettings() {
        super.applySettings();
        
        if (top != null && top.getUserObject() != null) {
            DcModule mod = DcModules.get(getModule());
            String label = mod.getObjectNamePlural();
            NodeElement ne = (NodeElement) top.getUserObject();
            ne.setDisplayValue(label);
        }
    }

    @Override
    protected void createTopNode() {
        DcModule mod = DcModules.get(getModule());
        String label = mod.getObjectNamePlural();
        
        top = new DcDefaultMutableTreeNode(label);
        top.setUserObject(new NodeElement(null, label, null));
    }

    private void add(DcObject dco, int level, DcDefaultMutableTreeNode node) {
        
        int index = level;
        
        if (fields == null || index >= fields.length) return;
        
        DcDefaultMutableTreeNode child;
        DcField field = dco.getField(fields[index]);
        Object value = dco.getValue(fields[index]);
        index++;
        if (CoreUtilities.isEmpty(value)) {
            child = new DcDefaultMutableTreeNode(new NodeElement(empty, empty, null));
            node.add(child);
            add(dco, index, child);
        } else {
            if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                @SuppressWarnings("unchecked")
				Collection<DcObject> references = (Collection<DcObject>) dco.getValue(field.getIndex());
                if (references != null && references.size() > 0) {
                    for (DcObject reference : references) {
                        child = new DcDefaultMutableTreeNode(new NodeElement(reference.toString(), reference.toString(), reference.getIcon()));
                        node.insert(child, 0);
                        add(dco, index, child);
                    }
                }
            } else {
                String key = dco.getDisplayString(field.getIndex());
                child = new DcDefaultMutableTreeNode(new NodeElement(key, key, null));
                node.add(child);
                add(dco, index, child);
            }
        }
    }
}
