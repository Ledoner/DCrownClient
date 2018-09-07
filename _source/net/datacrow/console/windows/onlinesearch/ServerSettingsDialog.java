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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.console.windows.settings.SettingsPanel;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsGroup;
import net.datacrow.settings.DcSettings;

public class ServerSettingsDialog extends DcDialog implements ActionListener {

    private IServer server;
    private SettingsPanel panelSettings;
    
    public ServerSettingsDialog(JFrame parent, IServer server) {
        super(parent);

        this.server = server;
        build();

        setTitle(DcResources.getText("lblServerSettings"));

        setSize(DcSettings.getDimension(DcRepository.Settings.stServerSettingsDialogSize));

        setCenteredLocation();
    }
    
    private void saveSettings() {
        panelSettings.saveSettings();
    }

    @Override
    public void close() {
        server = null;
        panelSettings = null;
        DcSettings.set(DcRepository.Settings.stServerSettingsDialogSize, getSize());
        super.close();
    }

    private void build() {
        //**********************************************************
        //Settings panel
        //**********************************************************
        SettingsGroup group = new SettingsGroup("serversettings", "");
        
        for (Setting setting : server.getSettings()) {
            group.add(setting);
        }
            
        panelSettings = new SettingsPanel(group, true);
        panelSettings.setVisible(true);
        panelSettings.initializeSettings();

        //**********************************************************
        //Actions panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        
        JButton btSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        btSave.setActionCommand("save");
        btSave.addActionListener(this);
        
        JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        btCancel.setActionCommand("cancel");
        btCancel.addActionListener(this);

        panelActions.add(btSave);
        panelActions.add(btCancel);
        
        //**********************************************************
        //Main panel
        //**********************************************************
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(panelSettings, Layout.getGBC(0, 0, 1, 1, 20.0, 20.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("save")) {
            saveSettings();
            close();
        } else {
            close();
        }
    }
}
