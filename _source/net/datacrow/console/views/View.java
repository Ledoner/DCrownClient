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

package net.datacrow.console.views;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.clients.UIClient;
import net.datacrow.console.components.DcPanel;
import net.datacrow.console.components.DcViewDivider;
import net.datacrow.console.components.panels.QuickViewPanel;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IGroupingPane;
import net.datacrow.core.console.IView;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.plugin.InvalidPluginException;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.Connector;
import net.datacrow.core.wf.tasks.DcTask;
import net.datacrow.core.wf.tasks.DeleteItemTask;
import net.datacrow.core.wf.tasks.SaveItemTask;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

/**
 * The Swing presentation. A view uses a view component to render items.
 * Any component implementing the IViewComponent interface can be used as a view
 * component. 
 */
public class View extends DcPanel implements ListSelectionListener, IView {

    private static Logger logger = Logger.getLogger(View.class.getName());
    
    public static final int _TYPE_SEARCH = 0;
    public static final int _TYPE_INSERT = 1;
    
    protected IViewComponent vc;
    protected DcTask task;
    
    protected DcViewDivider vdQuickPane;
    protected DcViewDivider vdGroupingPane;
    protected ViewActionPanel actionPanel;
    
    private IGroupingPane groupingPane;
    protected QuickViewPanel quickView;
    
    protected boolean updateQuickView = true;
    private boolean checkForChanges = true;

    protected JPanel panelResult = new JPanel();
    
    private ViewScrollPane spChildView;
    private IView childView;
    private IView parentView;

    private boolean actionsAllowed = true;
    
    private String parentID;
    
    private final int type;
    private final int index;
    
    private final ViewMouseListener vml = new ViewMouseListener();
    
