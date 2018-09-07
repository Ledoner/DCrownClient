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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;

public class AboutDialog extends DcDialog {

    private static Logger logger = Logger.getLogger(AboutDialog.class.getName());
    
    public AboutDialog() {
        super(GUI.getInstance().getRootFrame());

        buildDialog();

        setTitle("");
        Image img =IconLibrary._icoMain.getImage();
        img.flush();

        setCenteredLocation();
    }

    private void buildDialog() {
        JTabbedPane tabbedPane = ComponentFactory.getTabbedPane();

        //**********************************************************
        //About panel
        //**********************************************************
        JPanel panelAbout = new JPanel();
        panelAbout.setLayout(Layout.getGBL());

        DcPictureField about = ComponentFactory.getPictureField(false, false);
        try {
            about.setValue(new URL("file://" + DcConfig.getInstance().getInstallationDir() + "icons/aboutinformation.jpg"));
            panelAbout.add(about, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        } catch (Exception e) {
            logger.error("Could not load the about image", e);
        }

        //**********************************************************
        //Credits
        //**********************************************************
        JPanel panelCredits = new JPanel();
        panelCredits.setLayout(Layout.getGBL());

        DcPictureField credits = ComponentFactory.getPictureField(false, false);
        try {
            credits.setValue(new URL("file://" + DcConfig.getInstance().getInstallationDir() + "icons/aboutcredits.jpg"));
            panelCredits.add(credits, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        } catch (Exception e) {
            logger.error("Could not load the credits image", e);
        }

        //**********************************************************
        //Main panel
        //**********************************************************
        tabbedPane.addTab(DcConfig.getInstance().getVersion().getFullString(), IconLibrary._icoAbout, panelAbout);
        tabbedPane.addTab(DcResources.getText("lblCredits"), IconLibrary._icoHelp, panelCredits);

        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(tabbedPane,   Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                                           GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                                           new Insets(5, 5, 5, 5), 0, 0));

        setTitle(DcResources.getText("msgQuestion"));
        pack();
        setSize(new Dimension(445,390));
        setResizable(false);
    }
}
