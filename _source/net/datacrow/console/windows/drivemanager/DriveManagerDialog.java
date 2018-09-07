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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class DriveManagerDialog extends DcFrame implements ActionListener {

    private static DriveManagerDialog instance;
    
    private final DrivePollerPanel pollerPanel;
    private final FileSynchronizerPanel synchronizerPanel;
    private final DriveScannerPanel scannerPanel;
    private final JTabbedPane tp;
    
    static {
        instance = new DriveManagerDialog(); 
    }
    
    public static DriveManagerDialog getInstance() {
        return instance;
    }
    
    private DriveManagerDialog() {
        super(DcResources.getText("lblDriveManager"), IconLibrary._icoDriveManager);
        
        pollerPanel = new DrivePollerPanel();
        synchronizerPanel = new FileSynchronizerPanel();
        scannerPanel = new DriveScannerPanel();
        tp = ComponentFactory.getTabbedPane();
        
        setHelpIndex("dc.tools.drivemanager");
        
        build();
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
            saveSettings();
        } else { 
            setSize(DcSettings.getDimension(DcRepository.Settings.stDriveManagerDialogSize));
            setCenteredLocation();
        }
        
    	super.setVisible(visible);
    }

    @Override
    public void close() {
        saveSettings();
        setVisible(false);
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        if (tp != null) {
            tp.setFont(ComponentFactory.getSystemFont());
            scannerPanel.setFont(font);
            synchronizerPanel.setFont(font);
            pollerPanel.setFont(font);
        }
    }
    
    private void saveSettings() {
        scannerPanel.saveSettings();
        pollerPanel.saveSettings();
        synchronizerPanel.saveSettings();
        
        DcSettings.set(DcRepository.Settings.stDriveManagerDialogSize, getSize());
    }

    private void build() {
        tp.addTab(scannerPanel.getTitle(), scannerPanel.getIcon(),  scannerPanel);
        tp.addTab(pollerPanel.getTitle(), pollerPanel.getIcon(), pollerPanel);
        tp.addTab(synchronizerPanel.getTitle(), synchronizerPanel.getIcon(), synchronizerPanel);
        
        JPanel panelActions = new JPanel();
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        panelActions.add(buttonClose);
        
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(tp, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        
        setFont(ComponentFactory.getStandardFont());
        
        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("close"))
            close();
    }    
}
