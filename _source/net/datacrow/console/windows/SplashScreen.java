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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JWindow;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.util.Utilities;

public final class SplashScreen extends JWindow {

    private final JLabel status = ComponentFactory.getLabel("");

    public SplashScreen() {
        buildPanel();
    }

    public void setStatusMsg(String message) {
    	status.setText(message);
    }

	public void splash() {
		setVisible(true);
	}

    private void buildPanel() {
        //**********************************************************
        //Logo
        //**********************************************************
        JLabel logo = new JLabel(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons/logo.jpg"));

        //**********************************************************
        //Status 
        //**********************************************************
        status.setPreferredSize(new Dimension(500, 22));
        status.setText(DcConfig.getInstance().getVersion().getFullString());
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setVerticalAlignment(JLabel.CENTER);
        status.setFont(new Font("Tahoma", Font.BOLD, 11));
        status.setForeground(Color.WHITE);
        
        getContentPane().setBackground(Color.BLACK);
        status.setBackground(Color.BLACK);

        //**********************************************************
        //Main panel
        //**********************************************************
        getContentPane().setLayout(Layout.getGBL());

        // Build the panel
        this.getContentPane().add(   logo,   Layout.getGBC( 0, 0, 1, 1, 0.0, 0.0
                                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                             new Insets( 2, 2, 2, 2), 0, 0));
        this.getContentPane().add(   status, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
        		                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
											 new Insets( 2, 0, 0, 0), 0, 0));
        pack();
        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));
    }
}

