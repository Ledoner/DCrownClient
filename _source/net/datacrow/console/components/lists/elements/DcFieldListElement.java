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

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.objects.DcField;

public class DcFieldListElement extends DcListElement {
    
    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private static final Dimension dim = new Dimension(360, 30);
    
    private DcField field;
    
    public DcFieldListElement(DcField field) {
        this.field = field;
        
        setPreferredSize(dim);
        setMinimumSize(dim);
        
        build();
    }

    public DcField getField() {
        return field;
    }
    
    @Override
    public void build() {
        
        if (field == null)
            return;
        
        setLayout(layout);
        JLabel labelField = ComponentFactory.getLabel(field.getLabel());
        labelField.setPreferredSize(dim);
        add(labelField);
    }

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		field = null;
	}

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DcFieldListElement ? 
                ((DcFieldListElement) obj).getField().equals(field) : false;
    }
}
