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

package net.datacrow.console.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Utilities;
import net.datacrow.util.launcher.URLLauncher;

public class AwsKeyRequestDialog extends DcDialog implements ActionListener {

    private DcShortTextField txtAccessKeyID;
    private DcShortTextField txtSecretKey;

    public AwsKeyRequestDialog() {
        
        super();
        setTitle(DcResources.getText("lblAwsKey"));
        
        build();
        pack();
        
        setSize(new Dimension(600, 300));
        setCenteredLocation();
        
        setVisible(true);
    }
    
    public void setEditable(boolean b) {
        txtAccessKeyID.setEditable(b);
    }
    
    @Override
    public void close() {
        super.close();
        txtAccessKeyID = null;
        txtSecretKey = null;
    }
    
    private void requestAwsKey() {
        try {
            URL url = new URL("http://aws.amazon.com/");
            if (url != null) {
                URLLauncher launcher = new URLLauncher(url);
                launcher.launch();
            }
        } catch (Exception exp) {
            GUI.getInstance().displayErrorMessage(exp.toString());
        }
    }

    private void build() {
        getContentPane().setLayout(Layout.getGBL());
        
        DcHtmlEditorPane txtHelp = ComponentFactory.getHtmlEditorPane();
        
        txtAccessKeyID = ComponentFactory.getShortTextField(200);
        txtSecretKey = ComponentFactory.getShortTextField(200);
        
        txtAccessKeyID.setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        txtSecretKey.setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        
        String html = "<html><body " + Utilities.getHtmlStyle() + ">" + 
            DcResources.getText("msgAwsAccessKeyID") + "</body></html>";
        
        txtHelp.setHtml(html);
        
        getContentPane().add(new JScrollPane(txtHelp), Layout.getGBC( 0, 0, 2, 1, 5.0, 5.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 0, 5, 0), 0, 0));
        
        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());
        
        panelInput.add(ComponentFactory.getLabel(DcResources.getText("lblAwsAccessKeyID")), 
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 0, 5, 5), 0, 0));
        panelInput.add(txtAccessKeyID, Layout.getGBC( 1, 0, 1, 1, 5.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 0, 5, 0), 0, 0));
        panelInput.add(ComponentFactory.getLabel(DcResources.getText("lblAwsSecretKey")), 
                Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 0, 5, 5), 0, 0));
        panelInput.add(txtSecretKey, Layout.getGBC( 1, 1, 1, 1, 5.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 0, 5, 0), 0, 0));

        JButton buttonRequest = ComponentFactory.getButton(DcResources.getText("lblGetAwsKey"), 
                IconLibrary._icoSearchOnline16);
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblOK"));

        buttonRequest.setActionCommand("request");
        buttonOk.setActionCommand("ok");
        
        buttonRequest.addActionListener(this);
        buttonOk.addActionListener(this);
        
        getContentPane().add(panelInput, Layout.getGBC( 0, 1, 2, 1, 5.0, 5.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 5, 0, 5, 0), 0, 0));
        getContentPane().add(buttonRequest, Layout.getGBC( 0, 2, 2, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 0, 5, 0), 0, 0));
        

        getContentPane().add(buttonOk, Layout.getGBC( 0, 2, 2, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets( 5, 0, 5, 0), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("ok")) {
//            save();
            close();
        } else if (ae.getActionCommand().equals("request")) {
            requestAwsKey();
        }
    }
}
