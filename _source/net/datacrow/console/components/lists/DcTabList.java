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

package net.datacrow.console.components.lists;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;

import net.datacrow.console.components.lists.elements.DcListElement;
import net.datacrow.console.components.lists.elements.DcTabListElement;
import net.datacrow.console.components.renderers.DcListRenderer;
import net.datacrow.console.views.ISortableComponent;
import net.datacrow.tabs.Tab;

public class DcTabList extends DcList implements ISortableComponent {
    
    public DcTabList() {
        super(new DcListModel());
        setCellRenderer(new DcListRenderer(true));
        setLayoutOrientation(JList.VERTICAL_WRAP);
    }    

    public List<Tab> getTabs() {
        List<Tab> tabs = new ArrayList<Tab>();
        for (DcListElement element : getElements())
            tabs.add(((DcTabListElement) element).getTab());

        return tabs;
    }
    
    public List<Tab> getSelectedTabs() {
        int[] rows = getSelectedIndices();
        Object element;
        
        List<Tab> tabs = new ArrayList<Tab>();
        
        if (rows != null) {
            for (int row : rows) {
                element = getModel().getElementAt(row);
                tabs.add(((DcTabListElement) element).getTab());
            }
        }
        
        return tabs;
    }
    
    public Tab getSelectedTab() {
        DcTabListElement element = (DcTabListElement) getSelectedValue();
        return element != null ? element.getTab() : null;
    }
    
    public void add(Tab tab) {
        getDcModel().addElement(new DcTabListElement(tab));
        ensureIndexIsVisible(getModel().getSize());
    }
    
    public void remove(Tab tab) {
        for (DcListElement element : getElements()) {
            if (((DcTabListElement) element).getTab().equals(tab))
                getDcModel().removeElement(element);                
        }
    }    
}
