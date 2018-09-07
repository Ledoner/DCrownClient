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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.IDialog;
import net.datacrow.console.windows.NativeDialog;
import net.datacrow.util.Utilities;

public class NativeMessageBox extends NativeDialog implements ActionListener, IDialog {

    private JTextArea textMessage;
    private JButton buttonOk;
    private JPanel panel = new JPanel();

    public  static final int _ERROR = 1;
    public  static final int _WARNING = 2;
    public  static final int _INFORMATION = 3;
    
    public NativeMessageBox(String title, String message) {
        super((JFrame) null);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setTitle(title);
        build();
        
        GUI gui = GUI.getInstance();
        if (isModal() && gui.isSplashScreenActive())
            gui.showSplashScreen(false);
        
        textMessage.setText(message);
        pack();
        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));
        toFront();
        buttonOk.requestFocus();
        
        setModal(true);
        setVisible(true);
    }

    @Override
    public void close() {
        textMessage = null;
        buttonOk = null;
        panel = null;
        
        GUI gui = GUI.getInstance();
        if (gui.isSplashScreenActive())
            gui.showSplashScreen(true);
        
        dispose();
    }

    @Override
    public void setVisible(boolean b) {
        if (!b) dispose();
        super.setVisible(b);
    }

    private void build() {
        getContentPane().add(panel);

        setResizable(true);
        getContentPane().setLayout(new GridBagLayout());

        textMessage = new JTextArea();
        textMessage.setEditable(false);
        textMessage.setBackground(panel.getBackground());
        textMessage.setWrapStyleWord(true);
        textMessage.setLineWrap(true);
        textMessage.setMargin(new Insets(5,5,5,5));
        
        JScrollPane scrollIn = new JScrollPane(textMessage);
        scrollIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollIn.setPreferredSize(new Dimension(400,120));
        scrollIn.setMinimumSize(new Dimension(400,120));
        scrollIn.setBorder(null);
        buttonOk = new JButton("Ok");
        buttonOk.addActionListener(this);
        buttonOk.setMnemonic('O');

        panel.setLayout(new GridBagLayout());

        panel.add(scrollIn,   Layout.getGBC(0, 0, 1, 1, 40.0, 40.0
                             ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                              new Insets(5, 5, 5, 5), 0, 0));
        panel.add(buttonOk,   Layout.getGBC(0, 1, 1, 1, 0.0, 0.0
                             ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                              new Insets(5, 5, 5, 5), 0, 0));
        
        this.getContentPane().add(panel,   Layout.getGBC(0, 1, 2, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        close();
    }
}