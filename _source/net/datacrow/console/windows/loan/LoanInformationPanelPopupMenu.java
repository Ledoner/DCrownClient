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

package net.datacrow.console.windows.loan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class LoanInformationPanelPopupMenu extends DcPopupMenu implements ActionListener {

    private static Logger logger = Logger.getLogger(LoanInformationPanelPopupMenu.class.getName());
    
    private DcObject dco;
    private List<DcObject> items;
    
    public LoanInformationPanelPopupMenu(DcObject dco, List<DcObject> items) {
        
        this.dco = dco;
        this.items = items;
        
        JMenuItem menuOpen = ComponentFactory.getMenuItem(IconLibrary._icoOpen, DcResources.getText("lblOpenItem", dco.getModule().getObjectName()));
        JMenuItem menuEdit = ComponentFactory.getMenuItem(IconLibrary._icoOpen, DcResources.getText("lblEditItem", dco.getModule().getObjectName()));
        JMenuItem menuLoan = ComponentFactory.getMenuItem(IconLibrary._icoLoan, DcResources.getText("lblLoanAdministration"));
             
        menuLoan.setActionCommand("loan");
        menuOpen.setActionCommand("openItem");
        menuEdit.setActionCommand("editItem");
        
        menuOpen.addActionListener(this);
        menuEdit.addActionListener(this);
        menuLoan.addActionListener(this);
        
        this.add(menuLoan);
        this.addSeparator();
        this.add(menuOpen);

        Connector connector = DcConfig.getInstance().getConnector();
        SecuredUser su = connector.getUser();
        if (su.isAuthorized(dco.getModule())) {
            this.add(menuEdit);
            if (su.isEditingAllowed(dco.getModule()))
                this.add(menuEdit);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        dco.markAsUnchanged();
        dco.getModule();
        
        if (ae.getActionCommand().equals("openItem")) {
            ItemForm form = new ItemForm(true, true, dco, false);
            form.setVisible(true);
        } else if (ae.getActionCommand().equals("editItem")) {
            ItemForm form = new ItemForm(false, true, dco, false);
            form.setVisible(true);
        } else if (ae.getActionCommand().equals("loan")) {
            try {
                LoanForm form = new LoanForm(items);
                form.setVisible(true);
            } catch (Exception e) {
                logger.warn(e, e);
                GUI.getInstance().displayWarningMessage(e.getMessage());
            }
        }
    }
}
