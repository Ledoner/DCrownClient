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

package net.datacrow.tabs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Base64;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.XMLParser;
import net.datacrow.settings.definitions.DcFieldDefinition;
import net.datacrow.settings.definitions.Definition;

public class Tabs {
	
	private static Logger logger = Logger.getLogger(Tabs.class.getName());
	
	private static final Map<Integer, LinkedList<Tab>> moduleTabs = new LinkedHashMap<Integer, LinkedList<Tab>>();
	private static final Tabs instance;
	
	private File file;
	
	static {
	    instance = new Tabs();
	}
	
	private Tabs() {}	
	
	public static Tabs getInstance() {
        return instance;
    }
	
    public void initialize() {
        File f = new File(DcConfig.getInstance().getApplicationSettingsDir(), "tabs.xml");
        
        boolean success = false;
        if (f.exists())
            success = load(f);
        
        if (!success)
            setup();
        
        file = f;
    }
	
    private void setup() {
        String tabName;
        Tab tab;
        Collection<String> tabNames;
        for (DcModule module : DcModules.getModules()) {
            tabNames = new ArrayList<String>();
            for (Definition def : module.getSettings().getDefinitions(DcRepository.ModuleSettings.stFieldDefinitions).getDefinitions()) {
                tabName = ((DcFieldDefinition) def).getTab();
                
                if (!CoreUtilities.isEmpty(tabName) && !tabNames.contains(tabName)) {
                    tabNames.add(tabName);
                    tab = new Tab(module.getIndex(), tabName, null);
                    addTab(tab);
                }
            }
        }
    }
    
    private boolean load(File file) {
        
        InputStreamReader in = null;
        BufferedReader reader = null;

        boolean success = false;
        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            in = new InputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader (in); 
            InputSource input = new InputSource(reader);
            Document document = db.parse(input);

            NodeList nodes = document.getDocumentElement().getElementsByTagName("tab");
            
            Element e;
            int module;
            int order;
            String name;
            String tmp;
            Tab tab;
            DcImageIcon icon = null;
            
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                e = (Element) nodes.item(idx);
                module = XMLParser.getInt(e, Tab._MODULE);
                name = XMLParser.getString(e, Tab._NAME);
                order = XMLParser.getInt(e, Tab._ORDER);
                tmp = XMLParser.getString(e, Tab._ICON);
                if (tmp != null) {
                    tmp = tmp.replaceAll("\r", "");
                    tmp = tmp.replaceAll("\n", "");
                    icon = CoreUtilities.base64ToImage(tmp);
                }
                
                tab = new Tab(module, name, icon);
                tab.setOrder(order);
                addTab(tab);
            }
            
            success = true;
        
        } catch (Exception e) {
            logger.error("Failed to load tabs from " + file, e);
            
        } finally {
            try { 
                if (in != null) in.close();
                if (reader != null) reader.close();
            } catch (Exception e) {
                logger.debug("error while closing file input stream", e);
            }
        }
        
        return success;
    }
	
	public void remove(Tab tab) {
	    List<Tab> tabs = getTabs(tab.getModule());
	    tabs.remove(tab);
	}
	
	/**
	 * Overwrite all tabs with the newly delivered tab listing
	 * @param module
	 * @param tabs
	 */
	public void setTabs(int module, List<Tab> tabs) {
	    moduleTabs.put(Integer.valueOf(module), null);
	    for (Tab tab : tabs) {
	        addTab(tab);
	    }
	}
	
	public void addTab(Tab tab) {
		LinkedList<Tab> tabs = moduleTabs.get(Integer.valueOf(tab.getModule()));
		tabs = tabs == null ? new LinkedList<Tab>() : tabs;
	    
		if (!tabs.contains(tab)) {
		    String name = tab.getName();
		    
		    if (tab.getOrder() == 0) {
                int order = name.equals(DcResources.getText("lblInformation")) || name.equals("lblInformation") ? 2 : 
                            name.equals(DcResources.getText("lblSummary")) || name.equals("lblSummary") ? 1 : 3;
                tab.setOrder(order);
		    }
    
            if (tab.getIcon() == null) {
                if (    name.equalsIgnoreCase(DcResources.getText("lblInformation")) || 
                        name.equals(DcResources.getText("lblSummary")) ||
                        name.equalsIgnoreCase("lblInformation") ||
                        name.equalsIgnoreCase("lblSummary")) {
                    tab.setIcon(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons" + File.separator + "information.png"));
                } else if (
                        name.equalsIgnoreCase(DcResources.getText("lblTechnicalInfo")) ||
                        name.equalsIgnoreCase("lblTechnicalInfo")) {
                    tab.setIcon(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons" + File.separator + "informationtechnical.png"));
                } else {
                    for (String result : DcResources.getTextAllLanguages("lblInformation", null)) {
                        if (result.equals(name))
                            tab.setIcon(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons" + File.separator + "information.png"));
                    }
                    for (String result : DcResources.getTextAllLanguages("lblSummary", null)) {
                        if (result.equals(name))
                            tab.setIcon(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons" + File.separator + "information.png"));
                    }
                    for (String result : DcResources.getTextAllLanguages("lblTechnicalInfo", null)) {
                        if (result.equals(name))
                            tab.setIcon(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons" + File.separator + "informationtechnical.png"));
                    }
                }
            }

            if (name.startsWith("lbl")) {
                name = DcResources.getText(name);
                tab.setName(name);
            }
            
	        tabs.add(tab);
	    }
	    
	    moduleTabs.put(Integer.valueOf(tab.getModule()), tabs);
	}
	
	public Tab getTab(int module, String name) {
	    
	    if (CoreUtilities.isEmpty(name)) return null;
	    
		List<Tab> tabs = getTabs(module);
		
		Tab tab = null;
		for (Tab t : tabs) {
		    if ( t.getName().equalsIgnoreCase(name) ||
		        (name.startsWith("lbl") && t.getName().equalsIgnoreCase(DcResources.getText(name)))) {
		    	tab = t;
		    	break;
		    }
		}
		
		if (tab == null) {
		    tab = new Tab(module, name, null);
		    addTab(tab);
		}
		
		return tab;
	}
	
	public List<Tab> getTabs(int module) {
		Integer idx = Integer.valueOf(module);
		LinkedList<Tab> tabs = moduleTabs.get(idx);
		
		if (tabs == null) {
		    tabs =  new LinkedList<Tab>(); 
			moduleTabs.put(idx, tabs);
		} else {
		    Collections.sort(tabs);
		}
		
		return tabs;
	}
	   
    public void save() {
        
        if (file == null) return;
        
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");
                
        byte[] b;
        sb.append("<tabs>\r\n");
        for (Collection<Tab> tabs : moduleTabs.values()) {
            for (Tab tab : tabs) {
                sb.append("<tab>\r\n");
                sb.append("<name>");
                sb.append(tab.getName());
                sb.append("</name>\r\n");
                sb.append("<module>");
                sb.append(tab.getModule());
                sb.append("</module>\r\n");
                sb.append("<order>");
                sb.append(tab.getOrder());
                sb.append("</order>\r\n");
                
                sb.append("<icon>");
               
                DcImageIcon icon = tab.getIcon();
                if (icon != null) {
                    b = icon.getBytes();
                    sb.append(Base64.encode(b)); 
                }
                sb.append("</icon>\r\n");
                sb.append("</tab>\r\n");
            }
        }
        
        sb.append("</tabs>\r\n");
        
        try {
            CoreUtilities.writeToFile(sb.toString().getBytes("UTF8"), file);
        } catch (Exception e) {
            logger.error("Error while storing the tabs XML file", e);
        }
    }
}
