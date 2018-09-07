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

import net.datacrow.core.console.ISimpleItemView;
import net.datacrow.core.objects.DcObject;
import net.datacrow.util.task.WindowUpdater;

public class DcMinimalisticItemForm extends ItemForm {
    
    private ISimpleItemView parent;
    
    public DcMinimalisticItemForm(boolean readonly, boolean update, DcObject o, ISimpleItemView parent) {
        super(readonly, update, o, true);
        this.parent = parent;
    }
    
    @Override
    public void notifyTaskCompleted(boolean success, String ID) {
        if (success && parent != null)
            SwingUtilities.invokeLater(new Thread(new WindowUpdater(parent)));
        
        this.close(true);
    }
    
    @Override
    public void apply() {
        if (parent.getParentID() != null && dco.getParentReferenceFieldIndex() > 0)
            dco.setValue(dco.getParentReferenceFieldIndex(), parent.getParentID());
        
        super.apply();
    }
}
