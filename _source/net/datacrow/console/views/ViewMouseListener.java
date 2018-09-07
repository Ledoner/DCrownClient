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

package net.datacrow.console.views;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import net.datacrow.console.menu.ViewPopupMenu;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcObject;
import net.datacrow.settings.DcSettings;

public class ViewMouseListener implements MouseListener {
    
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        IViewComponent vc = (IViewComponent) e.getSource();
        View view = vc.getView();
        
        if (SwingUtilities.isRightMouseButton(e)) {
            // Only change the selection for single item selections
            // or when nothing has been selected as yet.
            if (vc.getSelectedIndices() == null ||
                vc.getSelectedIndices().length == 1) {
                int index = vc.locationToIndex(e.getPoint());
                vc.setSelected(index);
            }

            if (vc.getSelectedIndex() > -1) {
            	DcObject dco = vc.getSelectedItem();
            	
            	// item gotten via "getSelectedItem" is not loaded fully (!) 
            	dco.reload();
                ViewPopupMenu menu = new ViewPopupMenu(dco, view.getType(), view.getIndex(), view.getModule().getIndex()); 
                Component component = (Component) vc;
                menu.setInvoker(component);
                menu.show(component, e.getX(), e.getY());
            }
        }

        if (e.getClickCount() == 2 && vc.getSelectedIndex() > -1) {
            vc.getView().open(!DcSettings.getBoolean(DcRepository.Settings.stOpenItemsInEditModus));
            e.consume();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
}
