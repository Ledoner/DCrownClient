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
import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.menu.DcEditorMouseListener;

public class DcShortTextField extends JTextField implements IComponent {

    protected int MAX_TEXT_LENGTH = 0;
    
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private DcUndoListenerer undoListener;

    public DcShortTextField(int maxTextLength) {
        super();
        MAX_TEXT_LENGTH = maxTextLength;
        undoListener = new DcUndoListenerer(this);
        addMouseListener(new DcEditorMouseListener());
        ComponentFactory.setBorder(this);
    }    
    
    public DcUndoListenerer getUndoListener() {
        return undoListener;
    }
    
    @Override
    public void clear() {
        undoListener = null;
    }
    
    @Override
    protected Document createDefaultModel() {
        return new ShortStringDocument();
    }

    @Override
    public Object getValue() {
        return getText();
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof String)
            setText((String) o);
        else if (o != null)
            setText(o.toString());
        else
            setText("");
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    }
    
    protected class ShortStringDocument extends PlainDocument {
        @Override
        public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
            if (i + 1 > MAX_TEXT_LENGTH && MAX_TEXT_LENGTH != 0) {
                toolkit.beep();
            } else {
                super.insertString(i, s, attributeset);
            }
        }
    }

    @Override
    public void refresh() {}  
}
