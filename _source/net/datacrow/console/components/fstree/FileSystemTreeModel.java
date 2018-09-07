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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.datacrow.core.utilities.filefilters.FileNameFilter;

public class FileSystemTreeModel extends DefaultTreeModel {

    private Collection<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private FileNameFilter filter;

    public FileSystemTreeModel(File rootDirectory, FileNameFilter filter) {
        super(new FileSystemTreeNode(rootDirectory, filter));
        this.filter = filter;
    }

    @Override
    public Object getChild(Object parent, int index) {
        FileSystemTreeNode node = ((FileSystemTreeNode) parent).getChild(index);
        try {
            // check if the node exists
            FileSystemTreeNode child;
            for (Enumeration enumeration = ((FileSystemTreeNode) parent).children(); enumeration.hasMoreElements(); ) {
                child = (FileSystemTreeNode) enumeration.nextElement();
                if (child.getText().equals(node.getText()))
                    return child;
            }
        } catch (Exception e) {}
        
        // if not, add it
        List<String> children = new ArrayList<String>();
        FileSystemTreeNode parentNode = (FileSystemTreeNode) parent;
        
        // determine the position / sort index
        for (int i = 0; i < parentNode.getExistingChildCount(); i++) 
            children.add(((FileSystemTreeNode) parentNode.getChildAt(i)).getText());
        
        children.add(node.getText());
        Collections.sort(children);
        int pos = children.indexOf(node.getText());

        // and add the node
        insertNodeInto(node, (FileSystemTreeNode) parent, pos < 0 ? 0 : pos);
        
        return node;
    }

    @Override
    public int getChildCount(Object parent) {
        return ((FileSystemTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object o) {
        return ((FileSystemTreeNode) o).isLeaf();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((FileSystemTreeNode) parent).getIndex((TreeNode) child);
    }
    
    public void setSelectedRecurse(FileSystemTreeNode node, boolean b) {
        node.setSelected(b);
        FileSystemTreeNode child;
        for (int idx = 0; idx < node.getChildCount(); idx++) {
            child = ((FileSystemTreeNode) getChild(node, idx));
            child.setSelected(b);
            setSelectedRecurse(child, b);
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {
        FileSystemTreeNode oldNode = (FileSystemTreeNode) path.getLastPathComponent();
        String fileParentPath = oldNode.getParentPath();
        
        if (fileParentPath != null) {
            FileSystemTreeNode newNode = (FileSystemTreeNode) value;
    
            FileSystemTreeNode parent = new FileSystemTreeNode(new File(fileParentPath), filter);
            int[] changedChildrenIndices = { getIndexOfChild(parent, newNode) };
            Object[] changedChildren = {newNode};
            fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
        }
    }

    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        
        for (TreeModelListener listener : listeners)
            listener.treeNodesChanged(event);
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }
}