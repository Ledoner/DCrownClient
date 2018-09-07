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

package net.datacrow.tabs;

import net.datacrow.core.objects.DcImageIcon;

public class Tab implements Comparable {

    public static final String _MODULE = "module";
    public static final String _NAME = "name";
    public static final String _ICON = "icon";
    public static final String _ORDER = "order";
    
	private final int module;
	
	private String name;
	private int order;
	private DcImageIcon icon;
	   
	public Tab(
	        int module,
	        String name,
	        DcImageIcon icon) {
	    
	    this.module = module;
	    this.name = name;
	    this.icon = icon;
	}
	
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getModule() {
        return module;
    }
    
    public DcImageIcon getIcon() {
        return icon;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
 
    public void setIcon(DcImageIcon icon) {
        this.icon = icon;
    }

    @Override
    public int compareTo(Object o) {
        return getOrder() - ((Tab) o).getOrder();
    }
}
