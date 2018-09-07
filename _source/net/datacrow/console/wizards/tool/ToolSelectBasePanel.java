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

package net.datacrow.console.wizards.tool;

import javax.swing.JPanel;

import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;

public abstract class ToolSelectBasePanel extends JPanel implements IWizardPanel {

    private Tool tool;
    private Wizard wizard;
    
    public ToolSelectBasePanel(Wizard wizard) {
        this.wizard = wizard;
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
    public Tool getTool() {
        tool = tool == null ? new Tool() : tool;
        return tool;
    }
    
    public Wizard getWizard() {
        return wizard;
    }
    
    @Override
    public void onActivation() {}

    @Override
    public void onDeactivation() {}

    @Override
    public void destroy() {
        tool = null;
        wizard = null;
    }
}
