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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.utilities.StringUtils;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class DcMultiLineToolTipUI extends BasicToolTipUI {

    private static Logger logger = Logger.getLogger(DcMultiLineToolTipUI.class.getName());
    
    private final static DcMultiLineToolTipUI sharedInstance = new DcMultiLineToolTipUI();
    private CellRendererPane rendererPane;
    private static DcTextPane textPane;
    private boolean containsText = true;

    public DcMultiLineToolTipUI() {
        super();
        if (textPane == null) {
            textPane = ComponentFactory.getTextPane();
            textPane.setDocument(new HTMLDocument());
            textPane.setEditorKit(new HTMLEditorKit());
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        rendererPane = new CellRendererPane();
        rendererPane.add(textPane);
        c.add(rendererPane);
    }
    
    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        c.remove(rendererPane);
        
        if (rendererPane != null) 
            rendererPane.remove(textPane);
        
        rendererPane = null;
    }
    
    @Override
    public void paint(Graphics g, JComponent c) {
        if (containsText) {
            Dimension size = c.getSize();
            rendererPane.paintComponent(g, textPane, c, 1, 1, size.width - 1, size.height - 1, true);
        } else {
            g.dispose();
        }
    }
    
    private void setHtmlText(String text) {
        String s = getHtmlText(text);
        
        HTMLDocument document = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();
        
        try {
            textPane.setText("");
            kit.insertHTML(document, 0, s, 0, 0, null);
        } catch (Exception e) {
            logger.error("Could not insert html into the html document", e);
        }
    }
    
    private String getHtmlText(String s) {
        String text = s;
        
        String html = "";
        int length = text != null ? text.length() : 0;
        if (length > 0) { 
            text = length > 1000 ? StringUtils.concatUserFriendly(s, 1500) : text;
            text = text.replaceAll("[\r\n]", "<br>");
            text = text.replaceAll("[\t]", "    ");
            
            StringBuffer sb = new StringBuffer();
            sb.append("<html><body><table ");
            sb.append(getHtmlStyle(length));
            sb.append("><tr><td>");
            sb.append(text);
            sb.append("</td></tr></table></body></html>");
            html = sb.toString();
            containsText = true;
        } else {
            containsText = false;
        }
        
        return html;
    }
    
    private String getHtmlStyle(int length) {
        String width = "";
        if (length > 100) width = "width:400";
        return Utilities.getHtmlStyle(width, null);
    }       
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip)c).getTipText();
        Dimension dim = new Dimension(0,0);
        setHtmlText(tipText);
        
        if (containsText) {
            int width = ((DcMultiLineToolTip)c).getFixedWidth();
            int columns = ((DcMultiLineToolTip)c).getColumns();
            
            if (columns > 0) {
                textPane.setSize(0,0);
                textPane.setSize( textPane.getPreferredSize() );
            } else if (width > 0) {
                Dimension d = textPane.getPreferredSize();
                d.width = width;
                d.height++;
                textPane.setSize(d);
            } 
            dim = textPane.getPreferredSize();
            dim.height += 1;
            dim.width += 1;
        }
        return dim;
    }
    
    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }
    
    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}
