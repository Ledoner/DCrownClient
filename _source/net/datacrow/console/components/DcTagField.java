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

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.datacrow.console.components.painter.RectanglePainter;
import net.datacrow.core.DcConfig;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTag;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class DcTagField extends JTextArea implements IComponent, KeyListener, MouseListener {
    
    private static Logger logger = Logger.getLogger(DcTagField.class.getName());
    private static final RectanglePainter highlighter = new RectanglePainter(new Color(153, 204, 255));
    
    private Collection<DcObject> references = new ArrayList<DcObject>();
    
    private static final String separator = "\t";
    
    private int mappingModIdx;
    
    private List<DcObject> existingTags = new ArrayList<DcObject>();
    
    public DcTagField(int mappingModIdx) {
        super();
        
        this.mappingModIdx = mappingModIdx;
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setTabSize(1);
        setLineWrap(true);
        
        try {
            DataFilter df = new DataFilter(DcModules._TAG);
            Connector connector = DcConfig.getInstance().getConnector();
            existingTags = connector.getItems(df);
        } catch (Exception e) {
            logger.error("Could not retrieve the current Tags", e);
        }
    }
    
    @Override
    public void clear() {
        if (existingTags != null) existingTags.clear();
        existingTags = null;
    }
    
    @Override
    public Object getValue() {
        Collection<DcObject> mappings = new ArrayList<DcObject>();
        
        DcObject tag = null;
        DcMapping mapping;
        
        boolean found;
        for (String name : getTags()) {
            found = false;
            for (DcObject existingRef : references) {
                if (name.equals(existingRef.toString())) {
                    mappings.add(existingRef);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                try {
                    Connector connector = DcConfig.getInstance().getConnector();
                    DataFilter df = new DataFilter(DcModules._TAG);
                    df.addEntry(new DataFilterEntry(DcModules._TAG, DcTag._A_NAME, Operator.EQUAL_TO, name));
                    List<DcObject> items = connector.getItems(df);
                    
                    tag = items.size() > 0 ? items.get(0) : null;
                    
                    if (tag == null) {
                        tag = new DcTag();
                        tag.setValue(DcTag._A_NAME, name);
                        tag.setIDs();
                    }
                    
                    mapping = (DcMapping) DcModules.get(mappingModIdx).getItem();
                    mapping.setReference(tag);
                    mapping.setValue(DcMapping._B_REFERENCED_ID, tag.getID());
                    mappings.add(mapping);
                } catch (Exception e) {
                    logger.error("Error while saving Tag " + tag, e);
                }
            }
        }
        
        return mappings;
    }

    @Override
    public void setValue(Object o) {
        
        references = (Collection<DcObject>) o;
        
        StringBuffer sb = new StringBuffer("");
        for (DcObject tag : references) {
            sb.append(tag);
            sb.append(separator);   
        }
        
        setText(sb.toString());
        highlightTags();
    }
    
    private List<String> getTags() {
        String s = getText();
        StringTokenizer st = new StringTokenizer(s, separator);
        
        List<String> tags = new ArrayList<String>();
        String tag;
        while (st.hasMoreElements()) {
            tag = (String) st.nextElement();
            tag = tag.trim();
            
            if (!tags.contains(tag))
                tags.add(tag);
        }
        
        return tags;
    }

/*    private int getLastTagStart() {
        String s = getText();
        while (s.endsWith(separator)) 
            s = s.substring(0, s.length() - 1);
        
        int idx = s.lastIndexOf(separator);
        return idx == -1 ? 0 : idx;
    } */
    
    private Collection<Point> getLocations() {
        String s = getText();
        StringTokenizer st = new StringTokenizer(s, separator);
        
        Collection<Point> locs = new ArrayList<Point>();
        
        int length = 0;
        String word;
        while (st.hasMoreElements()) {
            word = (String) st.nextElement();
            locs.add(new Point(length, length + word.length()));
            length += word.length() + 1;
        }
        
        return locs;
    }
    
    private void highlightTags() {
        this.getHighlighter().removeAllHighlights();
        for (Point point : getLocations()) {
            try {
                this.getHighlighter().addHighlight(point.x, point.y, highlighter);
            } catch (BadLocationException ble) {
                logger.debug(ble, ble);
            }
        }
    }
    
    private char getLastChar() {
        String s = getText();
        return s== null || s.length() == 0 ? '\t' : s.charAt(s.length() - 1);
    }
    
    private int getTextLength() {
        String s = getText();
        return s == null ? 0 : s.length();
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_TAB && getLastChar() == '\t')
            ke.consume();
        else if (    getSelectionEnd() > -1 && 
               (ke.getKeyCode() == KeyEvent.VK_TAB ||
                ke.getKeyCode() == KeyEvent.VK_ENTER)) {
            acceptSuggestion();
        } 
    } 
    
    @Override
    public void keyReleased(KeyEvent ke) {
        if (    ke.getKeyCode() == KeyEvent.VK_TAB ||
                ke.getKeyCode() == KeyEvent.VK_ENTER) {
            highlightTags();
        }
    }
    
    boolean autoCompleting = false;
    
    private void acceptSuggestion() {
        setCaretPosition(getTextLength());
    }
    
    private void autoComplete() {
        autoCompleting = true;
        
        String text = getText();
        
        String tag = text.indexOf(separator) > -1 ? text.substring(text.lastIndexOf(separator) + 1, text.length()) : text;
        
        DcObject matchingTag = null;
        for (DcObject existingTag : existingTags) {
            if (existingTag.toString().startsWith(tag))
                matchingTag = existingTag;
        }
       
        if (matchingTag != null) {
            
            int caret = getCaretPosition();
            
            int start = text.length();
            text = text.substring(0, text.length() - tag.length());
            text += matchingTag.toString();
            int end = text.length();
            setText(text);
            
            setCaretPosition(caret);
            setSelectionStart(start);
            setSelectionEnd(end);
        }
        
        autoCompleting = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void refresh() {}
    
    @Override
    protected Document createDefaultModel() {
        return new TagDocument(this);
    }
    
    protected class TagDocument extends PlainDocument {
        
        private DcTagField fld;
        
        protected TagDocument(DcTagField fld) {
            this.fld = fld;
        }
        
        @Override
        public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
            
            if ((fld.getText().endsWith(" ") && (s.equals(" ") || s.equals(separator) || s.equals("\r") || s.equals("\n")))) {
                return;
            } else if (s.equals(separator) || s.equals("\n") || s.equals("\r")) {
                super.insertString(i, "\t", attributeset);
            } else {
                super.insertString(i, s, attributeset);
                
                if (s.length() == 1 && !autoCompleting)
                    autoComplete();
            }
        }
    }
}
