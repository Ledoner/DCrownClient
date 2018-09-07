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

import net.datacrow.console.components.lists.elements.DcFilterEntryListElement;
import net.datacrow.console.components.lists.elements.DcListElement;
import net.datacrow.console.components.renderers.DcListRenderer;
import net.datacrow.core.data.DataFilterEntry;

public class DcFilterEntryList extends DcList {

    public DcFilterEntryList() {
        super(new DcListModel());
        
        setCellRenderer(new DcListRenderer(true));
        setLayoutOrientation(JList.VERTICAL_WRAP);
    }    

    public List<DataFilterEntry> getEntries() {
        List<DataFilterEntry> entries = new ArrayList<DataFilterEntry>();
        for (DcListElement element : getElements()) {
            entries.add(((DcFilterEntryListElement) element).getEntry());
        }
        return entries;
    }
    
    public void add(DataFilterEntry entry) {
        getDcModel().addElement(new DcFilterEntryListElement(entry));
        ensureIndexIsVisible(getModel().getSize());
    }
}
