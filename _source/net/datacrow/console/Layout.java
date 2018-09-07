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

package net.datacrow.console;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Holds static references to the GridBagConstraints and the GridBagLayout.
 * These should always be used when adding components.
 * 
 * @see GridBagConstraints
 * @see GridBagLayout
 * 
 * @author Robert Jan van der Waals
 */
public final class Layout {
    
    private static final GridBagConstraints gbc = new GridBagConstraints();
    private static final GridBagLayout gbl = new GridBagLayout();
    
    public static GridBagLayout getGBL() {
        return gbl;
    }
    
    /**
     * Sets the Gridbag Constraints on the saved instance
     */
    public static GridBagConstraints getGBC (   int x,          
                                                int y,          
                                                int width,      
                                                int height,      
                                                double xWeight,
                                                double yWeigth,
                                                int gbcAnchor,
                                                int gbcStretch,
                                                Insets insets,
                                                int xPad,
                                                int yPad) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = xWeight;
        gbc.weighty = yWeigth;
        gbc.anchor = gbcAnchor;
        gbc.fill = gbcStretch;
        gbc.insets = insets;
        gbc.ipadx = xPad;
        gbc.ipady = yPad;
        return gbc;
    }
}
