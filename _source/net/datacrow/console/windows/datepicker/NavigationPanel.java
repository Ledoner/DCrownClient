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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcResources;

public class NavigationPanel extends JPanel implements ActionListener {
    
    private DatePickerDialog parent;

    private JComboBox monthBox;
    private JComboBox yearBox;
    
    private static String[] months;
    private static Integer[] years;
    
    static {
        SimpleDateFormat mf = new SimpleDateFormat("MMMMM");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        months = new String[12];
        for(int i = 0; i < 12; i++) {
            cal.set(Calendar.MONTH, i);
            months[i]= mf.format(cal.getTime());
        }
    }

    public NavigationPanel(DatePickerDialog parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        
        JButton btPreviousYr =  ComponentFactory.getIconButton(IconLibrary._icoArrowUp);
        btPreviousYr.setToolTipText(DcResources.getText("lblPreviousYear"));
        btPreviousYr.addActionListener(this);
        btPreviousYr.setActionCommand("prevYear");
        
        JButton btPreviousMt = ComponentFactory.getIconButton(IconLibrary._icoArrowUp);
        btPreviousMt.setToolTipText(DcResources.getText("lblPreviousMonth"));
        btPreviousMt.addActionListener(this);
        btPreviousMt.setActionCommand("prevMonth");

        JButton btNextMt = ComponentFactory.getIconButton(IconLibrary._icoArrowDown);
        btNextMt.setToolTipText(DcResources.getText("lblNextMonth"));
        btNextMt.addActionListener(this);
        btNextMt.setActionCommand("nextMonth");

        JButton btNextYr = ComponentFactory.getIconButton(IconLibrary._icoArrowDown);
        btNextYr.setToolTipText(DcResources.getText("lblNextYear"));
        btNextYr.addActionListener(this);
        btNextYr.setActionCommand("nextYear");

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(btPreviousMt);
        box.add(Box.createHorizontalStrut(3));
        box.add(btNextMt);
        add(box, BorderLayout.WEST);
        
        box = new Box(BoxLayout.X_AXIS);
        box.add(btPreviousYr);
        box.add(Box.createHorizontalStrut(3));
        box.add(btNextYr);        
        add(box, BorderLayout.EAST);
        
        monthBox = ComponentFactory.getComboBox();
        monthBox.addActionListener(this);
        monthBox.setActionCommand("monthChanged");
        
        yearBox = ComponentFactory.getComboBox();
        yearBox.addActionListener(this);
        yearBox.setActionCommand("yearChanged");
        
        box = new Box(BoxLayout.X_AXIS);
        box.add(monthBox);
        box.add(yearBox);
        add(box,BorderLayout.CENTER);
        
        setCurrentMonth(parent.getCalendar());
    }

    public void setCurrentMonth(Calendar c) {
        setMonthComboBox(c);
        setYearComboBox(c);
    }
    
    private void setMonthComboBox(Calendar c) {
        monthBox.setModel(new DefaultComboBoxModel(months));
        monthBox.setSelectedIndex(c.get(Calendar.MONTH));
    }
    
    private void setYearComboBox(Calendar c) {
        int y = c.get(Calendar.YEAR); 
        years = new Integer[101];
        for(int i = y - 50, j = 0; i <= y+50; i++, j++) {
            years[j] = i;
        }
        
        yearBox.setModel(new DefaultComboBoxModel(years));
        yearBox.setSelectedItem(years[50]);
    }

    public void clear() {
        parent = null;
        monthBox = null;
        yearBox = null;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Calendar c = new GregorianCalendar();
        
        c.setTime(parent.getCalendar().getTime());
        
        if (e.getSource() instanceof JButton) {
            if (e.getActionCommand().equals("prevMonth"))
                c.add(Calendar.MONTH, -1);
            if (e.getActionCommand().equals("nextMonth"))
                c.add(Calendar.MONTH, 1);
            if (e.getActionCommand().equals("prevYear"))
                c.add(Calendar.YEAR, -1);
            if (e.getActionCommand().equals("nextYear"))
                c.add(Calendar.YEAR, 1);
            
            parent.updateScreen(c);
        } else {
            if (e.getActionCommand().equals("monthChanged")) {
                JComboBox cb = (JComboBox)src;
                c.set(Calendar.MONTH, cb.getSelectedIndex());
            }
    
            if (e.getActionCommand().equals("yearChanged")) {
                JComboBox cb = (JComboBox)src;
                c.set(Calendar.YEAR, years[cb.getSelectedIndex()].intValue());
                setYearComboBox(c);
            }
            parent.setMonthPanel(c);
        }
    }
}
