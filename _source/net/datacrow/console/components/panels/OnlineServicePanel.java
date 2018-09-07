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

package net.datacrow.console.components.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.OnlineServices;
import net.datacrow.core.services.Region;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.plugin.IServer;

public class OnlineServicePanel extends JPanel implements ActionListener {
    
    private JComboBox comboServers = ComponentFactory.getComboBox();
    private JComboBox comboRegions = ComponentFactory.getComboBox();
    private JComboBox comboModes = ComponentFactory.getComboBox();
    
    private boolean modeSelectionAllowed = false;
    
    private JCheckBox checkUseOnlineService = ComponentFactory.getCheckBox(DcResources.getText("lblUseOnlineService"));

    public OnlineServicePanel(OnlineServices servers, boolean modeSelectionAllowed, boolean toggle) {
        this.modeSelectionAllowed = modeSelectionAllowed;
        build(servers, toggle);
    }
    
    public boolean useOnlineService() {
        return checkUseOnlineService.isSelected();
    }
    
    public IServer getServer() {
        return checkUseOnlineService.isSelected() ? (IServer) comboServers.getSelectedItem() : null;
    }
    
    public Region getRegion() {
        return checkUseOnlineService.isSelected() ? (Region) comboRegions.getSelectedItem() : null;
    }

    public SearchMode getMode() {
        return checkUseOnlineService.isSelected() && modeSelectionAllowed ? 
                (SearchMode) comboModes.getSelectedItem() : null;
    }
    
    public void setServer(String name) {
        IServer server;
        for (int idx = 0; idx < comboServers.getItemCount(); idx++) {
            server = (IServer) comboServers.getItemAt(idx);
            if (server.getName().equals(name))
                comboServers.setSelectedItem(server);
        }
    }
    
    public void setRegion(String code) {
        if (comboRegions == null || comboRegions.getItemCount() == 0) return;
        
        Region region;
        for (int idx = 0; idx < comboRegions.getItemCount(); idx++) {
            region = (Region) comboRegions.getItemAt(idx);
            if (region.getCode().equals(code))
                comboRegions.setSelectedItem(region);
        }
    }
    
    public void setMode(String displayName) {
        if (comboModes == null || comboModes.getItemCount() == 0) return;
        
        SearchMode mode;
        for (int idx = 0; idx < comboModes.getItemCount(); idx++) {
            mode = (SearchMode) comboModes.getItemAt(idx);
            if (mode.getDisplayName().equals(displayName))
                comboModes.setSelectedItem(mode);
        }
    }    
    
    private void applyServer() {
        IServer server = getServer();
        comboRegions.removeAllItems();
        comboModes.removeAllItems();

        if (server != null) {
        	for (Region region : server.getRegions())
        		comboRegions.addItem(region);
        	
        	if (server.getSearchModes() != null) {
            	for (SearchMode mode : server.getSearchModes())
        	        comboModes.addItem(mode);
        	}
        	
        	comboModes.setVisible(comboModes.getItemCount() > 0);
        }
        
        repaint();
    }    
    
    public void clear() {
        comboServers = null;
        comboRegions = null;
        comboModes = null;
        checkUseOnlineService = null;
        removeAll();
    }
    
    public void setUseOnlineService(boolean b) {
        checkUseOnlineService.setSelected(b);
    }
    
    private void toggleServer() {
        boolean b = checkUseOnlineService.isSelected();
        comboRegions.setEnabled(b);
        comboServers.setEnabled(b);
        comboModes.setEnabled(b);
    } 
    
    private void build(OnlineServices servers, boolean toggle) {
        setLayout(Layout.getGBL());

        for (IServer server : servers.getServers())
            comboServers.addItem(server);
        
        comboServers.addActionListener(this);
        comboServers.setActionCommand("applyServer");
        
        checkUseOnlineService.addActionListener(this);
        checkUseOnlineService.setActionCommand("toggleServer");
        
        if (toggle)
            add(checkUseOnlineService, Layout.getGBC(0, 0, 2, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        add(comboServers, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
             new Insets(5, 5, 5, 5), 0, 0));
         add(comboRegions, Layout.getGBC(1, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
             new Insets(5, 5, 5, 5), 0, 0));
         
         if (modeSelectionAllowed)
             add(comboModes, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblOnlineServiceConfig")));
        
        checkUseOnlineService.setSelected(true);
        comboServers.setSelectedIndex(0);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("toggleServer"))
            toggleServer();
        else if (e.getActionCommand().equals("applyServer"))
            applyServer();
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clear();
    }
}   