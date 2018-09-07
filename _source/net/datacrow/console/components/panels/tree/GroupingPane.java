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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.views.MasterView;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IGroupingPane;
import net.datacrow.core.console.ITreePanel;
import net.datacrow.core.console.IView;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.settings.DcSettings;

public class GroupingPane extends JPanel implements ChangeListener, IGroupingPane {

	private List<ITreePanel> panels = new ArrayList<ITreePanel>();
    
	private int current = 0;
    private int module;
    private MasterView view;
    
    private JTabbedPane tp;
    
    public GroupingPane(int module, MasterView view) {
        this.module = module;
        this.view = view;
        
        if (module == DcModules._CONTAINER) 
            panels.add(new ContainerTreePanel(this));
        else 
            panels.add(new FieldTreePanel(this));
        
        if (DcModules.get(module).isFileBacked())
            panels.add(new FileTreePanel(this));
    }
    
    @Override
	public boolean isEnabled() {
		return super.isEnabled() && DcSettings.getBoolean(DcRepository.Settings.stShowGroupingPanel);
	}
    
    /** 
     * Apply the user / system settings. The settings are applied on every {@link ITreePanel}.
     */
    @Override
    public void applySettings() {
        for (ITreePanel tp : panels)
            tp.applySettings();
        
        build();
    }
    
    /**
     * Update the tree with the item information.
     * @param dco   the item to reflect in the grouping pane / tree.
     */
    @Override
    public void updateTreeNodes(DcObject reference) {
        for (ITreePanel tp : panels)
            tp.updateTreeNodes(reference);
    }
    
    /**
     * Updates the Grouping Pane and its tree panels ({@link ITreePanel} with the item information.
     * @param dco   the item to use for the update.
     */
    @Override
    public void update(DcObject dco) {
    	for (ITreePanel tp : panels)
    		tp.update(dco);
    }
    
    /**
     * Indicates whether the grouping pane has been loaded
     * @return  loaded y/n
     */
    @Override
    public boolean isLoaded() {
        boolean loaded = false;
        for (ITreePanel tp : panels)
            loaded |= tp.isLoaded();
        
        return loaded;
    }
    
    /**
     * Removes the item with the specified key from the grouping pane and {@link IView}.
     * The item is removed from every {@link ITreePanel}.
     * @param key   the item ID
     */
    @Override
    public void remove(String item) {
    	for (ITreePanel tp : panels)
    		tp.remove(item);
    }
    
    /**
     * Adds the item to the Grouping Pane and its tree panels ({@link ITreePanel}.
     * @param dco   the item to add.
     */
    @Override
    public void add(DcObject dco) {
    	for (ITreePanel tp : panels)
    		tp.add(dco);
    }
    
    public boolean isHoldingItems() {
        for (ITreePanel tp : panels) {
            return tp.isHoldingItems();
        }
        
        return false;
    }
    
    public int getModule() {
        return module;
    }
    
    /**
     * Returns the view of which the grouping pane is part.
     * @return  the master view
     */
    @Override
    public MasterView getView() {
        return view;
    }
    
    /**
     * Clears the grouping pane and the view of which this pane is a part.
     */
    @Override
    public void clear() {
        for (ITreePanel tp : panels) {
            tp.clear();
        }
    }
    
    /**
     * Retrieves the currently selected {@link ITreePanel}
     * @return  the current tree panel
     */
    @Override
    public ITreePanel getCurrent() {
    	return current < 0 ? null : panels.get(current);
    }
    
    /**
     * Loads the view of which this grouping pane is part.
     */
    @Override
    public void load() {
        for (ITreePanel tp : panels) {
            // the current always must be updated and the activate panels need to be kept in synch
            // as well to make sure it reflects the current filter
            if (tp.isActivated() || tp == getCurrent())
                tp.groupBy();
        }
    }
    
    /**
     * Update the current view.
     * @see IView
     */
    @Override
    public void updateView() {
        TreePanel tp;
        for (ITreePanel itp : panels) {
            tp = (TreePanel) itp;
            
            if (tp.isEnabled() && tp.isShowing()) {
            	DcDefaultMutableTreeNode node = (DcDefaultMutableTreeNode) 
            	        ((TreePanel) tp).getLastSelectedPathComponent();
            	if (node != null)
            		tp.updateView(node.getItemsSorted(tp.top.getItemList()));
            	else 
            		tp.setDefaultSelection();
            } else if (tp.isShowing()) {
            	DcDefaultMutableTreeNode node = (DcDefaultMutableTreeNode) tp.getLastSelectedPathComponent();
            	if (node == null) 
            		tp.setDefaultSelection();
            	if (node != null)
            		tp.updateView(tp.top.getItems());
            }
        }
    }
    
    /**
     * Sorts the items in the view of which this pane is a part.
     */
    @Override
    public void sort() {
        for (ITreePanel tp : panels) {
        	if (tp.isEnabled() && tp.isLoaded()) {
	            tp.sort();
	            
	            if (tp == getCurrent())
	                tp.refreshView();
        	}
        }
    }
    
    /**
     * Applies the grouping.
     */
    @Override
    public void groupBy() {
        for (ITreePanel tp : panels)
    		tp.groupBy();
    }
    
    /**
     * Indicates whether changes should be saved when changing the view. 
     * As a new node is selected, the view is cleared. In case the view contains items
     * which have been changed (but not yet saved) these will be saved depending on this
     * setting.
     * @param b save changes y/n
     */
    @Override
    public void saveChanges(boolean b) {
        for (ITreePanel tp : panels)
    		tp.setSaveChanges(b);
    }
    
    private void build() {
        
        if (tp != null) {
            tp.removeChangeListener(this);
            remove(tp);
        }
        
        tp = ComponentFactory.getTabbedPane();
        tp.addChangeListener(this);
        
        for (ITreePanel panel : panels)
            tp.addTab(panel.getName(), (Component) panel);
        
        setLayout(Layout.getGBL());
        
        add(tp, Layout.getGBC( 0, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(0,0,0,0), 0, 0));
    }
    
    @Override
	public void stateChanged(ChangeEvent ce) {
        JTabbedPane pane = (JTabbedPane) ce.getSource();
        current = pane.getSelectedIndex();
        panels.get(current).activate();
	}

    /**
     * Applies the font to the Grouping Pane and its tree panels ({@link ITreePanel}).
     * @param font  the font to use.
     */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		
		if (panels != null) {
			for (ITreePanel panel : panels)
				panel.setFont(font);
		}
		
		for (Component c : getComponents()) 
			c.setFont(font);
	}
}
