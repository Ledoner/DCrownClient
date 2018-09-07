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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;

public class DcImageLabel extends JComponent implements IComponent {
    
    protected ImageIcon icon;

    public DcImageLabel() {
        setOpaque(false);
    }
    
    public void setIcon(ImageIcon icon) {
        setImage(icon);
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public DcImageLabel(ImageIcon icon) {
        this();
        setImage(icon);
    }
    
    private void setImage(ImageIcon icon) {
        this.icon = icon;
        repaint();
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }

    @Override
    public void clear() {
        flush();
    }

    @Override
    public Object getValue() {
        return getIcon();
    }

    @Override
    public void setEditable(boolean b) {}

    @Override
    public void setValue(Object value) {
        setIcon((ImageIcon) value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(this.getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (icon != null) {
            int centerX = ((super.getWidth() - icon.getIconWidth()) / 2);
            int centerY = ((super.getHeight() - icon.getIconHeight()) / 2);
            g2d.drawImage(icon.getImage(), centerX, centerY, this);
        }
        g2d.dispose();
    }    

    public void flush() {
        if (icon != null)
            icon.getImage().flush();
        
        icon = null;
    }
    
    @Override
    public void refresh() {}
}
