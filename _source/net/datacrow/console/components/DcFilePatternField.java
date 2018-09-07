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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import net.datacrow.console.GUI;
import net.datacrow.console.menu.DcFileRenamerPopupMenu;
import net.datacrow.console.windows.filerenamer.InsertTextDialog;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;

import org.apache.log4j.Logger;

public class DcFilePatternField extends DcLongTextField implements KeyListener, ActionListener {
    
    private static Logger logger = Logger.getLogger(DcFilePatternField.class.getName());
    
    private final int module;
    
    public DcFilePatternField(int module) {
        super();
        this.module = module;
        
        addKeyListener(this);
        addMouseListener(this);
    }
    
    @Override
    public void openTextWindow() {}

    @Override
    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }
    
    public String getNextChar(int start) {
        if (start < getDocument().getLength()) {
            try {
                return getText(start, 1);
            } catch (BadLocationException ble) {
                logger.error(ble, ble);
            }
        }
        return " ";
    }

    public String getPreviousChar(int start) {
        if (start > 0 && start < getDocument().getLength()) {
            try {
                return getText(start - 1, 1);
            } catch (BadLocationException ble) {
                logger.error(ble, ble);
            }
        }
        return " ";
    }
    
    private void insertText() {
        String text = new InsertTextDialog(GUI.getInstance().getRootFrame()).getText();
        if (text.length() > 0) {
            insert(text, getCaretPosition());
        }
    }
    
    private void insertField(int field) {
        DcModule m = DcModules.get(module);
        if (field == m.getParentReferenceFieldIndex()) {
            insert('[' + m.getParent().getObjectName() + ']', getCaretPosition());
        } else {
            insert('[' + m.getField(field).getSystemName() + ']', getCaretPosition());
        }
    }
    
    private void insertDirectory() {
        insert(File.separator, getCaretPosition());
    }

    @Override
    public String getSelectedText() {
        return super.getSelectedText() != null ? super.getSelectedText() : "";
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("insertText"))
            insertText();
        else if (ae.getActionCommand().equals("insertDirectory"))
            insertDirectory();
        else if (ae.getActionCommand().equals("remove"))
            cut();
        else
            insertField(Integer.valueOf(ae.getActionCommand()));
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (getSelectedText().length() == 0) {
            try {
                int offset = viewToModel(e.getPoint());
                int start = javax.swing.text.Utilities.getWordStart(this, offset);
                int end = javax.swing.text.Utilities.getWordEnd(this, offset);
                
                select(start, end);
                
                try {
                    char prev = getPreviousChar(getSelectionStart()).charAt(0);
                    char next = getNextChar(getSelectionEnd()).charAt(0);
                    
                    if (prev == '[' && next == ']') {
                        --start;
                        ++end;
                    } else if (getSelectedText().equals("]")) {
                        String text = getText().substring(0, end);
                        start = text.lastIndexOf('[');
                        
                    } else if (getSelectedText().equals("[")) {
                        String text = getText();
                        start = text.indexOf(']', end);
                    }

                    select(start, end);

                    prev = getPreviousChar(getSelectionStart()).charAt(0);
                    next = getNextChar(getSelectionEnd()).charAt(0);

                    if ((prev == '\\' || prev == '/') && (next == '\\' || next == '/'))
                        select(start, end + 1);
                    
                } catch (Exception exp) {
                    logger.error(exp, exp);
                }
                
            } catch (BadLocationException ble) {
                logger.error(ble, ble);
            }
        }
        
        if (SwingUtilities.isRightMouseButton(e) && getCaretPosition() > -1) {
            DcFileRenamerPopupMenu popupmenu = new DcFileRenamerPopupMenu(this, module);
            popupmenu.validate();
            popupmenu.show(this, e.getX(), e.getY());
        }
    }    
}
