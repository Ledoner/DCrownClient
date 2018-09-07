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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import net.datacrow.console.menu.ContainerTreePanelMenuBar;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterConverter;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Container;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.Settings;
import net.datacrow.util.PollerTask;

import org.apache.log4j.Logger;

/**
 * Hierarchical container view.
 * 
 * @author Robert Jan van der Waals
 */
public class ContainerTreePanel extends TreePanel {
	
    private final static Logger logger = Logger.getLogger(ContainerTreePanel.class.getName());
	
    private TreeHugger treeHugger;
    
    public ContainerTreePanel(GroupingPane gp) {
        super(gp);
    }
    
    @Override
    protected JMenuBar getMenu() {
        return new ContainerTreePanelMenuBar(getModule(), this);
    }
    
    @Override
    protected void createTopNode() {
        NodeElement ne = new NodeElement(getModule(), DcModules.get(DcModules._CONTAINER).getLabel(), null);
        top = new DcDefaultMutableTreeNode(ne);
    }

    @Override
    public void groupBy() {
    	createTree();
    }
    
    @Override
    public void applySettings() {
        super.applySettings();
        if (top != null && top.getUserObject() != null) {
            NodeElement ne = (NodeElement) top.getUserObject();
            ne.setDisplayValue(DcModules.get(DcModules._CONTAINER).getLabel());
        }
    }

    @Override
	public void sort() {}
    
    @Override
    public void setSelected(DcObject dco) {
        if (dco.getModule().getIndex() != DcModules._CONTAINER)
            return;
        
        super.setSelected(dco);
    }
    
    @Override
    public DcDefaultMutableTreeNode getFullPath(DcObject dco) {
        DcDefaultMutableTreeNode node;
        DcObject parent = (DcObject) dco.getValue(Container._F_PARENT);
        node = parent != null ? 
                findNode(new DcDefaultMutableTreeNode(new ContainerNodeElement(parent.getID(), parent.toString(), null)), top, true) :
                new DcDefaultMutableTreeNode(DcModules.get(DcModules._CONTAINER).getLabel());
            
        node = node == null ? new DcDefaultMutableTreeNode(DcModules.get(DcModules._CONTAINER).getLabel()) : node;
        node.removeAllChildren();
        node.add(new DcDefaultMutableTreeNode(new ContainerNodeElement(dco.getID(), dco.toString(), dco.getIcon())));
        return node;
    }  

	@Override
    public String getName() {
        return DcModules.get(DcModules._CONTAINER).getObjectNamePlural();
    }    
    
    @Override
    protected void createTree() {
    	
        build();
    	
        if (treeHugger != null) {
            treeHugger.cancel();
        }
        
        activated = true;
        
        treeHugger = new TreeHugger();
        treeHugger.start();
    }
    
    @Override
	public boolean isChanged(DcObject dco) {
    	return dco.getModule().getIndex() == DcModules._CONTAINER &&
    	        (dco.isChanged(Container._A_NAME) || dco.isChanged(Container._F_PARENT));
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
        
        private void createTree() {
	    	
	    	build();
	    	
	    	String sql = null;
	    	
	    	try {
		    	DcModule module = DcModules.get(DcModules._CONTAINER);
		    	
		    	DataFilter df = DataFilters.getCurrent(DcModules._CONTAINER);
		    	DataFilterConverter dfc = new DataFilterConverter(df);
		    	
		    	sql = dfc.toSQL(new int[] {Container._ID, Container._A_NAME, Container._F_PARENT, Container._E_ICON}, true, false);
		    	
		    	Connector connector = DcConfig.getInstance().getConnector();
		    	DcResultSet rs = connector.executeSQL(sql);
		    	
		    	logger.debug(sql);
		    	
		    	String name;
		    	String id;
		    	String parentId;
		    	String icon;
		    	
		    	Map<String, DcImageIcon> icons = new HashMap<String, DcImageIcon>();
		    	Map<String, String> parents = new LinkedHashMap<String, String>();
		    	Map<String, String> all = new LinkedHashMap<String, String>();
		    	Map<String, Collection<String>> relations = new HashMap<String, Collection<String>>();
		    	
		    	Settings settings = DcModules.get(DcModules._CONTAINER).getSettings();
		    	boolean flatView = settings.getBoolean(DcRepository.ModuleSettings.stContainerTreePanelFlat);
		    	
		    	for (int row = 0; row < rs.getRowCount(); row++) {
		    		id = rs.getString(row, 0); 
		    		name = rs.getString(row, 1); 
		    		parentId = rs.getString(row, 2); 
		    		icon = rs.getString(row, 3); 
		    		
		    		if (parentId != null && !flatView) {
		    			Collection<String> children = relations.get(parentId);
		    			children = children == null ? new ArrayList<String>() : children;
		    			children.add(id);
		    			relations.put(parentId, children);
		    		} else {
		    			parents.put(id, name);	
		    		}
		    		
		    		if (icon != null) icons.put(id, CoreUtilities.base64ToImage(icon));
		    		
		    		all.put(id, name);
		    	}
		    	
		    	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		    	int counter = 0;
		    	ContainerNodeElement ne;
		    	DcDefaultMutableTreeNode current;
		    	for (String parentKey : parents.keySet()) {
		    		
		    		if (stop) break;
		    		
		    		ne = new ContainerNodeElement(parentKey, (String) parents.get(parentKey), icons.get(parentKey));
		    		current = new DcDefaultMutableTreeNode(ne);
		    		model.insertNodeInto(current, top, counter++);
		    		top.addItem(parentKey, Integer.valueOf(module.getIndex()));
		    		createChildren(model, parentKey, current, relations, all, icons);
		    	}
		    	
	    	} catch (Exception e) {
	    		logger.error("Error while building the container tree", e);
	    	}
			
            SwingUtilities.invokeLater(
                    new Thread(new Runnable() { 
                        @Override
                        public void run() {
                            expandAll();
                            setDefaultSelection();
                        }
                    }));
	    }

	    private void createChildren(DefaultTreeModel model,
	    							String parentKey, 
	    							DcDefaultMutableTreeNode parentNode, 
	    							Map<String, Collection<String>> relations,
	    							Map<String, String> all,
	    							Map<String, DcImageIcon> icons) {
	    	
	    	if (relations.containsKey(parentKey)) {
	    		int counter = 0;
	    		ContainerNodeElement ne;
	    		DcDefaultMutableTreeNode node;
	    		for (String childKey : relations.get(parentKey)) {
		    		ne = new ContainerNodeElement(childKey, all.get(childKey), icons.get(childKey));
		    		node = new DcDefaultMutableTreeNode(ne);
		    		model.insertNodeInto(node, parentNode, counter++);
		    		top.addItem(childKey, Integer.valueOf(DcModules._CONTAINER));
		    		model.nodeChanged(top);
		    		createChildren(model, childKey, node, relations, all, icons);
	    		}
	    	}
	    }
 	}
}
