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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcMenu;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

public class TextEditMenu extends DcMenu {

    public TextEditMenu(DcLongTextField fld) {
        super(DcResources.getText("lblEdit"));
        build(fld);
    }
    
    private void build(DcLongTextField fld) {
        HashMap<Object, Action> actions = new HashMap<Object, Action>();
        Action[] actionsArray = fld.getActions();
        Action a;
        for (int i = 0; i < actionsArray.length; i++) {
            a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }

        JMenuItem menuUndo = ComponentFactory.getMenuItem(fld.getUndoListener().getUndoAction());
        JMenuItem menuRedo = ComponentFactory.getMenuItem(fld.getUndoListener().getRedoAction());
        JMenuItem menuCut = ComponentFactory.getMenuItem(IconLibrary._icoCut, DcResources.getText("lblCut"));
        JMenuItem menuCopy = ComponentFactory.getMenuItem(IconLibrary._icoCopy, DcResources.getText("lblCopy"));
        JMenuItem menuPaste = ComponentFactory.getMenuItem(IconLibrary._icoPaste, DcResources.getText("lblPaste"));
        JMenuItem menuSelectAll = ComponentFactory.getMenuItem(DcResources.getText("lblSelectAll"));

        menuCut.addActionListener(actions.get(DefaultEditorKit.cutAction));
        menuCopy.addActionListener(actions.get(DefaultEditorKit.copyAction));
        menuPaste.addActionListener(actions.get(DefaultEditorKit.pasteAction));
        menuSelectAll.addActionListener(actions.get(DefaultEditorKit.selectAllAction));
        
        menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        menuRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        
        add(menuCut);
        add(menuCopy);
        add(menuPaste);
        addSeparator();
        add(menuUndo);
        add(menuRedo);
        addSeparator();
        add(menuSelectAll);
    }
}
