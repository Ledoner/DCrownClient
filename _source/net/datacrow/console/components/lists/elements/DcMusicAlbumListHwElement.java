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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.helpers.MusicAlbum;
import net.datacrow.core.objects.helpers.MusicTrack;

public class DcMusicAlbumListHwElement extends DcObjectListHwElement {

    private static final Long empty = Long.valueOf(0);
    
    private JTextArea tracksField;
    
    public DcMusicAlbumListHwElement(int module) {
        super(module);
    }
    
    @Override
    public Collection<Picture> getPictures() {
        Collection<Picture> pictures = new ArrayList<Picture>();
        pictures.add((Picture) dco.getValue(MusicAlbum._J_PICTUREFRONT));
        pictures.add((Picture) dco.getValue(MusicAlbum._K_PICTUREBACK));
        return pictures;
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (tracksField != null) {
            int red = color.getRed() - 20;
            int green = color.getGreen() - 20;
            int blue = color.getBlue() - 20;
            
            red = red < 0 ? 0 : red > 255 ? 255 : red;
            green = green < 0 ? 0 : green > 255 ? 255 : green;
            blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
            
            tracksField.setBackground(new Color(red, green, blue));
        }
    }    
    
    @Override
    public void build() {
        setLayout(Layout.getGBL());
        
        tracksField = getTracksField();
        JLabel titleLabel = getLabel(MusicAlbum._A_TITLE, true, label1Length);
        if (DcModules.getCurrent().isAbstract())
            titleLabel.setIcon(dco.getModule().getIcon16());

        addComponent(titleLabel, 0, 0);
        addComponent(getLabel(MusicAlbum._A_TITLE, false, field1Length), 1, 0);
        addComponent(getLabel(MusicAlbum._G_GENRES, true, label2Length), 2, 0);
        addComponent(getLabel(MusicAlbum._G_GENRES, false, field2Length), 3, 0);
        
        addComponent(getLabel(MusicAlbum._F_ARTISTS, true, label1Length), 0, 1);
        addComponent(getLabel(MusicAlbum._F_ARTISTS, false, field1Length), 1, 1);
        addComponent(getLabel(MusicAlbum._I_STORAGEMEDIUM, true, label2Length), 2, 1);
        addComponent(getLabel(MusicAlbum._I_STORAGEMEDIUM, false, label2Length), 3, 1);      
        
        add(tracksField, Layout.getGBC( 0, 2, 4, 1, 10.0, 10.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0), 0, 0));
        add(getPicturePanel(getPictures()), Layout.getGBC( 4, 0, 1, 3, 10.0, 10.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));        
    }   
    
    protected JTextArea getTracksField() {
        String description = ""; 
        int counter = 0;
        
        String title;
        Long i;
        String length;
        for (DcObject track : dco.getChildren()) {
            if (counter > 0) description += " / ";
            
            title = (String) track.getValue(MusicTrack._A_TITLE);
            title = title == null ? "" : title; 
            
            i = (Long) track.getValue(MusicTrack._J_PLAYLENGTH);
            length = i == null || i.equals(empty) ? "" : " " + track.getDisplayString(MusicTrack._J_PLAYLENGTH);
            
            if (track.getValue(MusicTrack._F_TRACKNUMBER) != null)
                description += track.getValue(MusicTrack._F_TRACKNUMBER) + " - ";
            
            description += title;
            description += length;
            counter++;
        }

        JTextArea textField = ComponentFactory.getTextArea();
        textField.setPreferredSize(new Dimension(680, 120));
        textField.setText(description);
        return textField;        
    }
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		tracksField = null;
	}
}
