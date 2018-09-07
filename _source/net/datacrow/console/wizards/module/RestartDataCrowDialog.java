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

package net.datacrow.console.wizards.module;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

public class RestartDataCrowDialog extends DcDialog implements ActionListener {
    
    public RestartDataCrowDialog(JFrame parent) {
        super(parent);
        
        setModal(true);
        
        build();
        
        setResizable(false);
        pack();
        setCenteredLocation();
        
        setVisible(true);
    }
    
    private void build() {
        getContentPane().setLayout(Layout.getGBL());

        JTextArea textMessage = ComponentFactory.getTextArea();
        textMessage.setEditable(false);
        
        textMessage.setText(DcResources.getText("msgRestart"));
        
        JScrollPane scrollIn = new JScrollPane(textMessage);
        scrollIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollIn.setPreferredSize(new Dimension(350,50));
        scrollIn.setBorder(null);
        
        JButton buttonYes = ComponentFactory.getButton(DcResources.getText("lblYes"));
        buttonYes.addActionListener(this);
        buttonYes.setActionCommand("restart");

        JButton buttonNo = ComponentFactory.getButton(DcResources.getText("lblNo"));
        buttonNo.addActionListener(this);
        buttonNo.setActionCommand("close");
        
        getContentPane().add(ComponentFactory.getLabel(IconLibrary._icoAbout),  
                             Layout.getGBC( 0, 0, 1, 1, 0.0, 0.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                             new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(scrollIn,      Layout.getGBC( 1, 0, 2, 3, 90.0, 90.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                             new Insets(5, 5, 5, 5), 0, 0));
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelActions.add(buttonNo);
        panelActions.add(buttonYes);
        
        textMessage.setBackground(panelActions.getBackground());
        
        getContentPane().add(panelActions,  Layout.getGBC( 2, 3, 1, 1, 0.0, 0.0
                            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                             new Insets(5, 5, 5, 5), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("restart")) {
            GUI.getInstance().getMainFrame().setOnExitCheckForChanges(false);
            GUI.getInstance().getMainFrame().close();
        } else if (e.getActionCommand().equals("close")) {
            close();
        }
    }      
}
        
