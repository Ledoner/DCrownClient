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
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcDirectoriesAsDrivesField;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class DirectoriesAsDrivesDialog extends DcDialog implements ActionListener {

    private DcDirectoriesAsDrivesField mappingFld = ComponentFactory.getDirectoriesAsDrivesField();
    private boolean success = false;
    
    public DirectoriesAsDrivesDialog() {
        this(GUI.getInstance().getRootFrame());
    }
    
    public DirectoriesAsDrivesDialog(JFrame parent) {
        super(parent);
        build();
        setHelpIndex("dc.settings.directoriesasdrives");
        setTitle(DcResources.getText("msgAddDrive"));
        setCenteredLocation();
        setSize(DcSettings.getDimension(DcRepository.Settings.stDirectoriesAsDrivesDialogSize));
    }
    
    private void save() {
        String[] values = (String[]) mappingFld.getValue();
        DcSettings.set(DcRepository.Settings.stDirectoriesAsDrives, values);
        success = true;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stDirectoriesAsDrivesDialogSize, getSize());
        super.close();
        mappingFld.clear();
        mappingFld = null;
    }

    private void build() {
        getContentPane().setLayout(Layout.getGBL());

        mappingFld.setValue(DcSettings.getStringArray(DcRepository.Settings.stDirectoriesAsDrives));
        
        /***********************************************************************
         * ACTIONS PANEL
         **********************************************************************/
        JPanel panelActions = new JPanel();
        panelActions.setLayout(Layout.getGBL());

        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblCancel"));

        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");

        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");

        panelActions.add(buttonSave, Layout.getGBC(0, 0, 1, 4, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
        panelActions.add(buttonClose, Layout.getGBC(1, 0, 1, 4, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        /***********************************************************************
         * MAIN PANEL
         **********************************************************************/
        getContentPane().add(mappingFld, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5), 0, 0));

        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("close")) {
            close();
            success = false;
        } else if (e.getActionCommand().equals("save")) {
            save();
            close();
        }
    }
 
}
