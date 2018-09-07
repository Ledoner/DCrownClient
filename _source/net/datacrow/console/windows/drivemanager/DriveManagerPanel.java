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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.core.drivemanager.IDriveManagerListener;
import net.datacrow.core.drivemanager.JobAlreadyRunningException;
import net.datacrow.core.resources.DcResources;

public abstract class DriveManagerPanel extends JPanel implements IDriveManagerListener {
    
    private final DcLongTextField textHelp = ComponentFactory.getHelpTextField();
    private final JobStatusPanel statusPanel;
    
    public DriveManagerPanel() {
        statusPanel = new JobStatusPanel(this);
        build();
    }

    protected abstract ImageIcon getIcon();
    protected abstract String getTitle();
    protected abstract String getHelpText();
    protected abstract void saveSettings();
    
    protected abstract void start() throws JobAlreadyRunningException;
    protected abstract void stop();
    
    @Override
    public void notify(String msg) {
        statusPanel.setMessage(msg);
    }
    
    @Override
    public void notifyJobStarted() {
        notify(DcResources.getText("msgJobStartedAtX", new Date().toString()));
        statusPanel.isRunning(true);
        allowActions(false);
    }
    
    @Override
    public void notifyJobStopped() {
        notify(DcResources.getText("msgJobStoppedAtX", new Date().toString()));
        statusPanel.isRunning(false);
        allowActions(true);
    }
    
    protected void allowActions(boolean b) {}
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);

        if (textHelp != null) {
            textHelp.setFont(ComponentFactory.getStandardFont());
            statusPanel.setFont(ComponentFactory.getStandardFont());
        }
    }

    protected void build() {
        setLayout(Layout.getGBL());
        
        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());

        textHelp.setText(getHelpText());
        textHelp.setPreferredSize(new Dimension(100, 60));
        textHelp.setMinimumSize(new Dimension(100, 60));
        textHelp.setMaximumSize(new Dimension(800, 60));
        
        panel.add(textHelp, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        panel.add(statusPanel, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));

        add(panel, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                  ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                   new Insets(0, 0, 0, 0), 0, 0));
    }
}
