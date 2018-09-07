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
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.UserMode;
import net.datacrow.core.resources.DcResources;

public class SelectExpienceLevelDialog extends DcDialog implements ActionListener {

    public SelectExpienceLevelDialog() {
        super(GUI.getInstance().getMainFrame());
        setTitle(DcResources.getText("lblSelectXpMode"));
        build();
        setCenteredLocation();
        setModal(true);
        setSize(new Dimension(300, 260));
    }

    private void build() {

      //**********************************************************
        //Help
        //**********************************************************
        DcLongTextField explanation = ComponentFactory.getLongTextField();
        explanation.setText(DcResources.getText("msgSelectXpLevelHelp"));
        ComponentFactory.setUneditable(explanation);
        
        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        
        JButton buttonBeginner = ComponentFactory.getButton(DcResources.getText("lblBeginnerMode"));
        JButton buttonExpert = ComponentFactory.getButton(DcResources.getText("lblExpertMode"));
        
        buttonBeginner.setIcon(IconLibrary._icoBeginner);
        buttonExpert.setIcon(IconLibrary._icoExpert);
        
        buttonBeginner.addActionListener(this);
        buttonExpert.addActionListener(this);
        
        buttonBeginner.setActionCommand("beginner");
        buttonExpert.setActionCommand("expert");
        
        panelActions.add(buttonBeginner);
        panelActions.add(buttonExpert);
        

        //**********************************************************
        //Main Panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());
        
        this.getContentPane().add(
                explanation,   Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(
                 panelActions, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));

        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("expert"))
            UserMode.setUserMode(UserMode._XP_EXPERT);
        else if (ae.getActionCommand().equals("beginner"))
            UserMode.setUserMode(UserMode._XP_BEGINNER);
        
        close();
    }
}