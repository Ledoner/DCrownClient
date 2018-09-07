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

public class DcDecimalField extends JTextField implements IComponent {

    public DcDecimalField() {
        super();
        ComponentFactory.setBorder(this);
    }

    @Override
    protected Document createDefaultModel() {
        return new DecimalDocument();
    }

    @Override
    public Object getValue() {
        String text = getText();
        
        String s = DcSettings.getString(DcRepository.Settings.stDecimalSeparatorSymbol);
        char decimalSep = s != null && s.length() > 0 ? s.charAt(0) : ',';
        s = DcSettings.getString(DcRepository.Settings.stDecimalGroupingSymbol);
        char groupingSep = s != null && s.length() > 0 ? s.charAt(0) : '.';
        
        text = text.replaceAll("\\" + groupingSep, "");
        text = text.replaceAll("\\" + decimalSep, ".");
        
        return text.length() == 0 ? null : Double.valueOf(text);
    }
    
    @Override
    public void clear() {} 
    
    @Override
    public void setValue(Object o) {
        setText(CoreUtilities.toString((Double) o));
    }
    
    private static class DecimalDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String s, AttributeSet a) throws BadLocationException {
            if (s == null || s.trim().length() == 0) return;

            String sep = DcSettings.getString(DcRepository.Settings.stDecimalSeparatorSymbol);
            String grp = DcSettings.getString(DcRepository.Settings.stDecimalGroupingSymbol);
            
            if (s.length() == 1) {
                String check = getText(0, getContent().length());
                if (offs != 0 && s.matches("[0-9,'" + sep + "','" + grp + "']")) {
                    if (s.equals(sep) && check.indexOf(sep) == -1 || !s.equals(sep))
                        super.insertString(offs, s, a);
                } else if (offs == 0 && s.matches("[+,-,0-9]")) {
                    if ( (s.equals("+") && check.indexOf("+") == -1) ||
                         (s.equals("-") && check.indexOf("-") == -1) ||
                         (!s.equals("-") && !s.equals("+")))
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
