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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.helpers.Software;

public class DcSoftwareListHwElement extends DcObjectListHwElement {

    private JTextArea descriptionField;
    
    public DcSoftwareListHwElement(int module) {
        super(module);
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (descriptionField != null)
            descriptionField.setBackground(lighter(color));
    }     
    
    @Override
    public Collection<Picture> getPictures() {
        Collection<Picture> pictures = new ArrayList<Picture>();
        pictures.add((Picture) dco.getValue(Software._P_SCREENSHOTONE));
        pictures.add((Picture) dco.getValue(Software._Q_SCREENSHOTTWO));
        pictures.add((Picture) dco.getValue(Software._R_SCREENSHOTTHREE));
        pictures.add((Picture) dco.getValue(Software._M_PICTUREFRONT));
        return pictures;
    }

    @Override
    public void build() {
        setLayout(Layout.getGBL());

        JLabel label = getLabel(Software._K_CATEGORIES, false, field1Length);
        try {
            DcProperty category = (DcProperty) dco.getValue(Software._K_CATEGORIES);
            label.setIcon(category.getIcon());
        } catch (Exception exp) {}

        
        JLabel titleLabel = getLabel(Software._A_TITLE, true, label1Length);
        if (DcModules.getCurrent().isAbstract())
            titleLabel.setIcon(dco.getModule().getIcon16());
        
        addComponent(titleLabel, 0, 0);
        addComponent(getLabel(Software._A_TITLE, false, field1Length), 1, 0);
        
        addComponent(getLabel(Software._C_YEAR, true, label2Length), 2, 0);
        addComponent(getLabel(Software._C_YEAR, false, field2Length), 3, 0);
        
        addComponent(getLabel(Software._K_CATEGORIES, true, label1Length), 0, 1);
        addComponent(label, 1, 1);        
        addComponent(getLabel(Software._W_STORAGEMEDIUM, true, label2Length), 2, 1);
        addComponent(getLabel(Software._W_STORAGEMEDIUM, false, field2Length), 3, 1);     

        addComponent(getLabel(Software._H_PLATFORM, true, label1Length), 0, 2);
        addComponent(getLabel(Software._H_PLATFORM, false, field1Length), 1, 2);
        addComponent(getLabel(DcMediaObject._E_RATING, true, label2Length), 2, 2);
        addComponent(getRatingValueLabel(), 3, 2);
        
        descriptionField = ComponentFactory.getTextArea();
        descriptionField.setPreferredSize(dimDescriptionFld);
        descriptionField.setText(getShortDescription((String) dco.getValue(Software._B_DESCRIPTION)));
        add(descriptionField, Layout.getGBC( 0, 3, 4, 1, 10.0, 10.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0), 0, 0));
        add(getPicturePanel(getPictures()), Layout.getGBC( 4, 0, 1, 4, 10.0, 10.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
    } 
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		descriptionField = null;
	}
}
