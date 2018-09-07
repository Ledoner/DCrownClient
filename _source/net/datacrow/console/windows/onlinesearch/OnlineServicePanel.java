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

package net.datacrow.console.windows.onlinesearch;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.OnlineServices;
import net.datacrow.core.services.Region;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.plugin.IServer;

public class OnlineServicePanel extends JPanel implements ActionListener, KeyListener  {
    
    private JButton buttonSearch;
    private JTextField fldQuery;

    private JButton buttonStop = ComponentFactory.getButton(DcResources.getText("lblStop"));
    
    private JComboBox comboRegions = ComponentFactory.getComboBox(new DefaultComboBoxModel());
    private JComboBox comboModes = ComponentFactory.getComboBox(new DefaultComboBoxModel());
    private JComboBox comboServers = ComponentFactory.getComboBox(new DefaultComboBoxModel());
    
    private boolean listenForServerChanges = true;
    private boolean perfectMatchOccured = false;

    private OnlineSearchForm osf;
    private OnlineServices os;
    
    public OnlineServicePanel(OnlineSearchForm osf, OnlineServices os) {
        this.osf = osf;
        this.os = os;
        build(true);
        applySettings();
    }

    public void hasPerfectMatchOccured(boolean b) {
        perfectMatchOccured = b;
    }
    
    public boolean hasPerfectMatchOccured() {
        return perfectMatchOccured;
    }
    
    public String getQuery() {
        return fldQuery.getText().trim();
    }
    
    public void setQuery(String query) {
        fldQuery.setText(query);
    }    
    
    public SearchMode getMode() {
        IServer server = getServer();
        SearchMode mode = null;
        if (server.getSearchModes() != null && server.getSearchModes().size() > 0) {
            mode = (SearchMode) comboModes.getSelectedItem();
        }
        return mode;
    }
    
    public IServer getServer() {
        return (IServer) comboServers.getSelectedItem();
    }

    public Region getRegion() {
        IServer server = getServer();
        Region region = null;
        if (server.getRegions() != null && server.getRegions().size() > 0) {
            region = (Region) comboRegions.getSelectedItem();
        }
        return region;
    }
    
    protected void busy(boolean b) {
        if (b) {
            buttonSearch.setEnabled(false);
            buttonStop.setEnabled(true);
            buttonStop.requestFocus();
        } else {
            buttonSearch.setEnabled(true);
            buttonStop.setEnabled(false);

            if (perfectMatchOccured) {
                fldQuery.setText("");
                fldQuery.requestFocusInWindow();
            } else {
                fldQuery.requestFocusInWindow();
            }
        }
    }

    protected void setFocus() {
        fldQuery.requestFocusInWindow();
        fldQuery.setSelectionStart(0);
        fldQuery.setSelectionEnd(fldQuery.getText().length());
    }    
    
    protected void save() {
        String server = getServer() != null ? getServer().getName() : null;
        String mode = getMode() != null ? getMode().toString() : null;
        String region = getRegion() != null ? getRegion().getCode() : null;

        osf.getModule().setSetting(DcRepository.ModuleSettings.stOnlineSearchDefaultMode, mode);
        osf.getModule().setSetting(DcRepository.ModuleSettings.stOnlineSearchDefaultServer, server);
        osf.getModule().setSetting(DcRepository.ModuleSettings.stOnlineSearchDefaultRegion, region);        
    }
    
    protected void clear() {
        buttonSearch = null;
        fldQuery = null;
        buttonStop = null;
        comboRegions = null;
        comboModes = null;
        comboServers = null;
        osf = null;
        os = null;
    }    
    
    private void applySettings() {
        DcObject dco = osf.getDcObject();
        if (dco != null) {
            IServer server = os.getServer(dco);
            if (server != null) {
                comboServers.setSelectedItem(server);
                
                Region region = os.getRegion(dco);
                if (region != null) comboRegions.setSelectedItem(region);
                
                SearchMode mode = os.getMode(dco);
                if (mode != null) comboModes.setSelectedItem(mode);

                String query = os.getQuery(dco);
                if (query != null && !query.toLowerCase().equals("null")) 
                    fldQuery.setText(query.trim());
                
                return;
            }
        }
        
        if (os.getDefaultServer() != null)
            comboServers.setSelectedItem(os.getDefaultServer());
        else
            comboServers.setSelectedIndex(0);
        
        if (os.getDefaultRegion() != null)
            comboRegions.setSelectedItem(os.getDefaultRegion());
            
        if (os.getDefaultSearchMode() != null)
            comboModes.setSelectedItem(os.getDefaultSearchMode());
    }
    
