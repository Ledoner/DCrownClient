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

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.drivemanager.JobAlreadyRunningException;
import net.datacrow.core.resources.DcResources;

public class FileSynchronizerPanel extends DriveManagerPanel {

    private JComboBox comboPrecision;
    
    public FileSynchronizerPanel() {
        super();
        DriveManager.getInstance().addSynchronizerListener(this);
    }
    
    @Override
    protected String getHelpText() {
        return DcResources.getText("msgFileSynchronizerHelp");
    }

    @Override
    protected ImageIcon getIcon() {
        return IconLibrary._icoFileSynchronizer;
    }
    
    @Override
    protected void saveSettings() {}

    @Override
    protected String getTitle() {
        return DcResources.getText("lblFileSynzhronizer");
    }    
    
    @Override
    protected void allowActions(boolean b) {
        comboPrecision.setEnabled(b);
    }
    
    @Override
    protected void start() throws JobAlreadyRunningException {
        Precision precision = (Precision) comboPrecision.getSelectedItem();
        DriveManager.getInstance().startFileSynchronizer(precision.getLevel());
    }
    
    @Override
    protected void stop() {
        DriveManager.getInstance().stopFileSynchronizer();
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (comboPrecision != null)
            comboPrecision.setFont(ComponentFactory.getStandardFont());
    }
    
    @Override
    protected void build() {
        super.build();
        
        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(Layout.getGBL());
        
        comboPrecision = ComponentFactory.getComboBox();
        comboPrecision.addItem(new Precision(DriveManager._PRECISION_LOWEST, DcResources.getText("lblMatchOnFilename")));
        comboPrecision.addItem(new Precision(DriveManager._PRECISION_MEDIUM, DcResources.getText("lblMatchOnFilenameAndSize")));
        comboPrecision.addItem(new Precision(DriveManager._PRECISION_HIGHEST, DcResources.getText("lblMatchOnHashAndSize")));
        
        comboPrecision.setSelectedIndex(2);
        
        panelSettings.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblSettings")));
        
        panelSettings.add(ComponentFactory.getLabel(DcResources.getText("lblPrecision")), 
                Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        panelSettings.add(comboPrecision,     Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5), 0, 0));
        
        add(panelSettings, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    private static class Precision {
        private int level;
        private String description;
        
        protected Precision(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public int getLevel() {
            return level;
        }
        
        @Override
        public String toString() {
            return getDescription();
        }
    }
}