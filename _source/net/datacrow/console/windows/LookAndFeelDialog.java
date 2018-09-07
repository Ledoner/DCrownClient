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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLookAndFeelSelector;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class LookAndFeelDialog extends DcDialog implements ActionListener  {
    
    public LookAndFeelDialog() {
        super(GUI.getInstance().getMainFrame());
        
        setTitle(DcResources.getText("lblLookAndFeel"));
        setHelpIndex("dc.settings.laf");
        
        getContentPane().setLayout(Layout.getGBL());
        
        DcLookAndFeelSelector lafSelector = ComponentFactory.getLookAndFeelSelector();
        lafSelector.setParent(this);
        lafSelector.setValue(DcSettings.getLookAndFeel(DcRepository.Settings.stLookAndFeel));
        
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.addActionListener(this);
        
        getContentPane().add(lafSelector,  Layout.getGBC( 0, 0, 1, 1, 10.0, 10.0
             ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
              new Insets( 5, 5, 5, 5), 0, 0));
         getContentPane().add(buttonClose,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
             ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
              new Insets( 5, 5, 5, 5), 0, 0));

        pack();
        
        setSize(new Dimension(400, 300));
        
        setCenteredLocation();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        close();
    }
}
