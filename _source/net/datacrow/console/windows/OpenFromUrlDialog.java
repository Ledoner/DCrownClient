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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcUrlField;
import net.datacrow.core.http.HttpConnectionUtil;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;

public class OpenFromUrlDialog extends DcDialog implements ActionListener {

    private static Logger logger = Logger.getLogger(OpenFromUrlDialog.class.getName());
    
    private DcUrlField input = ComponentFactory.getURLField(500);
    
    private URL url;
    private byte[] image;
    
    public OpenFromUrlDialog() {
        super(GUI.getInstance().getRootFrame());
        setModal(true);
        build();
        setCenteredLocation();
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getActionCommand().equals("ok")) {
                url = input.getURL();                
                
                if (url != null) {
                    setImage(HttpConnectionUtil.retrieveBytes(url));
                    close();
                } else {
                    GUI.getInstance().displayMessage("msgEnterUrl");
                }
            } else if (ae.getActionCommand().equals("cancel")) {
                close();
            }
        } catch (Exception e) {
            String message = DcResources.getText("msgCouldNotReadFromURL", new String[] {(String) input.getValue(), e.getMessage()});
            logger.warn(message, e);
            GUI.getInstance().displayWarningMessage(message);
        }
    }

    @Override
    public void close() {
        super.close();
        input = null;
    }
    
    private void build() {
        getContentPane().setLayout(Layout.getGBL());
        
        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());
        
        JLabel label = ComponentFactory.getLabel(DcResources.getText("lblImageURL"));
        panelInput.add(label, Layout.getGBC( 0, 0, 1, 1, 0.0, 0.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelInput.add(input, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        
        JPanel panelAction = new JPanel();
        panelAction.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
        JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        
        buttonOk.setActionCommand("ok");
        buttonCancel.setActionCommand("cancel");

        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
        
        panelAction.add(buttonOk);
        panelAction.add(buttonCancel);
        
        getContentPane().add(panelInput, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        getContentPane().add(panelAction, Layout.getGBC( 0, 1, 1, 1, 0.0, 0.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));
        
        pack();
    }
}

