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

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcTree;
import net.datacrow.console.views.MasterView;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.ITreePanel;
import net.datacrow.core.console.IView;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public abstract class TreePanel extends JPanel implements TreeSelectionListener, ITreePanel {
    
    private static Logger logger = Logger.getLogger(TreePanel.class.getName());
    
    protected DcTree tree;
    private JScrollPane scroller;
    protected DcDefaultMutableTreeNode top;
    
    private boolean listenForSelection = true;
    
    protected GroupingPane gp;
    protected Object currentUserObject;
    
    protected boolean activated = false;
    private boolean saveChanges = true;
    
    private JMenuBar menu;
    
    public TreePanel(GroupingPane gp) {
        this.gp = gp;
        
        setLayout(Layout.getGBL());
        
        installMenu();
        build();
    }
    
    private void installMenu() {
        if (menu != null)
            remove(menu);
        
        menu = getMenu();
        if (menu != null) {
            add(menu, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 0, 5), 0, 0));
        }
    }
    
    @Override
    public void applySettings() {
        installMenu();
    }
    
    @Override
    public boolean isActivated() {
        return activated;
    }

    @Override
    public void activate() {
    	if (isShowing() && isEnabled() && !activated) {
    		activated = true;
    		groupBy();
    	} else if (activated) {
    		refreshView();
    	}
    }
    
    @Override
	public boolean isEnabled() {
		return super.isEnabled() && DcSettings.getBoolean(DcRepository.Settings.stShowGroupingPanel);
	}

    public MasterView getView() {
        return gp.getView();
    }
    
    public boolean isListeningForSelection() {
        return listenForSelection;
    }
    
    public void setListeningForSelection(boolean b) {
        listenForSelection = b;
    }
    
    @Override
    public boolean isHoldingItems() {
        return top != null;
    }
    
    public Object getLastSelectedPathComponent() {
        return tree != null ? tree.getLastSelectedPathComponent() : null;
    }
    
    public DcDefaultMutableTreeNode getTopNode() {
        return top;
    }
    
    @Override
    public void setSaveChanges(boolean b) {
        saveChanges = b;
    }
    
    public boolean isSaveChanges() {
        return saveChanges;
    }
    
    public int getModule() {
        return gp.getModule();
    }
    
    @Override
    public boolean isLoaded() {
        return top != null && top.getItemCount() > 0;
    }
    
	@Override
    public void sort() {
	    if (!isLoaded()) return;
	    
    	NodeElement topElem = (NodeElement) top.getUserObject();
    	Connector connector = DcConfig.getInstance().getConnector();
    	topElem.setItems(connector.getKeys(DataFilters.getCurrent(getModule())));
	}
	
    @Override
    public void updateTreeNodes(DcObject dco) {
        updateTreeNodes(tree.getModel(), dco, top);
    }
    
    private void updateTreeNodes(TreeModel model, DcObject dco, DcDefaultMutableTreeNode node) {
        int cc;
        cc = node.getChildCount();
        for (int i = cc - 1; i > -1; i--) {
            DcDefaultMutableTreeNode child = (DcDefaultMutableTreeNode) model.getChild(node, i);
            NodeElement ne = (NodeElement) child.getUserObject();
            if (ne.getKey() != null && ne.getKey().equals(dco.getID())) {
                ne.setDisplayValue(dco.toString());
                ne.setIcon(dco.getIcon());
            }         
            
            if (child.getChildCount() > 0)
                updateTreeNodes(model, dco, child);
        }
    }
	
	@Override
    public void setSelected(DcObject dco) {
	    
	    if (top == null || top.getChildCount() == 0)
	        return;
	    
	    DcDefaultMutableTreeNode node = (DcDefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        NodeElement elem = node != null ? (NodeElement) node.getUserObject() : null;
	    
	    // check whether the currently selected node already contains the key (in which case selection does
	    // not need to be changed.
	    if (elem == null ||  !elem.getItems().containsKey(dco.getID())) {
	        DcDefaultMutableTreeNode newNode = findNode((DcDefaultMutableTreeNode) getFullPath(dco).getLastChild(), top, true);
	        if (newNode != null) 
	            setSelected(newNode);
	    }
	}
    
    @Override
    public void add(DcObject dco) {
        DcDefaultMutableTreeNode path = getFullPath(dco);
        String item = dco.getID();
        if (top != null) {
            NodeElement ne = (NodeElement) top.getUserObject();
            ne.addItem(item, dco.getModule().getIndex());
            add(item, dco.getModule().getIndex(), path, top);
            
            if (top.getChildCount()  == 0)
                setDefaultSelection();
        }
    }
    
    /**
     * Adds recursive
     * @param child Does not need to exist!
     * @param parent Existing parent
     */
    private void add(String item, int module, DcDefaultMutableTreeNode node, DcDefaultMutableTreeNode parent) {
    	DcDefaultMutableTreeNode existingChild;

    	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    	
    	// need to add to a collection as nodes will be removed once placed in the actual tree (!)
    	Collection<DcDefaultMutableTreeNode> nodes = new ArrayList<DcDefaultMutableTreeNode>();
    	for (int i = 0; i < node.getChildCount(); i++)
    		nodes.add((DcDefaultMutableTreeNode) node.getChildAt(i));
    	
    	DcDefaultMutableTreeNode actualParent;
    	for (DcDefaultMutableTreeNode child : nodes) {
    	    existingChild = findNode(child, parent, false);
    	    
    	    actualParent = findNode((DcDefaultMutableTreeNode) child.getParent(), getTopNode(), true);
    	    actualParent = actualParent == null ? parent : actualParent;
    	    if (existingChild == null) {
    	    	// will be removed from the node as well: 
    	        child.addItem(item, Integer.valueOf(module));
    	        insertNode(child, actualParent);
    	        existingChild = child;
    	    } else {
    	        existingChild.addItem(item, Integer.valueOf(module));
    	        model.nodeChanged(existingChild);
    	        
    	    }
    	    add(item, module, child, existingChild);
    	}
    }
    
    @Override
    public void remove(String item) {
    	if (top == null) return;
    	
    	top.removeItem(item);
    	remove(item, top);
    	repaint();
    }
    
    /**
     * Adds recursive
     * @param child Does not need to exist!
     * @param parent Existing parent
     */
    private void remove(String item, DcDefaultMutableTreeNode node) {
        DcDefaultMutableTreeNode child;
        for (int i = node.getChildCount() -1; i > -1 ; i--) {
            child = (DcDefaultMutableTreeNode) node.getChildAt(i);
            child.removeItem(item);
            
            if (child.getItemCount() == 0) removeNode(child);
            
            remove(item, child);
        }
    }
    
    public abstract boolean isChanged(DcObject dco);
    
    /**
     * Updates the tree to reflect changes made to the item.
     * It will check if the items update should have any effect on the tree and
     * update the tree accordingly. 
     * @param dco
     */
    @Override
    public void update(DcObject dco) {
        if (isChanged(dco)) {
            remove(dco.getID());
            add(dco);
        }
    }
    
    /**
     * This method is used to determine the full tree structure for this item.
     * The structure can be used to add the item to the tree. 
     * @param dco
     * @return node containing tree structure
     */
    public abstract DcDefaultMutableTreeNode getFullPath(DcObject dco);
    
    public void collapseAll() {
        if (top == null) return;
        try {
            setSelected(top);
            tree.collapsePath(new TreePath(top.getPath()));
        } catch (Exception e) {
            logger.error(e, e);
        }

    }   
    
    public void expandAll() {
        expandAll(top);
    }   
    
    public void collapseChildren(DefaultMutableTreeNode parent) {
        
        if (parent == null || tree == null) return;
        
        int size = parent.getChildCount();
        
        setSelected(top);
        
        tree.removeTreeSelectionListener(this);
        
        DefaultMutableTreeNode child;
        for (int i = 0; i < size; i++) {
            try {
                child = (DefaultMutableTreeNode) parent.getChildAt(i);
                collapseChildren(child);
                tree.collapsePath(new TreePath(child.getPath()));
            } catch (Exception e) {
                logger.error("An error occurred while collapsing leafs of " + parent, e);
            }
            
            tree.addTreeSelectionListener(this);
        }
    }
    
    public void expandChildren(DefaultMutableTreeNode parent) {
        
        if (parent == null || tree == null) return;
        
        int size = parent.getChildCount();
        
        setSelected(top);
        
        tree.removeTreeSelectionListener(this);
        
        DefaultMutableTreeNode child;
        for (int i = 0; i < size; i++) {
            try {
                child = (DefaultMutableTreeNode) parent.getChildAt(i);
                expandChildren(child);
                tree.expandPath(new TreePath(child.getPath()));
            } catch (Exception e) {
                logger.error("An error occurred while expanding leafs of " + parent, e);
            }
        }
        
        tree.addTreeSelectionListener(this);
    }    
    
    
    private void expandAll(DefaultMutableTreeNode node) {
        try {
            
            if (node == null || node.getPath() == null || tree == null) return;
            
            TreePath path = new TreePath(node.getPath());
            if (path.getLastPathComponent() != null)
                tree.expandPath(path);
        } catch (Exception e) {
            logger.error(e, e);
        }
    }
    
    protected void updateView(Map<String, Integer> keys) {
        getView().getCurrent().clear();
        getView().getCurrent().add(keys);  
    }
    
    @Override
    public void setDefaultSelection() {
        
        if (tree == null) return;
        
        setListeningForSelection(true);
        try {
            if (isEnabled()) {
            	if (top.getChildCount() > 0)
            		tree.setSelectionInterval(1, 1);
            	else
            		tree.setSelectionInterval(0, 0);
            } else { 
                tree.setSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    @Override
    public void clear() {
        
        if (tree == null) return;

        if (top != null) {
            tree.removeTreeSelectionListener(this);
            
            DefaultMutableTreeNode pn;
            DefaultMutableTreeNode cn;
            DefaultMutableTreeNode cn2;
            for (int i = 0; i < top.getChildCount(); i++) {
                pn = (DefaultMutableTreeNode) top.getChildAt(i);
                
                if (pn.getUserObject() != null)
                    ((NodeElement) pn.getUserObject()).clear();
                
                for (int j = 0; j < pn.getChildCount(); j++) {
                    cn = (DefaultMutableTreeNode) pn.getChildAt(j);
                    if (cn.getUserObject() != null)
                        ((NodeElement) cn.getUserObject()).clear();
                    
                    for (int k = 0; k < cn.getChildCount(); k++) {
                        cn2 = (DefaultMutableTreeNode) cn.getChildAt(k);
                        if (cn2.getUserObject() != null)
                            ((NodeElement) cn2.getUserObject()).clear();
                        cn2.removeAllChildren();
                    }
                    cn.removeAllChildren();
                }
                pn.removeAllChildren();
            }
            
            top.removeAllChildren();
            ComponentFactory.clean(tree);
        }
        
        tree = null;
        top = null;
        currentUserObject = null;
        
        if (scroller != null) remove(scroller);
    }
    
    @Override
    public void setFont(Font font) {
    	super.setFont(font);
        if (tree != null)
            tree.setFont(font);
        
        for (Component c : getComponents())
        	c.setFont(font);
    }
    
    protected void build() {
        clear();
        
        removeAll();

        installMenu();
        
        createTopNode();
                
        tree = new DcTree(new DefaultTreeModel(top));
        tree.setFont(ComponentFactory.getStandardFont());
        tree.addTreeSelectionListener(this);
        
        scroller = new JScrollPane(tree);
        
        add(scroller,  Layout.getGBC( 0, 1, 1, 1, 100.0, 200.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(0, 5, 5, 5), 0, 0));
    }     
    
    public void reset() {
        createTree();
    }
    
    protected void setSelected(DefaultMutableTreeNode node) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        TreePath treePath = new TreePath(model.getPathToRoot(node));
        tree.setSelectionPath(treePath);
        tree.expandPath(treePath);
        tree.scrollPathToVisible(treePath);
        
        currentUserObject = node.getUserObject();
    }
    
    /**
     * Inserts a node. The node needs to have a valid user object defined
     * to insert it at the right position (ordering)
     */
    protected void insertNode(DefaultMutableTreeNode node, DefaultMutableTreeNode parent) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        
        NodeElement ne = (NodeElement) node.getUserObject();
        
        List<String> elements = new ArrayList<String>();
        NodeElement child;
        for (int i = 0; i < parent.getChildCount(); i++) {
            child = ((NodeElement) ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject());
            elements.add(child.getComparableKey());
        }
        
        elements.add(ne.getComparableKey());
        Collections.sort(elements);
        int idx = elements.indexOf(ne.getComparableKey());
        
        model.insertNodeInto(node, parent, parent.getChildCount() == 0 ? 0 : idx);
        
        tree.expandPath(new TreePath(model.getPathToRoot(node)));
        tree.revalidate();
    }

    /**
     * Recursive search method for tree nodes.
     * @param key
     * @param parent
     * @param recurse
     */
    protected DcDefaultMutableTreeNode findNode(DcDefaultMutableTreeNode child, 
                                                DcDefaultMutableTreeNode parent,
                                                boolean recurse) {
        
        int count = parent != null ? parent.getChildCount() : 0;
        
        if (parent == null && getTopNode().equals(child))
            return getTopNode();
        
        DcDefaultMutableTreeNode node;
        DcDefaultMutableTreeNode result = null;
        for (int i = 0; i < count; i++) {
            node = (DcDefaultMutableTreeNode) parent.getChildAt(i);

            if (child.equals(node))
                result = node;
            
            if (result == null && recurse)
                result = findNode(child, node, recurse);
            
            if (result != null) return result;
        }
        
        return null;
    }  
    
    protected abstract JMenuBar getMenu();
    protected abstract void createTopNode();
    protected abstract void createTree();
    
    @Override
    public void refreshView() {
        
        if (tree == null) 
            build();
        
    	DcDefaultMutableTreeNode node = (DcDefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    	if (node != null) {
    		getView().getCurrent().clear();
    		getView().getCurrent().add(node.getItemsSorted(top.getItemList()));
    	}
    }

    @Override
    public abstract void groupBy();
    
    private void removeNode(DefaultMutableTreeNode child) {
    	 DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    	 
    	 try {
    	     model.removeNodeFromParent(child); 
    	 } catch (Exception e) {
    	     logger.debug("Error while remove node " + child, e);
    	 }
    
    	 // remove empty branches above (needed for the file tree panel)
    	 DefaultMutableTreeNode parent = (DefaultMutableTreeNode) child.getParent();
    	 NodeElement ne;
    	 while (parent != null) {
    		 ne = ((NodeElement) child.getUserObject());
    		 if (ne.getCount() == 0 && parent.getChildCount() == 0) {
    			 DefaultMutableTreeNode newParent = null;

    			 try {
    				 newParent = (DefaultMutableTreeNode) parent.getParent();
    			 } catch (Exception e) {}
   			    
    			 try {
    				 model.removeNodeFromParent(parent);
    				 parent = newParent;
    			 } catch (IllegalArgumentException iae) {
   			  	  	parent = null;
   				}
    		 } else {
    		     parent = null;
    		 }
    	 }
    	 tree.revalidate();
    }
    
    /************************************************************************
     * Selection listener
     ************************************************************************/
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        
        if (gp.getCurrent() != this) {
            return;
        }
        
        if (!isListeningForSelection())
            return;
        
        if (e.getNewLeadSelectionPath() == null || 
            e.getNewLeadSelectionPath().equals(e.getOldLeadSelectionPath()))
            return;
        
        DcDefaultMutableTreeNode node = (DcDefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) {
            currentUserObject = null;
            return;
        }

        IView currentView = getView().getCurrent();
        if (currentView != null) {
        	try {
        		currentView.clear(isSaveChanges());
        	} catch (Exception ee) {}
        		
        	setSelected(node);

        	if (node.getUserObject() instanceof String)
        	    updateView(top.getItems());
        	else
        	    updateView(node.getItemsSorted(top.getItemList()));
        }
    }
}
