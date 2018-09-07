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

package net.datacrow.console.components;

import java.awt.Graphics;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.menu.DcEditorMouseListener;
import net.datacrow.console.windows.itemforms.IItemFormListener;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Utilities;
import net.datacrow.util.launcher.FileLauncher;
import net.datacrow.util.launcher.URLLauncher;

import org.apache.log4j.Logger;

public class DcHtmlEditorPane extends JEditorPane implements HyperlinkListener, IItemFormListener {

    private static Logger logger = Logger.getLogger(DcHtmlEditorPane.class.getName());
    
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument document = new HTMLDocument();
    
    public DcHtmlEditorPane() {
        setFont(ComponentFactory.getStandardFont());
        setEditorKit(kit);
        setDocument(document);
        setEditable(false);
        setBounds(1,1,1,10);

        addHyperlinkListener(this);
        addMouseListener(new DcEditorMouseListener());
    }
    
    public void setHtml(String s) {
        try {
            StringReader sr = new StringReader(s);
            read(sr, "Data Crow");
            sr.close();
        } catch (Exception e) {
            logger.error("Error while loading Html", e);
        }
    }
    
    public String createLink(DcObject dco, String description) {
        DcImageIcon icon = DcIconCache.getInstance().getIcon(dco);
        StringBuffer sb = new StringBuffer();
        
        if (icon != null && icon.exists()) {
            sb.append("<img border=\"0\" src=\"");
            
            String filename = icon.getFilename();
            filename = filename.startsWith("/") ? filename.substring(1) : filename;
            
            sb.append("file:///" + icon.getFilename());
            sb.append("\">");
            sb.append("&nbsp;");
        }
        
        sb.append("<a");
        sb.append(" href=\"http://");
        
        String ID = dco.getModule().getType() == DcModule._TYPE_MAPPING_MODULE ?
                dco.getDisplayString(DcMapping._B_REFERENCED_ID) : dco.getID();
        
        sb.append(ID);
        sb.append("?module=");
        
        if (dco.getModule().getType() == DcModule._TYPE_MAPPING_MODULE)
            sb.append(((DcMapping) dco).getReferencedModuleIdx());    
        else 
            sb.append(dco.getModule().getIndex());
        
        sb.append("\">");

        
        sb.append("<span ");
        sb.append(Utilities.getHtmlStyle(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal)));
        sb.append(">");
        sb.append(description); 
        sb.append("</span>");
        
        sb.append("</a>");
        
        return sb.toString();
    }
    
    @Override
    public void notifyItemSaved(DcObject dco) {}

    public String createLinks(Collection<DcObject> items) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (DcObject dco : items) {

            if (dco.getModule().getType() == DcModule._TYPE_MAPPING_MODULE)
                dco = ((DcMapping) dco).getReferencedObject();
            
            if (dco == null) continue;
            
            sb.append(createLink(dco, dco.toString()));
            
            if (i < items.size() - 1)
                sb.append("&nbsp;&nbsp;");
            
            i++;
        }
        return sb.toString();
    }
    
    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = e.getURL();

            String ID = url.getAuthority();
            String query = url.getQuery();
            
            if (url.getProtocol().equals("file")) {
                String file = url.toString();
                file = file.indexOf("?original=") > 0 ? file.substring(file.indexOf("?original=") + "?original=".length()) : file;
                file = file.replaceAll("%20", " ");
                new FileLauncher(file).launch();
            } else if (query == null || !query.contains("module=")) {
                try {
                	URLLauncher launcher = new URLLauncher(url);
                	launcher.launch();
                } catch (Exception exp) {
                    logger.error(exp, exp);
                }
            } else {
                Connector connector = DcConfig.getInstance().getConnector();
                int module = Integer.valueOf(query.substring(query.indexOf("=") + 1));
                DcObject dco = connector.getItem(module, ID);
                
                if (dco != null) {
                    dco.markAsUnchanged();
                    ItemForm form = new ItemForm(!DcSettings.getBoolean(DcRepository.Settings.stOpenItemsInEditModus), true, dco, false);
                    form.setListener(this);
                    form.setVisible(true);
                }
            }
        }
    }     
    
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(GUI.getInstance().setRenderingHint(g));
        } catch (Exception e) {
            logger.debug(e, e);
        }
    }    
}
