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

import java.util.List;
import java.util.Map;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.helpers.Item;
import net.datacrow.core.server.Connector;

public class ContainerNodeElement extends NodeElement {

	public ContainerNodeElement(String key, String displayValue, DcImageIcon icon) {
		super(key, displayValue, icon);
		addItem(key, DcModules._CONTAINER);
	}

	@Override
	public Map<String, Integer> getItems() {
		if (	DcModules.get(DcModules._CONTAINER).getSettings().getInt(
				DcRepository.ModuleSettings.stTreePanelShownItems) == DcModules._ITEM) {
			
			DataFilter df = new DataFilter(DcModules._ITEM);
			df.addEntry(new DataFilterEntry(DcModules._ITEM, Item._SYS_CONTAINER, Operator.EQUAL_TO, getKey()));
			Connector connector = DcConfig.getInstance().getConnector();
			return connector.getKeys(df);
		} else {
			return super.getItems();
		}
	}
	
	@Override
    public Map<String, Integer> getItemsSorted(List<String> allOrderedItems) {
    	return getItems();
    }
	
    @Override
    public String toString() {
        return getDisplayValue();
    }
}
