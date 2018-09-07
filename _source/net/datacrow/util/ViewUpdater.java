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

package net.datacrow.util;

import javax.swing.SwingUtilities;

import net.datacrow.console.views.IViewComponent;

import org.apache.log4j.Logger;

public class ViewUpdater extends Thread {
    
    private static Logger logger = Logger.getLogger(ViewUpdater.class.getName());
    
    private boolean canceled = false;
    
    private IViewComponent vc;
    
    public ViewUpdater(IViewComponent vc) {
        this.vc = vc;
    }
    
    public void cancel() {
        canceled = true;
    }
    
    @Override
    public void run() {
        int first = vc.getFirstVisibleIndex() - vc.getViewportBufferSize();
        int last = vc.getLastVisibleIndex() + vc.getViewportBufferSize();
        int size = vc.getItemCount();
        
        first = first < 0 ? 0 : first;
        last = last > size ? size : last;
        last = last < 0 ? 0 : last;
        
        for (int i = 0; i < first && !canceled; i++) {
            clearElement(i);
            
            try {
                sleep(100);
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        
        for (int i = last; i < size && !canceled; i++) {
            clearElement(i);
            try {
                sleep(100);
            } catch (Exception e) {
                logger.error("Ërror while trying to sleep a little before clearing the next element", e);
            }
        }
    }
    
    private void clearElement(final int idx) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(
                    new Thread(new Runnable() { 
                        @Override
                        public void run() {
                            vc.clear(idx);
                        }
                    }));
        } else {
            vc.clear(idx);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        vc = null;
        super.finalize();
    }
}
