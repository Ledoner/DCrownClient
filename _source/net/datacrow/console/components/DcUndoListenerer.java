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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.datacrow.core.resources.DcResources;

public class DcUndoListenerer {

    private final UndoManager undo = new UndoManager();
    
    private final UndoAction undoAction;
    private final RedoAction redoAction;
    
    public DcUndoListenerer(JTextComponent component) {
        this.undoAction = new UndoAction();
        this.redoAction = new RedoAction();
        
        component.getDocument().addUndoableEditListener(new UndoEditListener());
        
        component.getActionMap().put("Undo", undoAction);
        component.getInputMap().put(KeyStroke.getKeyStroke("control Z"), DcResources.isInitialized() ? DcResources.getText("lblUndo") : "Undo");
        component.getActionMap().put("Redo", redoAction);
        component.getInputMap().put(KeyStroke.getKeyStroke("control Y"), DcResources.isInitialized() ? DcResources.getText("lblRedo") : "Redo");
    }
    
    public UndoAction getUndoAction() {
        return undoAction;
    }
    
    public RedoAction getRedoAction() {
        return redoAction;
    }
    
    protected class UndoEditListener implements UndoableEditListener {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
            updateUndoState();
            updateRedoState();
        }
    }
    
    public void redo() {
        try {
            undo.redo();
        } catch (CannotRedoException ignore) {}
        updateRedoState();
        updateUndoState();
    }

    protected void updateRedoState() {
        if (undo.canRedo()) {
            redoAction.setEnabled(true);
            redoAction.putValue(Action.NAME, undo.getRedoPresentationName());
        } else {
            redoAction.setEnabled(false);
            redoAction.putValue(Action.NAME, DcResources.isInitialized() ? DcResources.getText("lblRedo") : "Redo");
        }
    }
    
    public void undo() {
        try {
            undo.undo();
        } catch (CannotUndoException ignore) {}
        updateUndoState();
        updateRedoState();        
    }

    protected void updateUndoState() {
        if (undo.canUndo()) {
            undoAction.setEnabled(true);
            undoAction.putValue(Action.NAME, undo.getUndoPresentationName());
        } else {
            undoAction.setEnabled(false);
            undoAction.putValue(Action.NAME, DcResources.isInitialized() ? DcResources.getText("lblUndo") : "Undo");
        }
    }    
    
    public class UndoAction extends AbstractAction {
        public UndoAction() {
            super(DcResources.isInitialized() ? DcResources.getText("lblUndo") : "Undo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undo();
        }
    }

    public class RedoAction extends AbstractAction {
        public RedoAction() {
            super(DcResources.isInitialized() ? DcResources.getText("lblRedo") : "Redo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            redo();
        }
    }    
}
