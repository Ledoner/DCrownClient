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

package net.datacrow.console.components.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.datacrow.console.components.panels.tree.NodeElement;
import net.datacrow.core.DcRepository;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class DcTreeRenderer extends DefaultTreeCellRenderer {

    private static Logger logger = Logger.getLogger(DcTreeRenderer.class.getName());
    private static final EmptyBorder border = new EmptyBorder(2, 5, 2, 2);
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {
    
        try {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
            setBackgroundSelectionColor(DcSettings.getColor(DcRepository.Settings.stSelectionColor));
            setForeground(Color.BLACK);
            setBorder(border);
            setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontBold));
            
            if (value instanceof DefaultMutableTreeNode) {
                Object o = ((DefaultMutableTreeNode) value).getUserObject();
        
                if (o instanceof NodeElement) {
                    ImageIcon icon = ((NodeElement) o).getIcon();
                    setIcon(icon);
                }
            }
        } catch (NullPointerException npe) {
            logger.error(npe, npe);
        }
        
        return this;
    }
}





