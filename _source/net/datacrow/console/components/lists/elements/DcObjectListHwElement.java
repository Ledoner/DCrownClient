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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLabel;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.settings.DcSettings;

public abstract class DcObjectListHwElement extends DcObjectListElement {
    
    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
    
    protected static final int label1Length = 110;
    protected static final int label2Length = 110;
    protected static final int field1Length = 290;
    protected static final int field2Length = 170;
    
    protected static final Dimension dimDescriptionFld = new Dimension(680, 80);
    
    public DcObjectListHwElement(int module) {
        super(module);
        setLayout(layout);
    }
    
    @Override
    public void update(DcObject dco) {
        this.dco.copy(dco, true, true);
        update();
    }  
    
    @Override
    public void update() {
        removeAll();
        build();
        revalidate();
    }  
    
    protected Color lighter(Color color) {
        int red = color.getRed() > 10 ? color.getRed() - 10 : color.getRed();
        int green = color.getGreen() > 10 ? color.getGreen() - 10 : color.getGreen();
        int blue = color.getBlue() > 10 ? color.getBlue() - 10 : color.getBlue();
        
        red = red < 0 ? 0 : red > 255 ? 255 : red;
        green = green < 0 ? 0 : green > 255 ? 255 : green;
        blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
        
        return new Color(red, green, blue);
    }
    
    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        return panel;
    }
    
    @Override
    public abstract void build();
    
    protected void addComponent(JComponent component, int x, int y) {
        Insets insets = y == 0 && x == 0 ? 
                            new Insets(5, 5, 0, 0) : y == 0 ?  
                                    new Insets(5, 0, 0, 0) : x == 0 ? 
                                            new Insets(0, 5, 0, 0) :
                                                new Insets(0, 0, 0, 0);
        add(component, Layout.getGBC( x, y, 1, 1, 1.0, 1.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));          
    }
    
    protected DcLabel getRatingValueLabel() {
        DcLabel labelRating = ComponentFactory.getLabel(dco.getDisplayString(DcMediaObject._E_RATING));
        labelRating.setPreferredSize(new Dimension(55, fieldHeight));
        return labelRating;
    }
    
    protected DcLabel getLabel(String text, boolean label, int width) {
        DcLabel lbl = new DcLabel();
        
        if (label) {
            Font font = DcSettings.getFont(DcRepository.Settings.stSystemFontBold);
            lbl.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        } else {
            lbl.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        }
        
        lbl.setText(text);
        lbl.setPreferredSize(new Dimension(width, fieldHeight));
        return lbl;
    }
    
    @Override
    protected DcLabel getLabel(int field, boolean label, int width) {
        DcLabel lbl = new DcLabel();
        if (label) {
            Font font = DcSettings.getFont(DcRepository.Settings.stSystemFontBold);
            lbl.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
            lbl.setText(dco.getLabel(field));
        } else {
            lbl.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
            lbl.setText(dco.getDisplayString(field));
        }
        
        lbl.setPreferredSize(new Dimension(width, fieldHeight));
        
        return lbl;
    }    
    
    protected JPanel getPicturePanel(Collection<Picture> pictures) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200,150));
        panel.setLayout(Layout.getGBL());
        
        ImageIcon image;
        DcPictureField label;
        for (Picture picture : pictures) {
            if (picture != null && !picture.isDeleted()) {
                image = (ImageIcon) picture.getValue(Picture._D_IMAGE);
                if (image == null) {
                	picture.loadImage(false);
                	image = (ImageIcon) picture.getValue(Picture._D_IMAGE);
                }
                
                if (image == null) continue;
                
                label = ComponentFactory.getPictureField(true, false);
                label.setValue(image);
                panel.add(label, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                          new Insets(0, 0, 0, 0), 0, 0));
                break;
            }
        }
        
        return panel;
    }

    protected String getShortDescription(String description) {
        int max = 500;
        String s = description;
        
        if (description == null) {
            s = "";
        } else if (description.length() > max) {
            s = s.substring(0, max);
            int index = description.indexOf(" ", max);
            if (index > -1) {
                String remainder = description.substring(max, index);
                s += remainder + ".....";
            }
        }
        
        s = s.replaceAll("\r", "");
        s = s.replaceAll("\n", "");
        
        return s;
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        for (int i = 0; i < getComponents().length; i++) {
            getComponents()[i].setBackground(color);
        }
    }    

    @Override
    public void setFont(Font font) {
        Font fontNormal = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);
        Font fontSystem = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);        
        
        super.setFont(fontNormal);
        Component component;
        Component subComponent;
        for (int i = 0; i < getComponents().length; i++) {
            component = getComponents()[i];
            if (component instanceof JPanel) {
                
                for (int j = 0; j < ((JPanel) component).getComponents().length; j++) {
                    subComponent = ((JPanel) component).getComponents()[j];
                    if (subComponent instanceof JLabel) 
                        subComponent.setFont(fontSystem);
                    else
                        subComponent.setFont(fontNormal);
                }
            }
        }
    }     
}
