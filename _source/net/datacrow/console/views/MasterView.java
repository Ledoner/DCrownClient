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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.panels.tree.GroupingPane;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IGroupingPane;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.IView;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;

/**
 * Interface for the MasterView. The master view manages the various view types for a single
 * {@link DcModule}. The module dictates the available views, which are then registered here.
 * The MasterView manages the updates, removals and addition of items to the views it manages.
 * 
 * @author Robert Jan van der Waals
 */
public class MasterView implements IMasterView {

    private final Map<Integer, IView> views = new HashMap<Integer, IView>();
    private IGroupingPane groupingPane;
    
    private String defaultViewSettingsKey;
    
    private int module;
    
    public MasterView(int module, String defaultViewSettingsKey) {
    	this.module = module;
    	this.defaultViewSettingsKey = defaultViewSettingsKey;
    }
    
    public void setTreePanel(DcModule module) {
        if (!module.isChildModule())
            this.groupingPane = new GroupingPane(module.getIndex(), this);
        else if (module.getIndex() == DcModules._ITEM)
            this.groupingPane = new GroupingPane(DcModules._CONTAINER, this);
    }
    
    /**
     * Returns the grouping pane. Returns NULL in case there is no grouping pane. 
     * @return  the grouping pane or NULL.
     */
    @Override
    public IGroupingPane getGroupingPane() {
    	return groupingPane;
    }
    
    public JPanel getViewPanel() {
        JPanel panel = new JPanel();
        IView view = getCurrent();
        panel.setLayout(Layout.getGBL());
        panel.add( (JComponent) view, Layout.getGBC( 0, 0, 2, 1, 2.0, 2.0
                  ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                   new Insets(5, 5, 5,  5), 0, 0));
        
        return panel;
    }
    
    /**
     * Make the view with the supplied index active.
     * @param index the view index; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW}
     */
    @Override
    public void setView(int index) {
    	GUI.getInstance().getMainFrame().applyView(index);
        if (groupingPane != null)
            groupingPane.saveChanges(false);
    }
    
    /** 
     * Retrieves the view
     * @param index the view index; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW} 
     * @return  the view instance
     */
    @Override
    public IView get(int index) {
        return views.get(index);
    }
    
    /**
     * Refreshes the quick view information.
     */
    @Override
    public void refreshQuickView() {
        for (IView view : views.values()) {
            view.refreshQuickView();
        }
    }
    
    public void setBusy(boolean b) {
        for (IView view : views.values()) {
            view.setCursor(b ? ComponentFactory._CURSOR_WAIT : ComponentFactory._CURSOR_NORMAL );
            view.setActionsAllowed(!b);
            view.setCheckForChanges(!b);
        }
    }
    
    /**
     * Retrieves the module to which the master view belongs.
     * @return  the module
     */
    @Override
    public DcModule getModule() {
    	return DcModules.get(module);
    }
    
    /**
     * Retrieves the currently selected view.
     * @return  the selected view
     */
    @Override
    public IView getCurrent() {
        int view = getModule().getSettings().getInt(defaultViewSettingsKey);
        IView current = get(view);
        
        // Get the first available view if the view cannot be found (for whatever reason)
        if (current == null) {
            for (Integer key : views.keySet()) { 
                current = views.get(key);
                
                if (current != null)
                    getModule().getSettings().set(defaultViewSettingsKey, Long.valueOf(current.getIndex())); 
                
                break;
            }
        }
        
        return current;
    }      
    
    /**
     * Indicates whether the view currently selected has been loaded / holds items.
     * @return  loaded y/n
     */
    @Override
    public boolean isLoaded() {
        return (groupingPane != null && groupingPane.isLoaded()) || getCurrent().isLoaded();
    }
    
    /**
     * Updates the information in the view for the supplied item.
     * @param dco   the item to update in the view.
     */
    @Override
    public void update(DcObject dco) {
        if (groupingPane != null && groupingPane.isEnabled())
            groupingPane.update(dco);
        
        for (IView view : getViews()) {
            int index = view.update(dco.getID());
            if (view == getCurrent())
                view.setSelected(index);
        }
    }

