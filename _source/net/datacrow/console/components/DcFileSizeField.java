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

import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.core.DcRepository;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;

public class DcFileSizeField extends JTextField implements IComponent {

    public DcFileSizeField() {
        super();
        
        setPreferredSize(new Dimension(50, ComponentFactory.getPreferredFieldHeight()));
        setPreferredSize(new Dimension(50, ComponentFactory.getPreferredFieldHeight()));
    }

    @Override
    protected Document createDefaultModel() {
        return new DecimalDocument();
    }

    @Override
    public Object getValue() {
        String num = "";
        for (char c :  getText().toCharArray()) {
            if (Character.isDigit(c))
                num += c; 
        }
        return num.length() == 0 ? null : Long.valueOf(num);
    }
    
    @Override
    public void clear() {} 
    
    @Override
    public void setValue(Object o) {
        if (o instanceof Long)
            setText(CoreUtilities.toFileSizeString((Long) o));
        else if (o != null) 
            setText(CoreUtilities.toFileSizeString(Long.valueOf(o.toString())));
    }
    
    private static class DecimalDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String s, AttributeSet a) throws BadLocationException {
            if (s == null || s.trim().length() == 0) return;

            String sep = DcSettings.getString(DcRepository.Settings.stDecimalGroupingSymbol);
            
            if (s.length() == 1) {
                String check = getText(0, getContent().length());
                if (offs != 0 && s.matches("[0-9,'" + sep + "']")) {
                    if (s.equals(sep) && check.indexOf(sep) == -1 || !s.equals(sep))
                        super.insertString(offs, s, a);
                } else if (offs == 0 && s.matches("[0-9]")) {
                    super.insertString(offs, s, a);
                }
            } else { 
                super.insertString(offs, s, a);
            }
        }
    } 
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    }    
    
    @Override
    public void refresh() {}
}