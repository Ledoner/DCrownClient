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

import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.helpers.MusicTrack;

public class DcMusicTrackListElement extends DcObjectListElement {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private JPanel panelInfo;
    
    public DcMusicTrackListElement(int module) {
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

        panelInfo = getPanel();
        JLabel labelTrack = getLabel(MusicTrack._F_TRACKNUMBER, false, 30);
        JLabel labelTitle = getLabel(MusicTrack._A_TITLE, false, 300);
        JLabel labelPlaylength = getLabel(MusicTrack._J_PLAYLENGTH, false, 100);
        
        panelInfo.add(labelTrack);
        panelInfo.add(labelTitle);
        panelInfo.add(labelPlaylength);
        
        panelInfo.setPreferredSize(new Dimension(50000, fieldHeight));
        add(panelInfo);
    } 
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		panelInfo = null;
	}
}
