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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;

import net.datacrow.console.GUI;
import net.datacrow.console.windows.BrowserDialog;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.util.filefilters.PictureFileFilter;

public class DcIconSelectField extends DcImageLabel implements MouseListener {

    private final Dimension size;
    private boolean changed = false;
    
    public DcIconSelectField(Dimension size) {
        super();
        
        setOpaque(true);
        addMouseListener(this);
        
        this.size = size;
    }
    
    public DcIconSelectField(ImageIcon icon) {
        super(icon);
        repaint();
        
        setOpaque(true);
        addMouseListener(this);
        
        this.size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
    }
    
    @Override
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        repaint();
        revalidate();
    }
    
    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }
    
    private void loadImage() {
        PictureFileFilter filter = new PictureFileFilter();
        BrowserDialog dlg = new BrowserDialog("", filter);
        File file = dlg.showOpenFileDialog(this, null);
        
        if (file == null)
            return;
        
        try {
            byte[] bytes = CoreUtilities.readFile(file);
            Image image = CoreUtilities.getScaledImage(bytes, size.width, size.height);
            setIcon(new DcImageIcon(image));
            image = null;
            changed = true;
        } catch (Exception exp) {
            GUI.getInstance().displayErrorMessage(exp.toString());
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        loadImage();
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}

    public boolean isChanged() {
        return changed;
    }
}
