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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcNumberField;
import net.datacrow.console.components.DcPasswordField;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;

public class LoginDialog extends DcDialog implements ActionListener, KeyListener {
    
    private DcShortTextField fldLoginName = ComponentFactory.getShortTextField(255);
    private DcPasswordField fldPassword = ComponentFactory.getPasswordField();
    private DcShortTextField fldServerAddress = ComponentFactory.getShortTextField(255);
    private DcNumberField fldApplicationServerPort = ComponentFactory.getNumberField();
    private DcNumberField fldImageServerPort = ComponentFactory.getNumberField();
    
    private boolean canceled = false;
    
    public LoginDialog() {
        super((JFrame) null);
        build();
        setTitle(DcResources.getText("lblLogin"));
        pack();
        toFront();
        setCenteredLocation();
        fldLoginName.requestFocusInWindow();
    }
    
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void close() {
        setVisible(false);
    }
    
    public String getLoginName() {
        return fldLoginName.getText();
    }

    public String getPassword() {
        return String.valueOf(fldPassword.getPassword());
    }

    private void login() {
        if (fldLoginName.getText().length() == 0) {
            GUI.getInstance().displayMessage("msgPleaseEnterUsername");
        } else {
            Connector conn = DcConfig.getInstance().getConnector();
            
            Long applicationServerPort = (Long) fldApplicationServerPort.getValue();
            if (applicationServerPort != null)
                conn.setApplicationServerPort(applicationServerPort.intValue());
            
            Long imageServerPort = (Long) fldImageServerPort.getValue();
            if (imageServerPort != null)
                conn.setImageServerPort(imageServerPort.intValue());
            
            String address = fldServerAddress.getText();
            conn.setServerAddress(address);
            
            DcSettings.set(DcRepository.Settings.stServerAddress, address);
            DcSettings.set(DcRepository.Settings.stApplicationServerPort, applicationServerPort);
            DcSettings.set(DcRepository.Settings.stImageServerPort, imageServerPort);
            
            close();
        }
    }
    
    public void clear() {
        super.close();
        fldLoginName = null;
        fldPassword = null;
    }

    private void build() {
         getContentPane().setLayout(Layout.getGBL());
         getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblLoginname")),   
                 Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(fldLoginName, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblPassword")),   
                 Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
         getContentPane().add(fldPassword, Layout.getGBC(1, 1, 1, 1, 1.0, 1.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
         
         Connector connector = DcConfig.getInstance().getConnector();
         
         if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT) {
             getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblServerAddress")),   
                     Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                     new Insets(5, 5, 5, 5), 0, 0));
             getContentPane().add(fldServerAddress, Layout.getGBC(1, 2, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 5, 5), 0, 0));
             getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblApplicationServerPort")),   
                     Layout.getGBC(0, 3, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                     new Insets(5, 5, 5, 5), 0, 0));
             getContentPane().add(fldApplicationServerPort, Layout.getGBC(1, 3, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 5, 5), 0, 0)); 
             getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblImageServerPort")),   
                     Layout.getGBC(0, 4, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                     new Insets(5, 5, 5, 5), 0, 0));
             getContentPane().add(fldImageServerPort, Layout.getGBC(1, 4, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 5, 5), 0, 0));
             
             String serverAddress = connector.getServerAddress();
             if (CoreUtilities.isEmpty(serverAddress))
                 serverAddress = DcSettings.getString(DcRepository.Settings.stServerAddress);
             fldServerAddress.setText(serverAddress);
             
             int applicationServerPort = connector.getApplicationServerPort();
             if (applicationServerPort > 0) {
                 Long port = DcSettings.getLong(DcRepository.Settings.stApplicationServerPort);
                 applicationServerPort = port != null ? port.intValue() : 9000;
             }
             fldApplicationServerPort.setValue(applicationServerPort);
             
             int imageServerPort = connector.getImageServerPort();
             if (imageServerPort > 0) {
                 Long port = DcSettings.getLong(DcRepository.Settings.stImageServerPort);
                 imageServerPort = port != null ? port.intValue() : 9001;
             }
             fldImageServerPort.setValue(imageServerPort);
         }
         
         JPanel panelActions = new JPanel();
         
         JButton btOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
         JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
         
         btOk.setActionCommand("ok");
         btCancel.setActionCommand("cancel");
         btOk.addActionListener(this);
         btCancel.addActionListener(this);
         
         fldPassword.addKeyListener(this);
         fldLoginName.addKeyListener(this);
         
         panelActions.add(btOk);
         panelActions.add(btCancel);
         
         getContentPane().add(panelActions, Layout.getGBC(0, 5, 2, 1, 1.0, 1.0,
                 GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 0), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("ok")) {
            login();
        } else if (ae.getActionCommand().equals("cancel")) {
            canceled = true;
            close();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            login();
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
