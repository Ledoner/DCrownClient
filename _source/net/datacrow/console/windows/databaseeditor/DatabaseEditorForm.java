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

package net.datacrow.console.windows.databaseeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class DatabaseEditorForm extends DcFrame implements ActionListener {

    public DatabaseEditorForm() {
        super(DcResources.getText("lblDatabaseEditor"), IconLibrary._icoSettings32);
        
        buildForm();
        setHelpIndex("dc.tools.databaseeditor");
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stExpertFormSize, getSize());
        super.close();
    }
    
    private void buildForm() {
        QueryPanel queryPanel = new QueryPanel();

        JPanel panelActions = new JPanel();
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.setActionCommand("close");
        buttonClose.addActionListener(this);
        panelActions.add(buttonClose);
        
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(queryPanel, Layout.getGBC( 0, 0, 1, 1, 50.0, 50.0
        		            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
							 new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                             new Insets(0, 0, 0, 10), 0, 0));
        
        pack();
        setSize(DcSettings.getDimension(DcRepository.Settings.stExpertFormSize));
        setCenteredLocation();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("close"))
            close();
    }
}
