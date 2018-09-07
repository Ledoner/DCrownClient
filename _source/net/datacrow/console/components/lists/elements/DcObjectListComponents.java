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

package net.datacrow.console.components.lists.elements;

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.console.components.DcTextPane;

public abstract class DcObjectListComponents {

    private static final int _MAX_ITEMS = 25;
    
    private static List<DcTextPane> textPanes = new ArrayList<DcTextPane>();
    private static List<DcPictureField> pictureFields = new ArrayList<DcPictureField>();
    
    public static DcTextPane getTextPane() {
        DcTextPane tp = textPanes.size() > 0 ? textPanes.remove(0) : ComponentFactory.getTextPane();
        tp.setFont(ComponentFactory.getStandardFont());
        return tp;
    }

    public static DcPictureField getPictureField() {
        return pictureFields.size() > 0 ? pictureFields.remove(0) : ComponentFactory.getPictureField(false, false);
    }
    
    public static void release(DcPictureField picField) {
        if (picField != null) {
            picField.clear();
            
            if (pictureFields.size() < _MAX_ITEMS)
                pictureFields.add(picField);
        }
    }

    public static void release(DcTextPane textPane) {
        if (textPane != null) {
            textPane.setText("");
            
            if (textPanes.size() < _MAX_ITEMS)
                textPanes.add(textPane);
        }
    }
}