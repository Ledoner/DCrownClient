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

import java.util.Calendar;


public class TimeFieldTableCellRenderer extends DcTableCellRenderer {
    
    private static final TimeFieldTableCellRenderer instance = new TimeFieldTableCellRenderer();

    private TimeFieldTableCellRenderer() {
        setOpaque(true);

        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    
    public static TimeFieldTableCellRenderer getInstance() {
        return instance;
    }
    
    @Override
    public void setText(String str) {
        int minutes = 0;
        int seconds = 0;
        int hours = 0;
        int days = 0;
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        if (str != null && str.length() > 0) {
        	int value = Integer.parseInt(str);
            cal.set(Calendar.SECOND, value);
            minutes = cal.get(Calendar.MINUTE);
            seconds = cal.get(Calendar.SECOND);
            hours = cal.get(Calendar.HOUR_OF_DAY);
            days = cal.get(Calendar.DATE) - 1;
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append(hours + (days > 0 ? days * 24 : 0));
        sb.append(":");
        sb.append(getDoubleDigitString(minutes));
        sb.append(":");
        sb.append(getDoubleDigitString(seconds));
        
        super.setText(sb.toString());
    } 
    
    private String getDoubleDigitString(int value) {
        if (value == 0) {
            return "00";
        } else if (value > 0 && value < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append(0);
            sb.append(value);
            return sb.toString();
        } else {
        	return String.valueOf(value);
        }
    }
}
