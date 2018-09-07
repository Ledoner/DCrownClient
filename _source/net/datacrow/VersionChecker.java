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

package net.datacrow;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.swing.SwingUtilities;

import net.datacrow.console.windows.VersionCheckerDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.Version;
import net.datacrow.core.http.HttpConnection;
import net.datacrow.core.http.HttpConnectionUtil;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class VersionChecker extends Thread {

    private static Logger logger = Logger.getLogger(VersionChecker.class.getName());

    private static final String file = "http://www.datacrow.net/version.properties"; 
    
    public static final String _VERSION = "version";
    public static final String _DOWNLOAD_URL = "download_url";
    public static final String _INFO_URL = "information_url";
    public static final String _COMMENT = "comment";
    public static final String _TYPE = "type";
    
    private URL address;
    private Properties properties;
    
    public VersionChecker() {
        properties = new Properties();
        setName("Version-Checker");
    }
    
    @Override
    public void run() {
        // Give Data Crow enough time to start
        try {
            sleep(20000);
        } catch (Exception ignore) {}
        
        try {
            address = new URL(file);
        } catch (Exception e) {
            logger.debug(e, e);
            return;
        }
        
        boolean checked = false;
        while (!checked) {
            try {
                HttpConnection conn =  HttpConnectionUtil.getConnection(address);
                InputStream is = conn.getInputStream();
                properties.load(is);

                String version = (String) properties.get(_VERSION);
                String downloadUrl = (String) properties.get(_DOWNLOAD_URL);
                String infoUrl = (String) properties.get(_INFO_URL);
                
                if (version == null) {
                    checked = true;
                    continue;
                }
                
                if (DcConfig.getInstance().getVersion().isOlder(new Version(version))) {
                    final String html = 
                        "<html><body " + Utilities.getHtmlStyle() + ">\n" +
                        DcResources.getText("msgNewVersion", 
                                new String[] {version, 
                                              "<a href=\"" + downloadUrl + "\">http://www.datacrow.net</a>", 
                                              "<a href=\"" + infoUrl + "\">" + DcResources.getText("lblHere") +  "</a>"}) +
                        "</body> </html>";

                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            VersionCheckerDialog dlg = new VersionCheckerDialog(html);
                            dlg.setVisible(true);
                        }
                    });
                } 

                checked = true;

                properties.clear();
                is.close();
                
                address = null;
                properties = null;
                
            } catch (Exception e) {
                logger.warn("Failed to check if a new version was released", e);
                break;
            }
        }
    }
}
