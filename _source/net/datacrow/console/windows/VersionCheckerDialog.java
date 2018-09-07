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
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcHtmlEditorPane;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class VersionCheckerDialog extends DcFrame implements ActionListener {

    private DcCheckBox cbDoCheck = ComponentFactory.getCheckBox(DcResources.getText("msgVersionCheckOnStartup"));
    private DcHtmlEditorPane pane = ComponentFactory.getHtmlEditorPane();
    
    public VersionCheckerDialog(String html) {
        super(DcResources.getText("lblVersionCheck"), IconLibrary._icoMain);
        build(html);
        setCenteredLocation();
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stCheckForNewVersion, Boolean.valueOf(cbDoCheck.isSelected()));
        super.close();
    }

    private void build(String html) {
        //**********************************************************
        //Version panel
        //**********************************************************

        pane.setHtml(html);
        JScrollPane scroller = new JScrollPane(pane);

        //**********************************************************
        //Settings
        //**********************************************************
        cbDoCheck.setSelected(true);
        
        //**********************************************************
        //Action panel
        //**********************************************************
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblOK"));
        buttonClose.addActionListener(this);
        
        //**********************************************************
        //Main panel
        //**********************************************************
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(scroller, Layout.getGBC(0, 0, 1, 1, 1.0, 50.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(cbDoCheck, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(buttonClose, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        
        pack();
        setSize(400, 400);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        close();
    }
}
