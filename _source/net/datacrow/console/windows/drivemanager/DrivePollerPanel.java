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

package net.datacrow.console.windows.drivemanager;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.drivemanager.JobAlreadyRunningException;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class DrivePollerPanel extends DriveManagerPanel {

    private JCheckBox cbRunOnStartup;
    
    public DrivePollerPanel() {
        super();
        DriveManager.getInstance().addPollerListener(this);
    }
    
    @Override
    protected String getHelpText() {
        return DcResources.getText("msgDrivePollerHelp");
    }

    @Override
    protected ImageIcon getIcon() {
        return IconLibrary._icoDrivePoller;
    }

    @Override
    protected String getTitle() {
        return DcResources.getText("lblDrivePoller");
    }    
    
    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stDrivePollerRunOnStartup, Boolean.valueOf(cbRunOnStartup.isSelected()));
    }

    @Override
    protected void start() throws JobAlreadyRunningException {
        DriveManager.getInstance().startDrivePoller();
    }
    
    @Override
    protected void stop() {
        DriveManager.getInstance().stopDrivePoller();
    }

    @Override
    protected void build() {
        super.build();

        cbRunOnStartup = ComponentFactory.getCheckBox(DcResources.getText("lblRunOnStartup"));   
        
        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(Layout.getGBL());
        panelSettings.add(cbRunOnStartup, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 0), 0, 0));
        panelSettings.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblSettings")));
        cbRunOnStartup.setSelected(DcSettings.getBoolean(DcRepository.Settings.stDrivePollerRunOnStartup));        
        
        add(panelSettings, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));

    }
    
    
}
