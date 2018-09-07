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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.datacrow.console.Layout;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Utilities;

public class SelectLanguageDialog extends NativeDialog implements ActionListener {

    private JComboBox cbLanguage = new JComboBox();
    
    public SelectLanguageDialog() {
        super((JFrame) null);
        build();
        pack();
        setTitle("Select the preferred language");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stLanguage, cbLanguage.getSelectedItem());
        setVisible(false);
        dispose();
    }
    
    private void build() {

        JLabel label = new JLabel("Please select the language of your choice:");
        
        for (String language : DcResources.getLanguages())
            cbLanguage.addItem(language);
        
        cbLanguage.setSelectedIndex(0);
        
        setResizable(true);
        getContentPane().setLayout(new GridBagLayout());

        JButton buttonOk = new JButton("Ok");
        buttonOk.addActionListener(this);

        getContentPane().add(label,   Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(cbLanguage,   Layout.getGBC(1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(buttonOk,   Layout.getGBC(1, 1, 1, 1, 0.0, 0.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        close();
    }
}
