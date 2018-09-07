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

package net.datacrow.console.components.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.border.Border;

import net.datacrow.console.components.lists.elements.DcObjectListElement;
import net.datacrow.core.DcRepository;
import net.datacrow.settings.DcSettings;

public class DcListRenderer extends DefaultListCellRenderer  {

    private boolean evenOddColors;
    private static final Border selectedBorder = BorderFactory.createLineBorder(Color.BLACK);
    private static final Border normalBorder = BorderFactory.createLineBorder(Color.WHITE);
    
    public DcListRenderer() {}

    public DcListRenderer(boolean evenOddColors) {
        this.evenOddColors = evenOddColors;
    }

    public void setEventOddColors(boolean b) {
        evenOddColors = b;
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = (Component) value;
        component.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        setElementColor(isSelected, component, index);
        return component;
    }

    protected void setElementColor(boolean isSelected, Component component, int index) {
        if (evenOddColors) {
            Color colorOddRow = DcSettings.getColor(DcRepository.Settings.stOddRowColor);
            Color colorEvenRow = DcSettings.getColor(DcRepository.Settings.stEvenRowColor);
            Color colorRowSelection = DcSettings.getColor(DcRepository.Settings.stSelectionColor);
            
            if (!isSelected) {
                if ((index % 2) == 0)
                    component.setBackground(colorEvenRow);
                else
                    component.setBackground(colorOddRow);
            } else {
                component.setBackground(colorRowSelection);
            }
        } else {
            if (isSelected) {
                if (component instanceof DcObjectListElement) 
                    ((DcObjectListElement) component).setBorder(selectedBorder);
            } else if (component instanceof DcObjectListElement) { 
                ((DcObjectListElement) component).setBorder(normalBorder);
            }
        }
        
    }
    
    @Override
    public void repaint(final long tm, final int x, final int y, final int width, final int height) {}
    
    @Override
    public void repaint(final Rectangle r) {}
    
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final byte oldValue, final byte newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final char oldValue, final char newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final short oldValue, final short newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final int oldValue, final int newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final long oldValue, final long newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final float oldValue, final float newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final double oldValue, final double newValue) {}
    
    @Override
    public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {}    
}