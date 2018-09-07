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
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.console.components.DcPictureField;
import net.datacrow.console.components.DcTextPane;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.settings.DcSettings;

public class DcCardObjectListElement extends DcObjectListElement {

    private static final Dimension size = new Dimension(250, 250);
    private static final Dimension dimTxt = new Dimension(250, 45);
    private static final Dimension dimPicLbl = new Dimension(250, 200);

    private DcTextPane fldTitle;
    private DcPictureField fldPicture;
    
    private boolean build = false;

    public DcCardObjectListElement(int module) {
        super(module);
        
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }

    @Override
    public void update(DcObject dco) {
        if (this.dco == null || this.dco.isNew()) {
            this.dco = dco;
            clear();
        } else {
            clear();
        }
    }    
    
    private String getDescription() {
        int[] fields = (int[]) dco.getModule().getSetting(DcRepository.ModuleSettings.stCardViewItemDescription);
        if (fields != null && fields.length > 0) {
            StringBuilder sb = new StringBuilder();
            String disp;
            for (int field :  fields) {
                disp = dco.getDisplayString(field);
                if (disp.length() > 0) {
                    if (sb.length() > 0)
                        sb.append(" / ");
                    sb.append(disp);
                }
            }
            
            if (sb.length() == 0) {
                return dco.getName();
            } else {
                return sb.toString();
            }
        } 
        return dco.getName();
    }

    public boolean isBuild() {
        return build;
    }
    
    @Override
    public Collection<Picture> getPictures() {
    	Collection<Picture> pictures = new ArrayList<Picture>();
    	
    	int[] fields = dco.getModule().getSettings().getIntArray(DcRepository.ModuleSettings.stCardViewPictureOrder);
    	
    	if (fields == null || fields.length == 0) {
            for (DcField field : dco.getFields()) {
                if (field.getValueType() == DcRepository.ValueTypes._PICTURE)
                    fields = new int[] {field.getIndex()};
            }
    	}
    	
    	dco.getModule().getSettings().set(DcRepository.ModuleSettings.stCardViewPictureOrder, fields);
    	
    	for (int field : fields)
    		pictures.add((Picture) dco.getValue(field));

		return pictures;
    }
    
    @Override
    public void setBackground(Color color) {
        if (fldTitle != null)
            fldTitle.setBackground(color);
    }    
    
    private void addPicture(Collection<Picture> pictures) {
        DcImageIcon scaledImage;
        DcImageIcon image;
        for (Picture p : pictures) {
            
            if (p == null || !p.hasImage()) continue;
                
            scaledImage = p.getScaledPicture();
            image = (DcImageIcon) p.getValue(Picture._D_IMAGE);
            
            if (scaledImage != null) { 
                fldPicture.setValue(scaledImage);
                fldPicture.setScaled(false);
                break;
            } else if (image != null) {
                fldPicture.setValue(image);
                fldPicture.setScaled(true);
                break;
            }                
        }

        fldPicture.setPreferredSize(dimPicLbl);
        fldPicture.setMinimumSize(dimPicLbl);
        fldPicture.setMaximumSize(dimPicLbl);
        add(fldPicture);
    }
    
    
    @Override
    public void build() {
        build = true;
        
        fldPicture = DcObjectListComponents.getPictureField();
        fldTitle = DcObjectListComponents.getTextPane();
        fldTitle.setText(getDescription());
        fldTitle.setPreferredSize(dimTxt);
        fldTitle.setMinimumSize(dimTxt);
        fldTitle.setMaximumSize(dimTxt);
        
        addPicture(getPictures());
        add(fldTitle);
          
        super.setBackground(DcSettings.getColor(DcRepository.Settings.stCardViewBackgroundColor));
    }
    
    @Override
    public void clear() {
        super.clear();
        
        removeAll();
        
        if (fldPicture != null) {
            fldPicture.clear();
            DcObjectListComponents.release(fldPicture);
            DcObjectListComponents.release(fldTitle);
        }
            
        fldPicture = null;
        fldTitle = null;
        build = false;
    }
    
	@Override
	protected void finalize() throws Throwable {
		clear();
	}
}