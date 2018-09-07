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
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.datacrow.core.utilities.filefilters.FileNameFilter;

public class FileSystemTreeNode extends DefaultMutableTreeNode {

    private File file;
    boolean selected;
    private List<String> children = new ArrayList<String>();
    private FileNameFilter filter;

    public FileSystemTreeNode(File file, FileNameFilter filter) {
        this.file = file;
        this.filter = filter;
        
        String[] list = file.list(filter);
        if (list != null) {
            for (String s : list)
                children.add(s);
        }
    }

    @Override
    public boolean isLeaf() {
        return children.size() == 0;
    }
    
    public FileSystemTreeNode getChild(int index) {
        String name = children.get(index);
        return new FileSystemTreeNode(new File(file.toString(), name), filter);
    }
    
    public String getParentPath() {
        return file.getParent();
    }
    
    @Override
    public int getChildCount() {
        return children.size();
    }
    
    public int getExistingChildCount() {
        return super.getChildCount();
    }    
    
    public String getName() {
        return file.getName();
    }

    @Override
    public int getIndex(TreeNode child) {
        String name = ((FileSystemTreeNode) child).getName();
        return children.indexOf(name);
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        selected = b;
    }
    
    public String getText() {
        return file.toString();
    }

    @Override
    public String toString() {
        String s = getName();
        return s == null || s.length() == 0 ? getText() : s;
    }

    @Override
    protected void finalize() throws Throwable {
        file = null;
        
        if (children != null)
            children.clear();
        
        children = null;
        
        super.finalize();
    }
}
