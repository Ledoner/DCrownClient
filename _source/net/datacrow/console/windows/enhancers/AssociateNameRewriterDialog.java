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

package net.datacrow.console.windows.enhancers;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.enhancers.AssociateNameRewriter;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class AssociateNameRewriterDialog extends DcDialog implements ActionListener {

    private static Logger logger = Logger.getLogger(AssociateNameRewriterDialog.class.getName());
    
    private boolean canceled = false;
    
    private JProgressBar progressBar = new JProgressBar();
    
    private JButton buttonRun = ComponentFactory.getButton(DcResources.getText("lblRun"));
    private JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    private JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
    
    private JCheckBox checkEnabled = ComponentFactory.getCheckBox(DcResources.getText("lblEnabled"));
    private JComboBox cbFormat = ComponentFactory.getComboBox();
    private DcModule module = DcModules.getCurrent();

    public AssociateNameRewriterDialog() {
        super(GUI.getInstance().getMainFrame());

        buildDialog();

        setHelpIndex("dc.tools.associatenamerewriter");
        setTitle(DcResources.getText("lblAssociateNameRewriter"));
        
        setCenteredLocation();
        setModal(true);
    }

    private void cancel() {
        canceled = true;
    }
    
    @Override
    public void close() {
        checkEnabled = null;
        cbFormat = null;
        progressBar = null;
        module = null;
        
        super.close();
    }

    private AssociateNameRewriter getAssociateNameRewriter() throws Exception {
        return new AssociateNameRewriter(checkEnabled.isSelected(), cbFormat.getSelectedIndex());
    }
    
    private void save() {
        try {
            AssociateNameRewriter rewriter = getAssociateNameRewriter();
            module.removeEnhancers();
            DcField field = module.getField(rewriter.getField());
            if (field == null) {
            	GUI.getInstance().displayWarningMessage("msgCouldNotSaveAssociateNameRewriter");
            } else {
                ValueEnhancers.registerEnhancer(field, rewriter);
                ValueEnhancers.save();
            }
        } catch (Exception exp) {
        	GUI.getInstance().displayWarningMessage(exp.toString());
        }
    }
    
    public void initProgressBar(int maxValue) {
        progressBar.setValue(0);
        progressBar.setMaximum(maxValue);
    }

    public void updateProgressBar() {
        int current = progressBar.getValue();
        progressBar.setValue(current + 1);
    }    

    private void buildDialog() {
        getContentPane().setLayout(Layout.getGBL());

        cbFormat.addItem(DcResources.getText("lblPersonFirstnameLastName"));
        cbFormat.addItem(DcResources.getText("lblPersonLastNameFirstname"));
        
        /***********************************************************************
         * Settings
         **********************************************************************/
        JPanel panelSettings = new JPanel(false);
        panelSettings.setLayout(Layout.getGBL());
        
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        
        panelSettings.add(checkEnabled,     Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelSettings.add(ComponentFactory.getLabel(DcResources.getText("lblNameDisplayFormat")),         
                Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 5, 0, 5), 0, 0));
        panelSettings.add(cbFormat,         Layout.getGBC(1, 1, 1, 1, 10.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 0, 5), 0, 0));
        panelSettings.add(buttonSave,       Layout.getGBC(1, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        
        /***********************************************************************
         * Rewrite Name
         **********************************************************************/
        JPanel panelRewrite = new JPanel(false);
        panelRewrite.setLayout(Layout.getGBL());
        
        DcLongTextField explanation = ComponentFactory.getLongTextField();
        explanation.setText(DcResources.getText("msgAssociateNameRewriterExplanation"));
        ComponentFactory.setUneditable(explanation);
        
        panelRewrite.add(explanation, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        buttonRun.addActionListener(this);
        buttonRun.setActionCommand("rewrite");
        
        JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("cancel");
        
        JPanel panel = new JPanel();
        panel.add(buttonRun);
        panel.add(buttonCancel);
        
        panelRewrite.add(panel,   Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        
        panelRewrite.add(progressBar, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        /***********************************************************************
         * Main
         **********************************************************************/
        panelRewrite.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblRewriteAll")));
        panelSettings.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblSettings")));
        
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");

        getContentPane().add(panelSettings, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0,
                GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelRewrite,  Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(buttonClose,  Layout.getGBC(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 10), 0, 0));        

        Collection<? extends IValueEnhancer> enhancers = 
            ValueEnhancers.getEnhancers(module.getIndex(), ValueEnhancers._ASSOCIATENAMEREWRITERS);
        
        if (enhancers != null && enhancers.size() > 0)
            setEnhancers(enhancers.toArray()[0]);
        else 
            cbFormat.setSelectedIndex(0);
        
        setResizable(false);
        pack();
        setSize(new Dimension(500,400));
        setCenteredLocation();
    }
    
    private void setEnhancers(Object enhancer) {
        if (enhancer instanceof AssociateNameRewriter) {
            AssociateNameRewriter rewriter = (AssociateNameRewriter) enhancer;
            checkEnabled.setSelected(rewriter.isEnabled());
            cbFormat.setSelectedIndex(rewriter.getOrder());
        }
    }
    
    private void rewrite() {
        save();
        this.canceled = false;
        Rewriter rewriter = new Rewriter();
        rewriter.start();
    }

    private class Rewriter extends Thread {
        
        public Rewriter() {}
        
        @Override
        public void run() {
            boolean active = false;
            
            for (DcField field : module.getFields()) {
                
                if (canceled) break;
                
                IValueEnhancer[] enhancers = field.getValueEnhancers();
                for (int i = 0; i < enhancers.length && !canceled; i++) {
                    if (enhancers[i].isEnabled() && enhancers[i] instanceof AssociateNameRewriter) {
                        active = true;
                        rewrite((AssociateNameRewriter) enhancers[i], 
                                module.getField(((AssociateNameRewriter) enhancers[i]).getField()));
                    }
                }
            }
            
            if (!active && !canceled) {
            	GUI.getInstance().displayErrorMessage("msgNoAssociateNameRewritersFound");
            } else {
                // refresh the view
            	GUI.getInstance().getSearchView(module.getIndex()).refresh();
            }            
        }
        
        private void rewrite(AssociateNameRewriter rewriter, DcField field) {
            save();
            
            buttonClose.setEnabled(false);
            buttonRun.setEnabled(false);
            buttonSave.setEnabled(false);            
            
            Connector conn = DcConfig.getInstance().getConnector();
            try {
                DataFilter df = new DataFilter(module.getIndex());
                df.addEntry(new DataFilterEntry(
                        module.getIndex(), 
                        DcAssociate._E_FIRSTNAME, 
                        Operator.IS_FILLED, null));
                df.addEntry(new DataFilterEntry(
                        module.getIndex(), 
                        DcAssociate._F_LASTTNAME, 
                        Operator.IS_FILLED, null));
            
                List<DcObject> items = conn.getItems(df, new int[] {
                		DcObject._ID, 
                		field.getIndex(),
                		DcAssociate._E_FIRSTNAME,
                		DcAssociate._F_LASTTNAME});
                
                initProgressBar(items.size());
                
                String firstName;
                String lastName;
                String newName;
                String name;
                for (DcObject item : items) {
                    
                    if (canceled) break;
                    
                    lastName = item.getDisplayString(DcAssociate._F_LASTTNAME);
                    firstName = item.getDisplayString(DcAssociate._E_FIRSTNAME);
                  
                    name = item.getDisplayString(DcAssociate._A_NAME);
                    newName = rewriter.getName(firstName, lastName);
                     
                     if (!name.equalsIgnoreCase(newName)) {
                    	 item.setUpdateGUI(false);
                         item.setValue(field.getIndex(), newName);
                         conn.saveItem(item);
                     }
                     
                     updateProgressBar();
                }                

            } catch (Exception e) {
                logger.error("An error occurred while rewriting titles", e);
            } finally {
                if (buttonRun != null) {
                    buttonClose.setEnabled(true);
                    buttonRun.setEnabled(true);
                    buttonSave.setEnabled(true);
                }
            }
        }
    }
    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("rewrite"))
            rewrite();
        else if (ae.getActionCommand().equals("cancel"))
            cancel();
        else if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("save"))
            save();
    }
}