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

package net.datacrow.console.components.fstree;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

class FileSystemTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {

    private FileSystemTreeNodeRenderer renderer = new FileSystemTreeNodeRenderer();
    
    public FileSystemTreeNodeEditor() {}

    @Override
    public Object getCellEditorValue() {
        JCheckBox cb = renderer.getLeafRenderer();
        return new FileSystemTreeNode(new File(cb.getText()), null);
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        return true;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row) {

        
        JCheckBox editor = (JCheckBox) renderer.getTreeCellRendererComponent(
                tree, value, true, expanded, leaf, row, true);

        // editor always selected / focused
        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (stopCellEditing())
                    fireEditingStopped();
            }
        };
        
        editor.addItemListener(itemListener);
        
        FileSystemTreeNode node = (FileSystemTreeNode) value;
        node.setSelected(!node.isSelected());
        ((FileSystemTreeModel) tree.getModel()).setSelectedRecurse(node, node.isSelected());
        
        
        tree.repaint();

        return editor;
    }

}
