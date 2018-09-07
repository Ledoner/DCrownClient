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

import javax.swing.JPanel;

import net.datacrow.console.components.DcLabel;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class DcTemplateListElement extends DcObjectListElement {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private JPanel panelInfo;
    
    public DcTemplateListElement(int module) {
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
    public void build() {
        setLayout(layout);
        
        DcTemplate template = (DcTemplate) dco;
        
        String label = dco.getDisplayString(DcTemplate._SYS_TEMPLATENAME); 
        if (template.isDefault()) 
            label += " (" + DcResources.getText("lblDefault") + ")";
        
        DcLabel lbl = new DcLabel(label);
        lbl.setPreferredSize(new Dimension(800, fieldHeight));
        lbl.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        
        panelInfo = getPanel();
        panelInfo.add(lbl);
        panelInfo.setPreferredSize(new Dimension(800, fieldHeight));
        add(panelInfo);
    } 
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		panelInfo = null;
	}
}