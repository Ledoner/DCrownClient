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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.objects.Picture;

public class DcPropertyListElement extends DcObjectListElement {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private JPanel panelInfo;

    public DcPropertyListElement(int module) {
        super(module);
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (panelInfo != null)
            panelInfo.setBackground(color);
    }     
    
    @Override
    public Collection<Picture> getPictures() {
        return new ArrayList<Picture>();
    }
    
    @Override
    public void setDcObject(DcObject dco) {
        super.setDcObject(dco);
        build();
    }
    
    @Override
    public int[] getFields() {
    	return new int[] {DcObject._ID, DcProperty._A_NAME, DcProperty._B_ICON};
    }

    @Override
    public void build() {
        setLayout(layout);
        panelInfo = getPanel();
        JLabel label = getLabel(DcProperty._A_NAME, false, 800);

        if (    dco.getValue(DcProperty._B_ICON) != null &&
                dco.getValue(DcProperty._B_ICON).toString().length() > 10)
            label.setIcon(dco.getIcon());
        
        panelInfo.add(label);
        panelInfo.setPreferredSize(new Dimension(800, fieldHeight));
        add(panelInfo);
    } 
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		panelInfo = null;
	}
}
