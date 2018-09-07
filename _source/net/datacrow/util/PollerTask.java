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

import net.datacrow.console.windows.onlinesearch.ProgressDialog;
import net.datacrow.core.console.IPollerTask;

public class PollerTask extends Thread implements IPollerTask {
        
    private Thread thread;
    
    private final ProgressDialog dlg;
    
    private boolean finished = false;
    
    public PollerTask(Thread thread, String title) {
        setPriority(Thread.MIN_PRIORITY);
        this.thread = thread;
        this.dlg = new ProgressDialog(title);
    }
    
    @Override
    public void finished(boolean b) {
        finished = b;
    }
    
    @Override
    public void setText(final String text) {
        SwingUtilities.invokeLater(
                new Thread(new Runnable() { 
                    @Override
                    public void run() {
                        dlg.setText(text);                            
                    }
                }));
    }

    @Override
    public void run() {
        
        while (!finished && thread.isAlive()) {
            SwingUtilities.invokeLater(
                    new Thread(new Runnable() { 
                        @Override
                        public void run() {
                            dlg.update();                                
                        }
                    }));
            
            try { sleep(10); } catch (Exception ignore) {}
        }
        
        SwingUtilities.invokeLater(
            new Thread(new Runnable() { 
                @Override
                public void run() {
                    dlg.close();
                }
            }));
        
        thread = null;
    }
}
