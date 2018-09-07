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

package net.datacrow.console.windows;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.objects.helpers.MusicTrack;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

public class CreateMultipleItemsDialog extends DcDialog implements ActionListener {
    
	private DcTable table;
	
	private int moduleIdx;
	
	private JButton buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAdd"));
    private JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
    private JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblClose"));
	
	private SavingTask task;
	
	public CreateMultipleItemsDialog(int moduleIdx) {
		super();
		
		this.moduleIdx = moduleIdx;
		this.table = new DcTable(DcModules.get(moduleIdx), false, false);
		
		setTitle(DcResources.getText("lblAddMultiple"));
		
		build();
		
		pack();
		setCenteredLocation();
	}
	
	private void save() {
		if (task == null || !task.isAlive()) {
			task = new SavingTask();
			task.start();
		}
	}
	
	private void add() {
		DcModule module = DcModules.get(moduleIdx);
		DcObject dco = module.getItem();
		if (module.isChildModule()) {
		    String parentID;
			if (GUI.getInstance().getRootFrame() instanceof ItemForm)
				parentID = ((ItemForm) GUI.getInstance().getRootFrame()).getItem().getID();
			else
				parentID = GUI.getInstance().getSearchView(module.getIndex()).getCurrent().getSelectedItem().getID();
			
			dco.setValue(dco.getParentReferenceFieldIndex(), parentID);
		}
		table.add(dco);
	}
	
	public void setActionsAllowed(boolean b) {
		buttonAdd.setEnabled(b);
		buttonSave.setEnabled(b);
		buttonCancel.setEnabled(b);
	}
	
	@Override
    public void close() {
		if (task == null || !task.isAlive()) {
			buttonAdd = null;
			buttonSave = null;
			buttonCancel = null;
			task = null;
			super.close();
		}
    }

	private void build() {
        JScrollPane sp = new JScrollPane(table);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        table.setDynamicLoading(false);
        table.activate();
        
        if (DcModules.get(moduleIdx) instanceof DcPropertyModule) {
        	table.setIgnoreSettings(true);
        	table.setVisibleColumns(new int[] {DcProperty._A_NAME});
        } else if (moduleIdx == DcModules._MUSIC_TRACK) {
            table.setIgnoreSettings(true);
            table.setVisibleColumns(new int[] {
                    MusicTrack._F_TRACKNUMBER, 
                    MusicTrack._A_TITLE,
                    MusicTrack._J_PLAYLENGTH});
        }
        
        getContentPane().setLayout(Layout.getGBL());
        
        getContentPane().add(sp,  Layout.getGBC( 0, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(0, 0, 0, 0), 0, 0));

        JPanel panelActions = new JPanel();
        
        buttonAdd.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonCancel.addActionListener(this);
        
        buttonAdd.setActionCommand("add");
        buttonSave.setActionCommand("save");
        buttonCancel.setActionCommand("cancel");
        
        panelActions.add(buttonAdd);
        panelActions.add(buttonSave);
        panelActions.add(buttonCancel);
        
        getContentPane().add(panelActions, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("save"))
			save();
		else if (ae.getActionCommand().equals("cancel"))
			close();
		else if (ae.getActionCommand().equals("add"))
			add();
    }
	
	private class SavingTask extends Thread {
		@Override
		public void run() {
		    DcObject dco;
		    Connector connector = DcConfig.getInstance().getConnector();
			for (int row = table.getRowCount(); row > 0; row--) {
				dco = table.getItemAt(row - 1);
				dco.setIDs();
				
				try {
					if (connector.saveItem(dco) &&
					    GUI.getInstance().getSearchView(dco.getModuleIdx()) != null) {
					    GUI.getInstance().getSearchView(dco.getModuleIdx()).add(dco);
					}
					
					table.removeRow(row - 1);
				} catch (ValidationException e) {
				    GUI.getInstance().displayWarningMessage(e.getMessage());
				}
			}
		}
	}	
}