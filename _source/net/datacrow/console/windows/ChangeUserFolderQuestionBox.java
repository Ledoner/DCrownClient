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

package net.datacrow.console.windows;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.windows.messageboxes.QuestionBox;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class ChangeUserFolderQuestionBox extends QuestionBox {

    public ChangeUserFolderQuestionBox() {
        super(DcResources.getText("msgChangeUserDir", DcConfig.getInstance().getDataDir()));
        
        DcCheckBox cbAskAgain = ComponentFactory.getCheckBox(DcResources.getText("msgDontAskAgainUserFolder"));
        cbAskAgain.addActionListener(this);
        cbAskAgain.setActionCommand("askagain");
        cbAskAgain.setSelected(DcSettings.getBoolean(DcRepository.Settings.stDoNotAskAgainChangeUserDir));
        
        this.getContentPane().add(cbAskAgain, Layout.getGBC( 0, 2, 2, 1, 0.0, 0.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        
        if (e.getActionCommand().equals("askagain")) {
            JCheckBox cb = (JCheckBox) e.getSource();
            DcSettings.set(DcRepository.Settings.stDoNotAskAgainChangeUserDir, cb.isSelected());
        }
    }    
}