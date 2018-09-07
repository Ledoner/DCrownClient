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

package net.datacrow.console.windows.filerenamer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFilePatternTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class InsertTextDialog extends DcDialog implements ActionListener {

    private final DcFilePatternTextField textFld = ComponentFactory.getFilePatternTextField();

    public InsertTextDialog(JFrame frame) {
        super(frame);
        setTitle(DcResources.getText("lblInsertText"));
        
        build();
        setCenteredLocation();
        setVisible(true);
    }

    public String getText() {
        return textFld.getText();
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stTextViewerSize, getSize());
        setVisible(false);
    }

    protected void build() {

        //**********************************************************
        //Text panel
        //**********************************************************
        JPanel panelText = new JPanel();
        panelText.setLayout(Layout.getGBL());
        
        panelText.add(ComponentFactory.getLabel(DcResources.getText("lblText")), 
                 Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTH, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelText.add(textFld, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));

        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelAction = new JPanel();

        JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblOK"));

        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("close");

        buttonOk.addActionListener(this);
        buttonOk.setActionCommand("ok");
        
        panelAction.add(buttonOk);
        panelAction.add(buttonCancel);

        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());
        this.getContentPane().add(panelText,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                                 ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                                  new Insets( 0, 0, 0, 0), 0, 0));
        this.getContentPane().add(panelAction,Layout.getGBC( 0, 1, 1, 1, 0.0, 0.0
                                 ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                                  new Insets( 0, 0, 0, 0), 0, 0));
        
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close")) {
            textFld.setText("");
            close();
        } else if (ae.getActionCommand().equals("ok")) {
            close();
        }
    }
}
