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

package net.datacrow.plugins;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JToolBar;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.DcConfig;
import net.datacrow.core.UserMode;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.InvalidPluginException;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

/**
 * Helps in placing plugins in menus and on toolbars. Is capable on deciding, with the
 * help of the user permissions, if a plugin should be displayed or not.
 * 
 * @author Robert Jan van der Waals
 */
public class PluginHelper {
    
    private static Logger logger = Logger.getLogger(PluginHelper.class.getName());

    public static void registerKey(JRootPane pane, String key) {
        registerKey(pane, key, -1, -1, -1);
    }
    
    public static void registerKey(JRootPane pane, String key, int viewIdx, int moduleIdx, int viewType) {
        try {
            Plugin plugin = Plugins.getInstance().get(key, null, null, viewIdx, moduleIdx, viewType);
            if (plugin != null) {
                
                Connector connector = DcConfig.getInstance().getConnector();
                if (!plugin.isAuthorizable() ||
                     connector.getUser().isAuthorized(plugin)) {
                    
                    String name = viewIdx > -1 ? key + "-" + viewIdx : moduleIdx > -1 ? key + "-" + moduleIdx : key;
                    pane.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(plugin.getKeyStroke(), name);
                    pane.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(plugin.getKeyStroke(), name);
                    pane.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(plugin.getKeyStroke(), name);
                    pane.getRootPane().getActionMap().put(name, plugin);
                }
            } else {
                logger.error("No valid plugin available for " + key);
            }
        } catch (InvalidPluginException e) {
            logger.error(e, e);
        }
    }
    
    public static void addListener(JButton button, 
                                   String key, 
                                   int moduleIdx,
                                   int viewType) {
        
        try {
            Plugin plugin = Plugins.getInstance().get(key, null, null, -1, moduleIdx, viewType);
            button.addActionListener(plugin);
        } catch (InvalidPluginException e) {
            logger.error(e, e);
            button.setEnabled(false);
        }
    }
    
    public static void add(JComponent c, String key) {
        add(c, key, null, null, null, -1, -1, -1);
    }
    
    public static void add(JComponent c, String key, int moduleIdx) {
        add(c, key, null, null, null, -1, moduleIdx, -1);
    }
    
    public static void add(JComponent c, String key, int moduleIdx, int viewType) {
        add(c, key, null, null, null, -1, moduleIdx, viewType);
    }
    
    public static void add(JComponent c, String key, int moduleIdx, int viewIdx, int viewType) {
        add(c, key, null, null, null, viewIdx, moduleIdx, viewType);
    }

    public static void add(JComponent c, String key, String label, int moduleIdx) {
        add(c, key, label, null, null, -1, moduleIdx, -1);
    }
    
    public static void add(JComponent c, String key, String label, DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        try {
            Plugin plugin = Plugins.getInstance().get(key, dco, template, viewIdx, moduleIdx, viewType);
            
            if (plugin != null && label != null && label.length() > 0)
                plugin.setLabel(label);
            
            if (plugin != null) {
                
                Connector connector = DcConfig.getInstance().getConnector();
                if (connector.getUser().isAuthorized(plugin) &&
                    UserMode.isCorrectXpLevel(plugin.getXpLevel())) {
                    
                    AbstractButton button = c instanceof JToolBar ? 
                            ComponentFactory.getToolBarButton(plugin) :
                            ComponentFactory.getMenuItem(plugin);
                            
                    if (plugin.getKeyStroke() != null && button instanceof JMenuItem)
                        ((JMenuItem) button).setAccelerator(plugin.getKeyStroke());
                        
                    button.setIcon(plugin.getIcon());
                    button.setEnabled(plugin.isEnabled());
                    
                    c.add(button);
                }
            } else {
                logger.error("No valid plugin available for " + key);    
            }
        } catch (InvalidPluginException e) {
            logger.error(e, e);
        }
    }
}
