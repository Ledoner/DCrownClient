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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.datacrow.core.objects.DcImageIcon;

public class NodeElement {

    protected Object key;
    protected String displayValue;
    protected DcImageIcon icon;
    
    private Map<String, Integer> items = new LinkedHashMap<String, Integer>();
    
    public NodeElement(Object key, String displayValue, DcImageIcon icon) {
        this.key = key;   
        this.displayValue = displayValue;
        this.icon = icon != null ? new DcImageIcon(icon.getBytes()) : icon;
    }
    
    public void setKey(Object key) {
        this.key = key;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public void setIcon(DcImageIcon icon) {
        this.icon = icon != null ? new DcImageIcon(icon.getBytes()) : icon;
    }
    
    public void addItem(String item, Integer moduleIdx) {
    	if (!items.containsKey(item) && item != null)
    		items.put(item, moduleIdx);
    }
    
    public void removeItem(String item) {
    	items.remove(item);
    }
    
    public int getCount() {
        return items != null ? items.size() : 0;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public Map<String, Integer> getItems() {
    	return items;
    }
    
    public Map<String, Integer> getItemsSorted(List<String> allOrderedItems) {
    	
    	Map<String, Integer> items = getItems();
    	
    	Map<String, Integer> result = new LinkedHashMap<String, Integer>();
    	
    	if (allOrderedItems.size() > 0) {
        	for (String orderedItem : allOrderedItems) {
        		for (String item : items.keySet()) {
        			if (item.equals(orderedItem)) {
        				result.put(item, items.get(item));
        			}
        		}
        	}
        	return result;
    	} else {
    	    return items;
    	}
    }
    
    public void setItems(Map<String, Integer> items) {
    	this.items = items;
    }

    public String getComparableKey() {
        return getDisplayValue() == null ? "" : getDisplayValue().toLowerCase();
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public Object getKey() {
        return key;
    }
    
    public void clear() {
        if (icon != null) icon.flush();
        
        key = null;
        icon = null;
        displayValue = null;
        
        if (items != null) {
        	items.clear();
        	items = null;
        }
    }

    @Override
    public String toString() {
        return getDisplayValue() + " (" + String.valueOf(getCount()) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof NodeElement))
            return false;
        else 
            return getComparableKey().equals(((NodeElement) o).getComparableKey());
    }

    @Override
    public int hashCode() {
        return getComparableKey().hashCode();
    }

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
	}
}