    private void openServerSettingsDialog() {
        ServerSettingsDialog dlg = new ServerSettingsDialog(osf, getServer());
        dlg.setVisible(true);
    }
        
    private void rebuild() {
        listenForServerChanges = false;
        removeAll();
        build(false);
        revalidate();
        repaint();
        
//        IServer server = getServer();
//        if (    server != null && 
//                server.getName().equals("Amazon") &&
//               !DcSettings.getBoolean(DcRepository.Settings.stAmazonFirstStartChecked)) {
//            
//            new AwsKeyRequestDialog();
//        }
        
        listenForServerChanges = true;
    }    
    
    private void build(boolean initialize) {
        setLayout(Layout.getGBL());
        
        if (initialize) {
            fldQuery = ComponentFactory.getShortTextField(255);
            fldQuery.addKeyListener(this);            
            fldQuery.setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
            fldQuery.setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        
            buttonStop.addActionListener(osf);
            buttonStop.setActionCommand("stopsearch");
            
            comboServers.setActionCommand("changeserver");
            
            for (IServer server : osf.getServers()) 
                comboServers.addItem(server);

            comboServers.addActionListener(this);
        }
        
        IServer server = getServer();
        JLabel labelServer = ComponentFactory.getLabel(DcResources.getText("lblInternetServer"));
        
        int stretchX = server.getSearchModes() != null && server.getSearchModes().size() > 0 ? 1 : 2;
        JLabel labelSearch = ComponentFactory.getLabel(DcResources.getText("lblSearchFor"));
        add(labelSearch,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));
        add(fldQuery,     Layout.getGBC( 1, 0, stretchX, 1, 10.0, 10.0
                         ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));                

        if (server.getSearchModes() != null && server.getSearchModes().size() > 0) {
            comboModes.removeAllItems();
            for (SearchMode mode : server.getSearchModes()) 
                comboModes.addItem(mode);

            add(comboModes,   Layout.getGBC( 2, 0, 1, 1, 10.0, 10.0
                             ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                              new Insets(5, 5, 5, 5), 0, 0));
        }
  
        comboRegions.removeAllItems();
        for (Region region : server.getRegions()) 
            comboRegions.addItem(region);

        buttonSearch = ComponentFactory.getButton(DcResources.getText("lblFind"));
        buttonSearch.addActionListener(osf);
        buttonSearch.setActionCommand("search");
        buttonSearch.setMnemonic('F');
        
        
        // servers panel
        JPanel panelServers = new JPanel();
        panelServers.setLayout(Layout.getGBL());
        JButton btServerSettings = ComponentFactory.getIconButton(IconLibrary._icoSettings16);
        btServerSettings.setActionCommand("serversettings");
        btServerSettings.addActionListener(this);
        panelServers.add(comboServers, Layout.getGBC( 0, 0, 1, 1, 100.0, 100.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        if (server.getSettings() != null)
            panelServers.add(btServerSettings, Layout.getGBC( 1, 0, 1, 1, 1.0, 10.0
                    ,GridBagConstraints.EAST, GridBagConstraints.NONE,
                     new Insets(5, 0, 5, 0), 0, 0));
        

        // main panel
        add(labelServer,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));
        add(panelServers, Layout.getGBC( 1, 1, 1, 1, 10.0, 10.0
                         ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                          new Insets(0, 0, 0, 0), 0, 0));
        add(comboRegions, Layout.getGBC( 2, 1, 1, 1, 10.0, 10.0
                         ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));
        add(buttonSearch, Layout.getGBC( 3, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));
        add(buttonStop,   Layout.getGBC(4, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));      
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("stopsearch")) {
            osf.stop();
        } else if (e.getActionCommand().equals("changeserver") && listenForServerChanges) {
            rebuild();
        } else if (e.getActionCommand().equals("search")) {
            perfectMatchOccured = false;
            osf.start();
        } else if (e.getActionCommand().equals("serversettings")) {
            openServerSettingsDialog();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            perfectMatchOccured = false;
            osf.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
