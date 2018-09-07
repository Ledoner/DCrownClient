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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.datacrow.console.GUI;
import net.datacrow.console.menu.DcEditorMouseListener;
import net.datacrow.console.windows.TextDialog;

import org.apache.log4j.Logger;

public class DcLongTextField extends JTextArea implements IComponent, MouseListener {

    private static Logger logger = Logger.getLogger(DcLongTextField.class.getName());
    
    private DcUndoListenerer undoListener;
    
    public DcLongTextField() {
        super();
        addMouseListener(this);
        undoListener = new DcUndoListenerer(this);
        addMouseListener(new DcEditorMouseListener());
        setLineWrap(true);
        setEditable(true);
    }
    
    public DcUndoListenerer getUndoListener() {
        return undoListener;
    }
    
    @Override
    protected Document createDefaultModel() {
        return new LongStringDocument();
    }

    @Override
    public Object getValue() {
        return getText();
    }

    @Override
    public void clear() {
        undoListener = null;
    }
    
    @Override
    public void setValue(Object o) {
        setText((String) o);
        setCaretPosition(0);
    }
    
    protected class LongStringDocument extends PlainDocument {
        @Override
        public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
            super.insertString(i, s, attributeset);
        }
    }

    public void openTextWindow() {
        TextDialog textView = new TextDialog(getValue().toString(), isEditable() && isEnabled());
        if (textView.isSuccess())
            setText(textView.getText());
        textView.clear();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2)
            openTextWindow();
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(GUI.getInstance().setRenderingHint(g));
        } catch (Exception e) {
            logger.debug(e, e);
        }
    }
    
    @Override
    public void refresh() {}
}
