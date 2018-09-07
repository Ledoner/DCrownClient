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

package net.datacrow.console.windows.datepicker;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcLabel;

public class DayLabel extends DcLabel implements MouseInputListener, MouseMotionListener {
    
    private DatePickerDialog parent;
    private Border oldBorder;
    
    public DayLabel(DatePickerDialog parent, int day) {
        super("" + day);
        this.parent = parent;
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(ComponentFactory.getSystemFont());
        this.addMouseListener(this);
    }

    public void setCurrentDayStyle() {
        setFont(ComponentFactory.getSystemFont());
        setForeground(Color.RED);
    }

    public void setSelectedDayStyle() {
        setFont(ComponentFactory.getSystemFont());
        setForeground(Color.BLUE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void setWeekendStyle() {
        setFont(ComponentFactory.getSystemFont());
        setForeground(Color.GRAY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        parent.dayPicked(Integer.parseInt(getText()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        oldBorder = this.getBorder();
        Border b = BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
        b = BorderFactory.createEtchedBorder();
        this.setBorder(b);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(oldBorder);
    }

    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
}
