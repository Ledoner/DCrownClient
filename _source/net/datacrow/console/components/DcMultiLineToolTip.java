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

package net.datacrow.console.components;

import java.awt.Graphics;

import javax.swing.JToolTip;

import net.datacrow.console.GUI;

public class DcMultiLineToolTip extends JToolTip {

    protected int columns = 0;
    protected int fixedwidth = 0;
    
    public DcMultiLineToolTip() {
        updateUI();
    }
    
    @Override
    public void updateUI() {
        setUI(DcMultiLineToolTipUI.createUI(this));
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
        this.fixedwidth = 0;
    }
    
    public int getColumns() {
        return columns;
    }
    
    public void setFixedWidth(int width) {
        this.fixedwidth = width;
        this.columns = 0;
    }
    
    public int getFixedWidth() {
        return fixedwidth;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    }       
}

