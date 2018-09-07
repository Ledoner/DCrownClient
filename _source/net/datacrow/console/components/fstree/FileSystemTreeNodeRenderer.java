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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import net.datacrow.core.DcRepository;
import net.datacrow.settings.DcSettings;

class FileSystemTreeNodeRenderer implements TreeCellRenderer {

    private JCheckBox leafRenderer = new JCheckBox();
    
    public FileSystemTreeNodeRenderer() {}
    
    protected JCheckBox getLeafRenderer() {
        return leafRenderer;
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded, 
            boolean leaf, int row, boolean hasFocus) {

        FileSystemTreeNode node = (FileSystemTreeNode) value;
        
        leafRenderer.setText(node.toString());
        leafRenderer.setSelected(node.isSelected());
        leafRenderer.setEnabled(tree.isEnabled());

        if (selected)
            leafRenderer.setBackground(DcSettings.getColor(DcRepository.Settings.stSelectionColor));
        else
            leafRenderer.setBackground(Color.WHITE);

        return leafRenderer;
    }
}
