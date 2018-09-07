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

package net.datacrow.console.windows.settings;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.panels.NavigationPanel;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.resources.DcResources;

public class CardViewPictureSettingsPanel extends JPanel {

	private final int module;
    private DcTable table;
    private NavigationPanel panelNav;
	
	public CardViewPictureSettingsPanel(int module) {
		this.module = module;
		build();
	}
	
	public void save() {
		int[] picOrder = new int[table.getRowCount()];
		for (int i = 0; i < table.getRowCount(); i++) {
			DcField field = (DcField) table.getValueAt(i, 0);
			picOrder[i] = field.getIndex();
		}
		DcModules.get(module).setSetting(DcRepository.ModuleSettings.stCardViewPictureOrder, picOrder);
	}
	
	private void build() {
        setLayout(Layout.getGBL());
        
        table = ComponentFactory.getDCTable(false, false);
        table.setColumnCount(1);

        TableColumn c = table.getColumnModel().getColumn(0);
        c.setHeaderValue(DcResources.getText("lblField"));

        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        NavigationPanel panelNav = new NavigationPanel(table);
        
        DcLongTextField textHelp = ComponentFactory.getHelpTextField();
        textHelp.setText(DcResources.getText("msgCardPictureSelectionHelp"));
        add(textHelp, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        add(scroller,  Layout.getGBC(0, 1, 1, 1, 5.0, 5.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));
        add(panelNav,  Layout.getGBC(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));
        
        Collection<DcField> fields = new ArrayList<DcField>();
        DcField fld;
        for (int fieldIdx : DcModules.get(module).getSettings().getIntArray(DcRepository.ModuleSettings.stCardViewPictureOrder)) {
            fld = DcModules.get(module).getField(fieldIdx);
            if (fld != null && fld.getValueType() == DcRepository.ValueTypes._PICTURE && !fields.contains(fld))
                fields.add(fld);
        }

        for (DcField field : DcModules.get(module).getFields()) {
            if (field.getValueType() == DcRepository.ValueTypes._PICTURE && !fields.contains(field))
                fields.add(field);
        }
        
        for (DcField field : fields)
            table.addRow(new Object[] {field});
	}
	
    protected void clear() {
        table = null;
        
        if (panelNav != null) {
        	panelNav.clear();
        	panelNav = null;
        }
    }
}
