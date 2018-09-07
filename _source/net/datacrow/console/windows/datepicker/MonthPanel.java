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
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.datacrow.console.ComponentFactory;

public class MonthPanel extends JPanel {
    
    private DatePickerDialog parent;

    public MonthPanel(DatePickerDialog parent, Calendar c) {
        this.parent = parent;
        
        GridLayout g = new GridLayout();
        g.setColumns(7);
        g.setRows(0);
        setLayout(g);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, c.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
        cal.set(Calendar.HOUR, 0);
        for (int i = 0; i < 7; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i + 1);
            
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            String s = sdf.format(cal.getTime());
            JLabel lblWeekDay = ComponentFactory.getLabel(s);
            lblWeekDay.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (i == 0)
                lblWeekDay.setForeground(Color.RED);
            else if (i == 6)
                lblWeekDay.setForeground(Color.gray);
            
            this.add(lblWeekDay);
        }
        
        setDaysOfMonth(c);
    }

    public void clear() {
        parent = null;
        ComponentFactory.clean(this);
    }
    
    private void setDaysOfMonth(Calendar c) {
        Calendar curr = new GregorianCalendar();
        int currdate = curr.get(Calendar.DAY_OF_MONTH);
        int currmon = curr.get(Calendar.MONTH);
        int curryear = curr.get(Calendar.YEAR);

        int seldate = -1;
        int selmon = -1;
        int selyear = -1;
        
        Calendar cal = parent.getDate();
        if (cal != null) {
            seldate = cal.get(Calendar.DAY_OF_MONTH);
            selmon = cal.get(Calendar.MONTH);
            selyear = cal.get(Calendar.YEAR);
        }

        int date = c.get(Calendar.DAY_OF_MONTH);
        int mon = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DAY_OF_WEEK);
        int start = (7 - (date - day) % 7) % 7;
        int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        JLabel label;
        for (int i = 0; i < start; i++) {
            label = new JLabel("");
            add(label);
        }
        
        int pos = start;
        DayLabel lbl;
        for (int i = 1; i <= days; i++) {
            pos++;
            lbl = new DayLabel(parent, i);
            if (seldate == i && selmon == mon && selyear == year)
                lbl.setSelectedDayStyle();
            if (currdate == i && currmon == mon && curryear == year)
                lbl.setCurrentDayStyle();
            if (pos % 7 == 0 || pos % 7 == 1)
                lbl.setWeekendStyle();
            add(lbl);

        }
    }
}