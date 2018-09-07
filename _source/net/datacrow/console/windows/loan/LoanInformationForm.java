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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcButton;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcComboBox;
import net.datacrow.console.components.DcDateField;
import net.datacrow.console.components.DcObjectComboBox;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class LoanInformationForm extends DcFrame implements ActionListener {
    
    private LoanInformationPanel panelLoans;
    private LoanFilterPanel panelFilter;
    
	public LoanInformationForm() {
		this(null);
	}
	
    public LoanInformationForm(DcObject person) {
        super(DcResources.getText("lblLoanAdministration"), IconLibrary._icoLoan);
        build(person);
        pack();
        
        setSize(DcSettings.getDimension(DcRepository.Settings.stLoanAdminFormSize));
        
        setCenteredLocation();
        setHelpIndex("dc.loanadmin");
        
        refresh();
    }
    
    public void refresh() {
        search();
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stLoanAdminFormSize, getSize());
        
        if (panelLoans != null) {
            panelLoans.cancel();
            panelLoans.clear();
        }
        
        panelLoans = null;
        panelFilter = null;
        
        super.close();
    }
    
    private void build(DcObject person) {
        panelFilter = new LoanFilterPanel(this);
        panelLoans = new LoanInformationPanel(person);
        
    	getContentPane().setLayout(Layout.getGBL());

    	getContentPane().add(panelFilter,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelLoans,        Layout.getGBC( 0, 1, 1, 1, 50.0, 50.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonOk.setActionCommand("ok");
        buttonOk.addActionListener(this);
        
        JPanel pActions = new JPanel();
        pActions.add(buttonOk);
        
        getContentPane().add(pActions,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
    }
    
    private void search() {
        panelFilter.allowActions(false);
        
        panelLoans.setFilter(panelFilter.getLoanFilter());
        panelLoans.load();
    }
    
    public void stop() {
        panelFilter.allowActions(true);
        panelLoans.cancel();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("ok")) {
            close();
        } else if (ae.getActionCommand().equals("search")) {
            search();
        } else if (ae.getActionCommand().equals("cancel")) {
            stop();
        }
    }
    
    private class LoanFilterPanel extends JPanel {
        
        private DcObjectComboBox cbPersons = ComponentFactory.getObjectCombo(DcModules._CONTACTPERSON);
        private DcComboBox cbModules = ComponentFactory.getComboBox();
        
        private DcDateField dtDueFrom = ComponentFactory.getDateField();
        private DcDateField dtDueTo = ComponentFactory.getDateField();

        private DcDateField dtStartFrom = ComponentFactory.getDateField();
        private DcDateField dtStartTo = ComponentFactory.getDateField();

        private DcComboBox cbLoans = ComponentFactory.getComboBox(); 
        
        private DcCheckBox cbOnlyTooLate = ComponentFactory.getCheckBox(""); 
        
        private DcButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        private DcButton btSearch = ComponentFactory.getButton(DcResources.getText("lblSearch"));
                
        public LoanFilterPanel(LoanInformationForm parent) {
            build(parent);
        }
        
        protected LoanFilter getLoanFilter() {
            LoanFilter lf = new LoanFilter();
            Calendar cal = Calendar.getInstance();
            
            Date dueDateFrom = (Date) dtDueFrom.getValue();
            if (dueDateFrom != null) {
                cal.setTime(dueDateFrom);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                lf.setDueDateFrom(cal.getTime());
            }

            Date dueDateTo = (Date) dtDueTo.getValue();
            if (dueDateTo != null) {
                cal.setTime(dueDateTo);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
                lf.setDueDateTo(cal.getTime());
            }

            Date startDateFrom = (Date) dtStartFrom.getValue();
            Date startDateTo = (Date) dtStartTo.getValue();
            if (startDateFrom != null) {
                cal.setTime(startDateFrom);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                lf.setStartDateFrom(cal.getTime());
            }

            if (startDateTo != null) {
                cal.setTime(startDateTo);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
                startDateTo = cal.getTime();
                lf.setStartDateTo(cal.getTime());
            }
            
            lf.setLoanType(cbLoans.getSelectedIndex());
            
            if (cbPersons.getSelectedItem() instanceof DcObject)
                lf.setPerson((DcObject) cbPersons.getSelectedItem());
            
            if (cbModules.getSelectedItem() instanceof DcModule)
                lf.setModule((DcModule) cbModules.getSelectedItem());
            
            lf.setOnlyOverdue(cbOnlyTooLate.isSelected());
            
            return lf;
        }
        
        private void build(LoanInformationForm parent) {
            DcModule module = DcModules.get(DcModules._SOFTWARE);
            cbModules.addItem(" ");
            for (DcModule m : DcModules.getModules()) {
                if (m.canBeLend() && !m.isAbstract())
                    cbModules.addItem(m);
            }
            
            cbLoans.addItem(DcResources.getText("lblOnlyCurrentLoans"));
            cbLoans.addItem(DcResources.getText("lblOnlyHistoricalLoans"));
            cbLoans.addItem(DcResources.getText("lblAllLoans"));
            cbLoans.setSelectedIndex(0);
    
            setLayout(Layout.getGBL());
            
            JPanel left = new JPanel();
            left.setLayout(Layout.getGBL());
            
            left.add(ComponentFactory.getLabel(DcResources.getText("lblLoanType")),  
                    Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 0), 0, 0));
            left.add(cbLoans,  
                    Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));       
            
            left.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_LENDBY).getLabel()),  
                    Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 0), 0, 0));
            left.add(cbPersons,   
                    Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));
            
            left.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_MODULE).getLabel()),  
                    Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 0), 0, 0));
            left.add(cbModules,  
                    Layout.getGBC( 1, 2, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0)); 

            left.add(ComponentFactory.getLabel(DcResources.getText("lblOnlyOverdueLoans")),  
                    Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 0), 0, 0));
            left.add(cbOnlyTooLate,  
                    Layout.getGBC( 1, 3, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0)); 

            JPanel right = new JPanel();
            right.setLayout(Layout.getGBL());
            
            right.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_LOANDUEDATE).getLabel() + " " +
                    DcResources.getText("lblFrom")),  
                    Layout.getGBC( 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 20, 5, 0), 0, 0));
            right.add(dtDueFrom,  
                    Layout.getGBC( 3, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));        
            right.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_LOANDUEDATE).getLabel() + " " +
                    DcResources.getText("lblTo")),  
                    Layout.getGBC( 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 20, 5, 0), 0, 0));        
            right.add(dtDueTo,  
                    Layout.getGBC( 3, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));
            
            right.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_LOANSTARTDATE).getLabel() + " " +
                    DcResources.getText("lblFrom")),  
                    Layout.getGBC( 2, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 20, 5, 0), 0, 0));
            right.add(dtStartFrom,  
                    Layout.getGBC( 3, 2, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));        
            right.add(ComponentFactory.getLabel(module.getField(DcObject._SYS_LOANSTARTDATE).getLabel() + " " +
                    DcResources.getText("lblTo")),  
                    Layout.getGBC( 2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(5, 20, 5, 0), 0, 0));        
            right.add(dtStartTo,  
                    Layout.getGBC( 3, 3, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0), 0, 0));            

            
            btCancel.setActionCommand("cancel");
            btCancel.addActionListener(parent);
            btCancel.setEnabled(false);
            
            btSearch.setActionCommand("search");
            btSearch.addActionListener(parent);
            
            JPanel panelActions = new JPanel();
            panelActions.add(btCancel);
            panelActions.add(btSearch);
            
            
            add(left, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));  
            add(right, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));              
            
            add(panelActions, Layout.getGBC(0, 1, 2, 1, 1.0, 1.0,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                    new Insets(10, 5, 5, 0), 0, 0));  
        }
        
        protected void allowActions(boolean b) {
            cbLoans.setEnabled(b);
            cbModules.setEnabled(b);
            cbOnlyTooLate.setEnabled(b);
            cbPersons.setEnabled(b);
            dtDueFrom.setEnabled(b);
            dtDueTo.setEnabled(b);
            dtStartFrom.setEnabled(b);
            dtStartTo.setEnabled(b);
            
            btSearch.setEnabled(b);
            btCancel.setEnabled(!b);
        }
    }
}

