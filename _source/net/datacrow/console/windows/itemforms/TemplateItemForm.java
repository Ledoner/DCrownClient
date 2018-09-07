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

package net.datacrow.console.windows.itemforms;

import javax.swing.SwingUtilities;

import net.datacrow.core.objects.DcObject;
import net.datacrow.util.task.WindowUpdater;

public class TemplateItemForm extends ItemForm {
    
    private DcMinimalisticItemView parent;
    
    public TemplateItemForm(boolean update, DcObject o, DcMinimalisticItemView parent) {
        super(false, update, o, true);
        this.parent = parent;
    }
    
    @Override
    public void notifyTaskCompleted(boolean success, String ID) {
        if (success)
            SwingUtilities.invokeLater(new Thread(new WindowUpdater(parent)));  
        
        super.notifyTaskCompleted(success, ID);
    }
}
