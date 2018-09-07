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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.windows.settings.SettingsPanel;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.settings.SettingsGroup;

public class UpgradeDialog extends DcDialog implements ActionListener {

    private SettingsPanel pnlSettings;
    private boolean affirmative = false;
    
    public UpgradeDialog(String help, SettingsGroup settings) {
        super((JFrame) null);
        
        build(help, settings);
        
        setCenteredLocation();
        setModal(true);
    }

    public boolean isAffirmative() {
        return affirmative;
    }

    @Override
    public void close() {
        pnlSettings = null;
        super.close();
    }

    protected void build(String help, SettingsGroup settings) {

        //**********************************************************
        //Text panel
        //**********************************************************
        DcLongTextField fldHelp = ComponentFactory.getHelpTextField();
        JScrollPane spHelp = new JScrollPane(fldHelp);
        spHelp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spHelp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        fldHelp.setText(help);
        
        //**********************************************************
        //Settings panel
        //**********************************************************
        pnlSettings = new SettingsPanel(settings, true);
        pnlSettings.setVisible(true);
        pnlSettings.initializeSettings();

        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel pnlActions = new JPanel();
        JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        JButton btOK = ComponentFactory.getButton(DcResources.getText("lblOK"));

        btCancel.addActionListener(this);
        btCancel.setActionCommand("cancel");
        btOK.addActionListener(this);
        btOK.setActionCommand("ok");

        pnlActions.add(btOK);
        pnlActions.add(btCancel);

        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());
        setTitle(DcResources.getText("Database Upgrade"));

        this.getContentPane().add(spHelp,      Layout.getGBC( 0, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 0, 0), 0, 0));
        this.getContentPane().add(pnlSettings, Layout.getGBC( 0, 1, 1, 1, 30.0, 30.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 0, 0), 0, 0));
        this.getContentPane().add(pnlActions,  Layout.getGBC( 0, 2, 1, 1, 0.0, 0.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));
        
        pack();
        setSize(500, 500);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("cancel")) {
            affirmative = false;
        } else if (ae.getActionCommand().equals("ok")) {
            pnlSettings.saveSettings();
            affirmative = true;
        }
        close();
    }
}
