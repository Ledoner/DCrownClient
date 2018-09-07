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

package net.datacrow.console.windows.help;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.swing.JFrame;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class HelpDialog {

    private static String helpIndex = "dc.general.introduction";

    private static Logger logger = Logger.getLogger(HelpDialog.class.getName());
    
    public static void setHelpIndex(String helpIndex) {
    	HelpDialog.helpIndex = helpIndex;
    }

	public HelpDialog(Window window) {
		try {
		    
			ClassLoader cl = HelpDialog.class.getClassLoader();
			
			String language = DcSettings.getSetting(DcRepository.Settings.stLanguage).getValueAsString();
			
			java.net.URL hsURL = javax.help.HelpSet.findHelpSet(cl, "en/jHelpSet.hs");
			if ("Portuguese".equals(language))
			    hsURL = javax.help.HelpSet.findHelpSet(cl, "pt/jHelpSet.hs"); 
            
            HelpSet hs = new HelpSet(cl, hsURL);
            hs.setTitle(DcResources.getText("lblHelp"));
            
            final DefaultHelpBroker hb = new DefaultHelpBroker();
            hb.setHelpSet(hs);
            hb.setActivationWindow(window);
            hb.setCurrentID(helpIndex);
            hb.setSize(DcSettings.getDimension(DcRepository.Settings.stHelpFormSize));
            hb.setLocation(Utilities.getCenteredWindowLocation(hb.getSize(), false));
            hb.setFont(ComponentFactory.getSystemFont());
            hb.setDisplayed(true);

            Window helpWindow = hb.getWindowPresentation().getHelpWindow();
            if (helpWindow instanceof JFrame) {
                JFrame helpFrame = (JFrame) helpWindow;
                helpFrame.setIconImage(IconLibrary._icoMain.getImage());
                helpFrame.setTitle(DcResources.getText("lblDataCrowHelp"));

                
                helpWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (hb != null)
                            DcSettings.set(DcRepository.Settings.stHelpFormSize, hb.getSize());
                    }
                });
            }

        } catch (Exception e) {
            logger.error(DcResources.getText("msgErrorOpeningHelp"), e);
            GUI.getInstance().displayErrorMessage("msgErrorOpeningHelp");
            return;
		}
	}
}