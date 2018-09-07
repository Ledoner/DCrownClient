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

package net.datacrow.console.windows.messageboxes;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.NativeDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.util.Utilities;

public class NativeQuestionBox extends NativeDialog implements ActionListener {

    private JTextArea textMessage;
    private JButton buttonYes;
    private JButton buttonNo;
    private boolean affirmative = false;

    public NativeQuestionBox(String message) {
        super(null);
        init(message);
        setVisible(true);
    }

    public boolean isAffirmative() {
        return affirmative;
    }
    
    @Override
    public void close() {
        textMessage = null;
        buttonYes = null;
        buttonNo = null;
        
        if (GUI.getInstance().isSplashScreenActive())
            GUI.getInstance().showSplashScreen(true);
        
        dispose();
    }
    
    private void init(String message) {
        buildDialog();
        textMessage.setText(message);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        this.pack();
        this.setModal(true);

        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));

        buttonYes.requestFocus();
    }

    private void buildDialog() {
        this.setResizable(false);
        this.getContentPane().setLayout(Layout.getGBL());

        textMessage = ComponentFactory.getTextArea();
        textMessage.setEditable(false);
                
        JScrollPane scrollIn = new JScrollPane(textMessage);
        scrollIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollIn.setPreferredSize(new Dimension(400,120));
        scrollIn.setMinimumSize(new Dimension(400,120));
        scrollIn.setBorder(null);

        buttonYes = ComponentFactory.getButton("Yes");
        buttonNo = ComponentFactory.getButton("No");

        textMessage.setBackground(buttonYes.getBackground());
        
        buttonYes.addActionListener(this);
        buttonYes.setActionCommand("confirm");
        buttonNo.addActionListener(this);
        buttonNo.setActionCommand("cancel");
        
        JPanel panelAction = new JPanel();
        panelAction.add(buttonYes);
        panelAction.add(buttonNo);

        setIconImage(IconLibrary._icoQuestion.getImage());

        this.getContentPane().setLayout(Layout.getGBL());
        this.getContentPane().add(scrollIn,    Layout.getGBC( 0, 0, 1, 1, 90.0, 90.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelAction, Layout.getGBC( 0, 1, 1, 1, 0.0, 0.0
               ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        this.pack();
        this.setModal(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("confirm")) {
            affirmative = true;
            close();
        } else if (e.getActionCommand().equals("cancel")) {
            affirmative = false;
            close();
        }
    }
}