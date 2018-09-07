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

package net.datacrow.console.windows.security;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcPasswordField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.Connector;

public class ChangePasswordDialog extends DcDialog implements ActionListener, KeyListener {
    
    private DcPasswordField fldCurrentPassword = ComponentFactory.getPasswordField();
    
    private DcPasswordField fldNewPassword1 = ComponentFactory.getPasswordField();
    private DcPasswordField fldNewPassword2 = ComponentFactory.getPasswordField();
    
    private boolean canceled = false;
    
    public ChangePasswordDialog() {
        super(GUI.getInstance().getMainFrame());
        
        build();
        pack();
        setSize(new Dimension(300, 200));
        setHelpIndex("dc.security");
        toFront();
        setCenteredLocation();
        fldCurrentPassword.requestFocusInWindow();
    }
    
    public boolean isCanceled() {
        return canceled;
    }

    private void changePassword() {
        String currentPass = String.valueOf(fldCurrentPassword.getPassword());
        
        Connector connector = DcConfig.getInstance().getConnector();
        SecuredUser su = connector.login(connector.getUser().getUsername(), currentPass);
        if (su != null) {
            
            String newPass1 = String.valueOf(fldNewPassword1.getPassword());
            String newPass2 = String.valueOf(fldNewPassword2.getPassword());

            if (newPass1.length() == 0 || newPass2.length() == 0) {
                GUI.getInstance().displayMessage("msgPleaseEnterNewPassword");
            } else if (newPass1.equals(newPass2)){
                connector.changePassword(su.getUser(), newPass1);
                close();
            } else {
                GUI.getInstance().displayMessage("msgPasswordsDoNotMatch");
            }
            
        } else {
            GUI.getInstance().displayMessage("msgIncorrectOldPassword");
        }
    }
    
    @Override
    public void close() {
        super.close();
        fldCurrentPassword = null;
        fldNewPassword1 = null;
        fldNewPassword2 = null;
    }

    private void build() {
         getContentPane().setLayout(Layout.getGBL());
         Connector connector = DcConfig.getInstance().getConnector();
         
         String name = connector.getUser().getUser().getName();
         String loginname = connector.getUser().getUsername();
         
         DcLongTextField help = ComponentFactory.getHelpTextField();
         help.setText(DcResources.getText("lblPasswordForUserX",
                      new String[] {name, loginname}));
         
         getContentPane().add(help,   
                 Layout.getGBC(0, 0, 2, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));

         
         getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblOldPassword")),   
                 Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(fldCurrentPassword, Layout.getGBC(1, 1, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
         
         getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblNewPassword")),   
                 Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(fldNewPassword1, Layout.getGBC(1, 2, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));

         getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblRetypePassword")),   
                 Layout.getGBC(0, 3, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(fldNewPassword2, Layout.getGBC(1, 3, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
         
         JPanel panelActions = new JPanel();
         
         JButton btOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
         JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
         
         btOk.setActionCommand("ok");
         btCancel.setActionCommand("cancel");
         btOk.addActionListener(this);
         btCancel.addActionListener(this);
         
         fldNewPassword1.addKeyListener(this);
         fldNewPassword2.addKeyListener(this);
         fldCurrentPassword.addKeyListener(this);
         
         panelActions.add(btOk);
         panelActions.add(btCancel);
         
         getContentPane().add(panelActions, Layout.getGBC(0, 4, 2, 1, 1.0, 1.0,
                 GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("ok")) {
            changePassword();
        } else if (ae.getActionCommand().equals("cancel")) {
            canceled = true;
            close();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            changePassword();
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}