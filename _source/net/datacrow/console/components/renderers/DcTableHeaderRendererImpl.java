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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcMultiLineToolTip;
import net.datacrow.core.DcRepository;
import net.datacrow.settings.DcSettings;

public abstract class DcTableHeaderRendererImpl extends DefaultTableCellRenderer {

    private final JButton button = ComponentFactory.getTableHeader("");
    
    protected DcTableHeaderRendererImpl() {}
    
    public void applySettings() {
        button.setBorder(BorderFactory.createLineBorder(DcSettings.getColor(DcRepository.Settings.stTableHeaderColor)));
        button.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontBold));
        button.setBackground(DcSettings.getColor(DcRepository.Settings.stTableHeaderColor));
    }
    
    public JButton getButton() {
        return button;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        button.setText(String.valueOf(value));
        button.setToolTipText(String.valueOf(value));
        return button;
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
}
