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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ViewKeyListener implements KeyListener {

    private View view;
    
    private int rowFrom = -1;
    private int rowTo = -1;

    public ViewKeyListener(View view) {
        this.view = view;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        IViewComponent vc = view.getViewComponent();
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
           rowFrom = vc.getSelectedIndex();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ((view.allowsVerticalTraversel() && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) ||
            (view.allowsHorizontalTraversel() && ( e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT))) {
            
            IViewComponent vc = view.getViewComponent();
            rowTo = vc.getSelectedIndex();
            if (rowFrom != rowTo)
                view.setSelected(vc.getSelectedIndex());
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}

}