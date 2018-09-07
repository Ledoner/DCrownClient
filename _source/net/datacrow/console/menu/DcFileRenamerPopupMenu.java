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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcFilePatternField;
import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;

public class DcFileRenamerPopupMenu extends DcPopupMenu   {
    
    public DcFileRenamerPopupMenu(DcFilePatternField fpf, int modIdx) {
        JMenuItem menuInsertText = ComponentFactory.getMenuItem(DcResources.getText("lblInsertText"));
        JMenuItem menuInsertDir = ComponentFactory.getMenuItem(DcResources.getText("lblInsertDirectory"));
        JMenuItem menuRemove = ComponentFactory.getMenuItem(DcResources.getText("lblRemove"));

        JMenu menuInsertField = ComponentFactory.getMenu(DcResources.getText("lblInsertField"));
        
        DcModule module = DcModules.get(modIdx); 
        JMenuItem menuField;
        for (DcField field : DcModules.get(modIdx).getFields()) {
            
        	if (field.getIndex() == DcObject._SYS_CONTAINER)
        		continue;
        	
            if (field.getIndex() == module.getParentReferenceFieldIndex()) {
                menuField = ComponentFactory.getMenuItem(module.getParent().getObjectName());
                menuField.setActionCommand(String.valueOf(field.getIndex()));
                menuField.addActionListener(fpf);
                menuInsertField.add(menuField);
            } else if (isValid(field)) {
                menuField = ComponentFactory.getMenuItem(field.getSystemName());
                menuField.setActionCommand(String.valueOf(field.getIndex()));
                menuField.addActionListener(fpf);
                menuInsertField.add(menuField);
            }
        }
        
        menuInsertText.setActionCommand("insertText");
        menuInsertDir.setActionCommand("insertDirectory");     
        menuRemove.setActionCommand("remove");

        menuInsertDir.addActionListener(fpf);
        menuInsertText.addActionListener(fpf);
        menuRemove.addActionListener(fpf);
        
        String next = fpf.getNextChar(fpf.getCaretPosition());
        String prev = fpf.getPreviousChar(fpf.getCaretPosition());
        
        String selectedText = fpf.getSelectedText();
        menuInsertDir.setEnabled(!isDirectoryChar(selectedText) && 
                                 !isDirectoryChar(next) && !isDirectoryChar(prev));
        menuRemove.setEnabled(fpf.getSelectedText().length() > 0);
        
        this.add(menuInsertText);
        this.add(menuInsertField);
        this.add(menuInsertDir);
        this.addSeparator();
        this.add(menuRemove);
    }
    
    private boolean isDirectoryChar(String s) {
        return s.equals("\\") || s.equals("/"); 
    }
    
    private boolean isValid(DcField field) {
        int vt = field.getValueType();
        int ft = field.getFieldType(); 
        return field.isEnabled() && 
               vt != DcRepository.ValueTypes._BLOB &&
               vt != DcRepository.ValueTypes._DATE &&
               vt != DcRepository.ValueTypes._DATETIME &&
               vt != DcRepository.ValueTypes._IMAGEICON &&
               vt != DcRepository.ValueTypes._PICTURE &&
               ft != ComponentFactory._LONGTEXTFIELD;
    }
}