    /**
     * Adds a single item to the view(s).
     * @param dco   the item to add.
     */
    @Override
    public void add(DcObject dco) {
        add(dco, true);
    }
    
    /**
     * Adds a single item to the view(s).
     * @param dco   the item to add.
     * @param select    indicate whether the item should be selected after it has been added
     */
    public void add(DcObject dco, boolean select) {
        if (groupingPane != null && groupingPane.isEnabled()) {
            groupingPane.add(dco);
            if (select) {
                groupingPane.getCurrent().setSelected(dco);
                groupingPane.getCurrent().refreshView();
                groupingPane.getView().getCurrent().setSelected(groupingPane.getView().getCurrent().getIndex(dco.getID()));
            }
            
        } else {
            for (IView view : getViews())
                if (view.isLoaded() || view == getCurrent())
                    view.add(dco);
        }
    }
    
    /**
     * Removes the item from the view.
     * @param key   the ID of the item.
     */
    @Override
    public void remove(String key) {
        
        for (IView view : getViews())
            view.remove(new String[] {key});

        if (groupingPane != null && groupingPane.isEnabled())
            groupingPane.remove(key);
    }
    
    /**
     * Adds a view to this master view. This method is mainly used by the {@link DcModule}/
     * @param index index of this view; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW} 
     * @param view  the view to add.
     */
    @Override
    public void addView(int index, IView view) {
        views.put(index, view);        
    }

    /**
     * Save the settings for the views.
     */
    @Override
    public void saveSettings() {
        for (IView view : getViews())
            view.saveSettings();
    }    
    
    /**
     * Apply settings to the views this master view manages
     */
    @Override
    public void applySettings() {
        for (IView view : getViews())
            view.applySettings();
        
        if (groupingPane != null)
            groupingPane.applySettings();
    }
    
    /**
     * Sort the items in the view, based on the {@link DcRepository.ModuleSettings#stSearchOrder}
     */
    @Override
    public void sort() {
    	if (getGroupingPane() == null || !getGroupingPane().isEnabled()) {
    		getCurrent().sort();
    	} else {
    		groupingPane.sort();
    	}
    }
    
    /**
     * Refresh the views.
     */
    @Override
    public void refresh() {
        if (groupingPane != null && groupingPane.isEnabled()) {
            groupingPane.groupBy();
        } else {
            clear();
            
            Connector connector = DcConfig.getInstance().getConnector();
            add(connector.getKeys(DataFilters.getCurrent(module)));
        }
    }

    public void removeFromCache(String ID) {
        for (IView view : getViews())
            view.removeFromCache(ID);
    }    
    
    /**
     * Clear the views.
     */
    @Override
    public void clear() {
        for (IView view : getViews())
            view.clear();
        
        if (groupingPane != null)
            groupingPane.clear();
    }

    public void clear(boolean saveChanges) {
        for (IView view : getViews())
            view.clear(saveChanges);

        if (groupingPane != null && groupingPane.isEnabled())
            groupingPane.clear();
    }
    
    /**
     * Adds the items to the view(s). The items are added based on their key.
     * The views are responsible for loading the item information.
     * @param keys  Key map, containing the ID of the item (String) and the module index (Integer).
     */
    @Override
    public void add(final Map<String, Integer> keys) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	for (IView view : getViews())
                    view.clear();
                
                if (groupingPane != null && groupingPane.isEnabled()) {
                    groupingPane.load();
                } else { 
                	for (IView view : getViews())
                		view.add(keys);
                }
            }
        });
    }
    
    /**
     * Returns all the views managed by this master view.
     * @return  the managed view collection.
     */
    @Override
    public Collection<IView> getViews() {
        Collection<IView> c = new ArrayList<IView>();
        c.addAll(views.values());
        return c;
    }
}
