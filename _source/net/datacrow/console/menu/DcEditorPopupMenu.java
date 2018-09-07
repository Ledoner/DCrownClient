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

package net.datacrow.console.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcMenuItem;
import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

public class DcEditorPopupMenu extends DcPopupMenu implements ActionListener  {

    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    
    private JTextComponent c;
    
    public DcEditorPopupMenu(JTextComponent c) {
        this.c = c;
        
        JMenuItem menuCut = ComponentFactory.getMenuItem(IconLibrary._icoCut, DcResources.getText("lblCut"));
        JMenuItem menuCopy =ComponentFactory.getMenuItem(IconLibrary._icoCopy, DcResources.getText("lblCopy"));
        JMenuItem menuPaste = ComponentFactory.getMenuItem(IconLibrary._icoPaste, DcResources.getText("lblPaste"));
        JMenuItem menuSelectAll = ComponentFactory.getMenuItem(DcResources.getText("lblSelectAll"));
         
        boolean isEditable = c.isEditable();
        boolean isTextSelected = c.getSelectedText() != null;

        menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

        menuSelectAll.setActionCommand("selectAll");
        menuPaste.setActionCommand("paste");
        menuCopy.setActionCommand("copy");        
        menuCut.setActionCommand("cut");        
        
        if (isEditable && isTextSelected)
            menuCut.addActionListener(this);
        else
            menuCut.setEnabled(false);
        
        if (isTextSelected)
            menuCopy.addActionListener(this);
        else
            menuCopy.setEnabled(false);
        
        Transferable content = tk.getSystemClipboard().getContents(null);
        if (isEditable && content.isDataFlavorSupported(DataFlavor.stringFlavor))
            menuPaste.addActionListener(this);
        else
            menuPaste.setEnabled(false);
        
        if (    c.getText().length() > 0 && 
               (c.getSelectedText() == null || 
                c.getSelectedText().length() < c.getText().length())) {
            
            menuSelectAll.addActionListener(this);
        } else {
            menuSelectAll.setEnabled(false);
        }

        this.add(menuCut);
        this.add(menuCopy);
        this.add(menuPaste);
        
        if (c instanceof DcShortTextField || c instanceof DcLongTextField) {
            this.addSeparator();

            DcMenuItem menuUndo;
            DcMenuItem menuRedo;

            if (c instanceof DcShortTextField) {
                menuUndo = ComponentFactory.getMenuItem(((DcShortTextField) c).getUndoListener().getUndoAction());
                menuRedo = ComponentFactory.getMenuItem(((DcShortTextField) c).getUndoListener().getRedoAction());
            } else {
                menuUndo = ComponentFactory.getMenuItem(((DcLongTextField) c).getUndoListener().getUndoAction());
                menuRedo = ComponentFactory.getMenuItem(((DcLongTextField) c).getUndoListener().getRedoAction());
            }

            menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
            menuRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));

            this.add(menuUndo);
            this.add(menuRedo);
            
            if (!isEditable) {
                menuUndo.setEnabled(false);
                menuRedo.setEnabled(false);
            }
        }
        
        this.addSeparator();
        this.add(menuSelectAll);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("cut"))
           c.cut(); 
        else if (ae.getActionCommand().equals("copy"))
           c.copy(); 
        else if (ae.getActionCommand().equals("paste"))
           c.paste(); 
        else if (ae.getActionCommand().equals("selectAll"))
           c.selectAll(); 
    }
}