    public View(MasterView mv, int type, IViewComponent vc, int index) {
        
        this.groupingPane = mv.getGroupingPane();
        this.type = type;
        this.vc = vc;
        this.index = index;
        
        GUI gui = GUI.getInstance();
        DcModule cm = vc.getModule() != null ? vc.getModule().getChild() : null;
        
        this.childView = cm != null && getIndex() == MasterView._TABLE_VIEW ?
                getType() == _TYPE_SEARCH ? gui.getSearchView(cm.getIndex()).get(index) :
                gui.getInsertView(cm.getIndex()).get(index) : null;
        
        if (childView != null)
            childView.setParentView(this);
                
        vc.addMouseListener(vml);
        vc.addSelectionListener(this);
        vc.addKeyListener(new ViewKeyListener(this));

        if (type == _TYPE_INSERT)
            actionPanel = new ViewActionPanel(this);
        
        build();
        
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        setFocusable(true);

        Connector connector = DcConfig.getInstance().getConnector();
        SecuredUser su = connector.getUser();
        if (su == null ||su.isAdmin()) {
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
            getActionMap().put("DELETE", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ignored) {
                    delete();
                }
            });
        }
    }
    
    @Override
    public String getHelpIndex() {
    	if (getType() == _TYPE_INSERT)
    		return "dc.items.inserting";	
    	else 
    		return "dc.items.views";
    }
    
    @Override
    public void open(boolean readonly) {
        DcObject dco = getSelectedItem();
        
        if (dco != null) {
            ItemForm frm = GUI.getInstance().getItemForm(
                    getModule().getIndex(), 
                    readonly, 
                    getType() == View._TYPE_SEARCH, 
                    dco, getType() != View._TYPE_SEARCH);
            frm.setVisible(true);
        } else {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgSelectRowToOpen"));
        }
    }
    
    @Override
    public void setListSelectionListenersEnabled(boolean b) {
        if (b)
            vc.addSelectionListener(this);
        else
            vc.removeSelectionListener(this);
    }

    protected boolean allowsHorizontalTraversel() {
        return vc.allowsHorizontalTraversel();
    }
    
    @Override
    public void refreshQuickView() {
        if (quickView != null)
            quickView.refresh();
    }
    
    protected boolean allowsVerticalTraversel() {
        return vc.allowsVerticalTraversel();
    }
    
    public boolean isParent() {
        return childView != null && childView.isVisible();
    }
    
    public boolean isChild() {
        return parentView != null;
    }
    
    public IView getParentView() {
        return parentView;
    }

    public IView getChildView() {
        return childView;
    }
    
    @Override
    public void setParentView(IView parentView) {
        this.parentView = parentView;
    }      
    
    public IViewComponent getViewComponent() {
        return vc;
    }
    
    @Override
    public int getIndex() {
        return index;
    }
    
    @Override
    public int getIndex(String ID) {
        return vc.getIndex(ID);
    }
    
    @Override
    public int getType() {
        return type;
    }
    
    protected boolean isTaskRunning() {
        boolean isTaskRunning = task != null && task.isRunning();

        if (isTaskRunning)
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgJobRunning"));
        
        return isTaskRunning; 
    }
    
    @Override
    public void applyViewDividerLocation() {
        if (vdQuickPane != null) 
            vdQuickPane.applyDividerLocation();
        if (vdGroupingPane != null) 
            vdGroupingPane.applyDividerLocation();
        
        revalidate();
        repaint();
    }
    
    @Override
    public DcTask getCurrentTask() {
        return task;
    }
    
    @Override
    public void undoChanges() {
        vc.undoChanges();
    }    
    
    public void afterUpdate() {
        vc.afterUpdate();
    }
    
    public void setDefaultSelection() {
        int total = getItemCount();
        if (total > 0) {
        	setSelected(0);
        }
    }
    
    @Override
    public void sort() {
        DataFilter df = DataFilters.getCurrent(vc.getModule().getIndex());
        Connector connector = DcConfig.getInstance().getConnector();
        add(connector.getKeys(df));
    }
    
    @Override
    public DcModule getModule() {
        return vc.getModule();
    }
    
    @Override
    public void add(DcObject dco) {
        add(dco, true);
    }  
    
    public void add(String key) {
        add(key, true);
    }    
    
    public void add(final String key, final boolean select) {
        int index = vc.add(key);
        if (select)
            setSelected(index);
    }
    
    public void add(final DcObject dco, final boolean select) {
        dco.markAsUnchanged();
        
        if (getType() == View._TYPE_INSERT)
            dco.setIDs();
        
        int index = vc.add(dco);
        if (select)
            setSelected(index);
    }    

    public void cancelCurrentTask() {
        if (task != null && task.isRunning())
            task.endTask();
    }
    
    /**
     * Adds the items to the view. 
     * Note: children for the insert view are added by the view component.
     * @see DcTable#add(DcObject).
     * @param items
     */
    @Override
    public void add(Map<String, Integer> keys) {
        setActionsAllowed(false);

        vc.deselect();
        vc.add(keys);
        
        setActionsAllowed(true);
        
        revalidate();
        afterUpdate();
        setSelected();
    }      
    
    /**
     * Adds the items to the view. 
     * Note: children for the insert view are added by the view component.
     * @see DcTable#add(DcObject).
     * @param items
     */
    @Override
    public void add(List<DcObject> items) {
        setActionsAllowed(false);

        for (DcObject item : items) {
            if (getType() == View._TYPE_INSERT)
                item.setIDs();
        }
        
        vc.deselect();
        vc.add(items);
        
        setActionsAllowed(true);
        
        revalidate();
        afterUpdate();
        setDefaultSelection();
    }    
    
    protected void setSelected() {
        setSelected(0);
    }    
    
    public void checkForChanges(boolean b) {
        checkForChanges = b;
    }
    
    @Override
    public void clear(boolean saveChanges) {
        if (checkForChanges && saveChanges) {
            boolean saved = isChangesSaved();
            if (!saved) {
                if (GUI.getInstance().displayQuestion("msgNotSaved"))
                    save();
                else
                    vc.undoChanges();
            }
        } else {
            vc.undoChanges();
        }

        vc.ignoreEdit(true);
        vc.clear();
        updateProgressBar(0);      
        
        if (isParent()) {
            childView.clear();
            childView.setParentID(null, false);
        }
        
        if (quickView != null)
            quickView.clear();
        
        vc.ignoreEdit(false);
    }

    public void cancelTask() {
        if (task != null) task.cancel();
        setActionsAllowed(true);
        updateProgressBar(0);
    }
    
    @Override
    public void deactivate() {
        if (vdGroupingPane != null) vdGroupingPane.deactivate();
        if (vdQuickPane != null) vdQuickPane.deactivate();
    }
    
    private void reinstall() {
        
        removeAll();
        
        // only the search view uses view dividers
        if (getType() == _TYPE_SEARCH && !getModule().isChildModule()) {
            
            if (vdGroupingPane != null) remove(vdGroupingPane);
            if (vdQuickPane != null) remove(vdQuickPane);
            
            Component c = vdQuickPane != null ? vdQuickPane : vdGroupingPane;
            
            vdGroupingPane = new DcViewDivider((JComponent) groupingPane, panelResult, DcRepository.Settings.stTreeDividerLocation);
            c = vdGroupingPane;
            
            vdQuickPane = null;
            quickView = null;
            if (DcSettings.getBoolean(DcRepository.Settings.stShowQuickView)) {
                quickView = GUI.getInstance().getQuickView(getModule().getIndex());
                vdQuickPane = new DcViewDivider(vdGroupingPane, quickView, DcRepository.Settings.stQuickViewDividerLocation);
                c = vdQuickPane;
                
                vdGroupingPane.applyDividerLocation();
                vdQuickPane.applyDividerLocation();
            }
            
            add(c, Layout.getGBC( 0, 1, 3, 1, 100.0, 100.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0)); 
            
            c.revalidate();
            
        } else {
            remove(panelResult);
            add(panelResult, Layout.getGBC( 0, 1, 3, 1, 100.0, 100.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0)); 
        }
        
        revalidate();
    }
    
    @Override
    public void activate() {        
        reinstall();
        
        vc.activate();
        
        if (childView != null) 
        	childView.activate();
        
        applyViewDividerLocation();
    }
    
    public void groupBy() {
        if (groupingPane != null)
            groupingPane.groupBy();        
    }

    @Override
    public void delete() {
        if (getType() == _TYPE_INSERT) {
            remove(vc.getSelectedIndices());
        } else {
            if (isTaskRunning())
                return;
                
            List<String> keys = getSelectedItemKeys();
            if (keys.size() > 0) {
                
                if (!GUI.getInstance().displayQuestion("msgDeleteQuestion"))
                    return;
                
                task = new DeleteItemTask();
                task.setModule(getModule().getIndex());
                task.addClient(this);
                
                DcModule m = getModule();
                DcObject dco;
                for (String key : keys) {
                    dco = m.getItem();
                    dco.setValueLowLevel(DcObject._ID, key);
                    task.addItem(dco);
                    task.addClient(new UIClient(UIClient._DELETE, dco, true, true));
                }
                
                Connector connector = DcConfig.getInstance().getConnector();
                connector.executeTask(task);
            } else {
                GUI.getInstance().displayWarningMessage(DcResources.getText("msgSelectItemToDel"));
            }
        }
    }

    @Override
    public boolean isChangesSaved() {
        if (!checkForChanges)
            return true;
        
        cancelEdit();
        boolean saved = vc.isChangesSaved();
        if (childView != null)
            saved &= childView.isChangesSaved();
        
        return saved;
    }

    @Override
    public int update(String ID) {
        return vc.update(ID);
    }  
    
    @Override
    public int update(String ID, DcObject dco) {
        return vc.update(ID, dco);
    }
    
    public void repaintQuickViewImage() {
        if (quickView != null)
            quickView.reloadImage();
    }
    
    private void save(List<DcObject> items) {
        if (items.size() > 0) {
            
            task = new SaveItemTask();
            task.addItems(items);
            task.setModule(getModule().getIndex());
            task.startTask();
            task.addClient(this);
            
            for (DcObject dco : items) {
                task.addClient(new UIClient(
                        getType() == _TYPE_SEARCH ? UIClient._UPDATE : UIClient._INSERT, dco, true, true));
            }
            
            Connector connector = DcConfig.getInstance().getConnector();
            connector.executeTask(task);

        } else {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgNoChangesToSave"));
        }
    }

    @Override
    public void save() {
        if (!isTaskRunning()) {
        	
        	List<DcObject> items = new ArrayList<DcObject>();
        	if (getType() == _TYPE_SEARCH) {
        		int[] indices = vc.getChangedIndices();
        		for (int index : indices) 
        			items.add(getItemAt(index));
                
                if (getChildView() != null) { 
                    for (DcObject child : getChildView().getChangedItems()) 
                        items.add(child);
                }
        	} else {
        		items.addAll(getItems());
        	}
        	
        	save(items);
        }        
    }

    @Override
    public void saveSelected() {
        if (!isTaskRunning()) {
            List<DcObject> items = new ArrayList<DcObject>();
            
            if (getType() == _TYPE_SEARCH) {
                
                if (getChildView() != null)
                    items.addAll(getChildView().getChangedItems());
                
                // reversed order; make sure that GUI is updated after the children have been saved
                // for correct representation in the quick view (for example).
                items.addAll(getSelectedItems());
            } else {
                items.addAll(getSelectedItems());
            } 
            
            save(items);
        }        
    }

    @Override
    public void clear() {
        clear(true);
    }
    
    @Override
    public void saveSettings() {
        vc.saveSettings();
        
        if (childView != null)
            childView.saveSettings();
    }
    
    @Override
    public void applySettings() {
        setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        
        vc.applySettings();
        
        if (quickView != null) {
        	quickView.refresh();
            quickView.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontBold));
            quickView.setVisible(DcSettings.getBoolean(DcRepository.Settings.stShowQuickView));
        }
        
        if (groupingPane != null) {
            boolean treeVisibible = groupingPane.isVisible();
            boolean treeVisibibleSett = DcSettings.getBoolean(DcRepository.Settings.stShowGroupingPanel);
            
            groupingPane.setVisible(treeVisibibleSett);
            if (!treeVisibible && treeVisibibleSett)
                groupingPane.groupBy();
            else 
            	groupingPane.updateView();
            
            groupingPane.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontBold));
        }
        
        if (childView != null)
            childView.applySettings();
        
        applyViewDividerLocation();
    }

    public int getItemCount() {
        return vc.getItemCount();
    }
    
    @Override
    public boolean isLoaded() {
        return vc.getItemCount() > 0;
    }

    public boolean isActionsAllowed() {
        return actionsAllowed;
    }
    
    @Override
    public void setActionsAllowed(boolean b) {
        this.actionsAllowed = b;
        
        Cursor cursor = b ? new Cursor(Cursor.DEFAULT_CURSOR) : new Cursor(Cursor.WAIT_CURSOR);
        vc.setCursor(cursor);
        
        if (b) {
            for (MouseListener ml : vc.getMouseListeners()) {
                if (ml == vml)
                    vc.removeMouseListener(ml);
            }
            
            vc.addMouseListener(vml);
        } else { 
            vc.removeMouseListener(vml);
        }

        if (actionPanel != null)
            actionPanel.setEnabled(b);
    }
    
    public List<DcObject> getItems() {
        List<DcObject> items = new ArrayList<DcObject>();
        for (int i = 0; i < getItemCount(); i++)
            items.add(getItemAt(i));
        
        return items;
    }    

    public DcObject getItemAt(int idx) {
        vc.cancelEdit();
        DcObject dco = vc.getItemAt(idx);
         
        if (dco != null) {
            if (isParent() && getType() == View._TYPE_INSERT) {
                dco.removeChildren();
                dco.setChildren(((CachedChildView) childView).getChildren(dco.getID()));
            }
        }        
        
        return dco;
    }

    public DcObject getItem(String ID) {
        return vc.getItem(ID);
    }
    
    @Override
    public void setSelected(int index) {
        if (vc.getSelectedIndex() == index) {
            if (quickView != null) 
                quickView.refresh();
        } else if (vc.getItemCount() > 0 && index > -1) { 
	        vc.setSelected(index);
	        afterSelect(index);
    	}
    }
    
    @Override
    public List<? extends DcObject> getSelectedItems() {
        List<DcObject> items = new ArrayList<DcObject>();
        for (int row : getSelectedRows())
            items.add(getItemAt(row));
        
        return items;
    }
    
    @Override
    public List<String> getSelectedItemKeys() {
        return vc.getSelectedItemKeys();
    }
    
    @Override
    public List<String> getItemKeys() {
        return vc.getItemKeys();
    }

    @Override
    public DcObject getSelectedItem() {
        if (vc.getSelectedIndex() > -1)
            return getItemAt(vc.getSelectedIndex());
        else
            return null;
    }

    @Override
    public void remove(String[] keys) {
        // remove it from the view
        setListSelectionListenersEnabled(false);
        vc.setIgnorePaintRequests(true);
        
        if (vc.remove(keys)) {
            
            if (actionsAllowed) {
                setDefaultSelection();
            } else if (quickView != null) {
                quickView.clear();
            }
            
            if (isParent() && childView instanceof CachedChildView) {
                for (String ID : keys)
                    ((CachedChildView) childView).removeChildren(ID);
            }
            
            if (getItemCount() == 0) {
                if (quickView != null) quickView.clear();
                if (isParent()) childView.clear(false);
            }
        }
        
        vc.setIgnorePaintRequests(false);
        setListSelectionListenersEnabled(true);
    }

    public void remove(int[] indices) {
        String[] keys = new String[indices.length];
        int i = 0;
        
        for (int index : indices) {
            keys[i++] = vc.getItemKey(index);
        }
        remove(keys);
    }

    public void showQuickView(boolean b) {
        quickView.setVisible(b);
    }

    public int[] getSelectedRows() {
        return vc.getSelectedIndices();
    }
    
    @Override
    public List<? extends DcObject> getChangedItems() {
        List<DcObject> objects = new ArrayList<DcObject>();
        for (int idx : vc.getChangedIndices()) 
            objects.add(getItemAt(idx));
        
        return objects;
    }

    @Override
    public void removeFromCache(String key) {
        DcObject dco = vc.getItem(key);
        if (dco != null) dco.markAsUnchanged();
    }

    public DcObject getDcObject(String key) {
        return vc.getItem(key);
    }
    
    public void cancelEdit() {
        vc.cancelEdit();
    }
    
    @Override
    public void loadChildren() {
        if (isParent())
            childView.loadChildren();
        else if (isChild() && parentID != null) {
            clear();
            Connector connector = DcConfig.getInstance().getConnector();
            add(connector.getChildrenKeys(parentID, getModule().getIndex()));
        }
    }
    
    /**
     * Note that the items only have to be shown after a select. 
     */
    @Override
    public void setParentID(String ID, boolean show) {
        this.parentID = ID;
    }
    
    @Override
    public String getParentID() {
        return parentID;
    }
    
    public void afterSelect(int idx) {
        String key = vc.getItemKey(idx);
        
        if (key == null) return;
        
        int module = vc.getModule(idx);
        if (isParent() && actionsAllowed) {
            childView.setParentID(key, true);
            if (!(childView instanceof CachedChildView))
                loadChildren();
        }

        if (!isChild() && quickView != null && quickView.isVisible() && isActionsAllowed()) {
            quickView.setObject(key, module); 
        }
    }
    
    protected Collection<Component> getAdditionalActions() {
        ArrayList<Component> components = new ArrayList<Component>();
        if (isParent()) {
            try {
                JButton btAddChild = ComponentFactory.getButton(DcResources.getText("lblAddChild", getModule().getChild().getObjectName()));
                btAddChild.addActionListener(Plugins.getInstance().get("AddChild", null, null, getIndex(), getModule().getIndex(), getType()));
                btAddChild.setMnemonic('T');
                components.add(btAddChild);
            } catch (InvalidPluginException ipe) {
                logger.error(ipe, ipe);
            }
        }
        return components;
    }    

    private void addChildView() {
        if (childView != null) {
            childView.setVisible(true);
            panelResult.add(spChildView,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                            new Insets(5, 5, 5, 5), 0, 0));
        }
    }
    
    private void build() {
        //**********************************************************
        //Search result panel
        //**********************************************************
        panelResult.setLayout(Layout.getGBL());
        panelResult.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ViewScrollPane scroller1 = new ViewScrollPane(this);
        scroller1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelResult.add(scroller1,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        
        if (isParent()) {
            spChildView = new ViewScrollPane((View) childView);
            spChildView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            spChildView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            addChildView();
        }
        
        //**********************************************************
        //Main panel
        //**********************************************************
        setLayout(Layout.getGBL());
        
        if (actionPanel != null)
            add(    actionPanel,    Layout.getGBC( 0, 2, 3, 1, 1.0, 1.0
                    ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 0, 5), 0, 0));

        ToolTipManager.sharedInstance().registerComponent((JComponent) vc);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        
        if (vc.getSelectedIndex() == -1) return;
        
        if (actionsAllowed)
            afterSelect(vc.getSelectedIndex());
    }

    @Override
    public void applyGrouping() {
    	groupBy();
    }

    @Override
    public void setCheckForChanges(boolean b) {
        checkForChanges = b;
    }

    @Override
    public void notify(String msg) {
    	logger.info(msg);
    }

    @Override
    public void notifyError(Throwable t) {
        logger.error(t, t);
        GUI.getInstance().displayErrorMessage(t.getMessage());
    }

    @Override
    public void notifyWarning(String msg) {
    	logger.warn(msg);
    	GUI.getInstance().displayWarningMessage(msg);
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {}

    @Override
    public void notifyTaskStarted(int taskSize) {}

    @Override
    public void notifyProcessed() {}

    @Override
    public boolean isCancelled() {
        return false;
    }
}
