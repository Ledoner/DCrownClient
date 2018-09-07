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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcButton;
import net.datacrow.console.components.DcObjectComboBox;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.console.ISimpleItemView;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.objects.template.Templates;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;

public class MergePropertyItemsDialog extends DcDialog implements ActionListener, IClient {
    
    private static Logger logger = Logger.getLogger(MergePropertyItemsDialog.class.getName());

    private JTextArea textLog = ComponentFactory.getTextArea();
    
    private DcModule module;
    private Collection<DcObject> items;
    private DcObjectComboBox cbItems;

    private DcButton buttonApply = ComponentFactory.getButton(DcResources.getText("lblApply"));
    private DcButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    
    private JProgressBar progressBar = new JProgressBar();
    private ISimpleItemView view;
    
    private boolean canceled = false;
    
    public MergePropertyItemsDialog(Collection<DcObject> items, DcModule module, ISimpleItemView view) {
        super(GUI.getInstance().getRootFrame());
        
        this.setTitle(DcResources.getText("lblMergeItems", module.getObjectNamePlural()));
        this.module = module;
        this.items = items;
        this.view = view;

        setHelpIndex("dc.items.mergeitems");

        build();

        setSize(DcSettings.getDimension(DcRepository.Settings.stMergeItemsDialogSize));
        setCenteredLocation();
    }

    private void replace() {
        DcObject target = (DcObject) cbItems.getSelectedItem();
        
        if (items.contains(target)) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgMergeTargetSameAsSource"));
        } else {
            MergeTask task = new MergeTask(this, items, target);
            task.start();
        }
    }

    @Override
    public void notify(String msg) {
        if (textLog != null && msg != null) 
            textLog.insert(msg + '\n', 0);
    }
    
    @Override
    public void notifyError(Throwable t) {
        notify(t.getMessage());
        logger.error(t, t);
        GUI.getInstance().displayErrorMessage(t.getMessage());
    }

    @Override
    public void notifyWarning(String msg) {
        notify(msg);
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {
        buttonApply.setEnabled(true);
        cbItems.setEnabled(true);
        progressBar.setValue(progressBar.getMaximum());
        
        if (module.getType() == DcModule._TYPE_PROPERTY_MODULE) {
            view.load();
        }
    }

    @Override
    public void notifyTaskStarted(int taskSize) {
        buttonApply.setEnabled(false);
        cbItems.setEnabled(false);
        
        progressBar.setValue(0);
        progressBar.setMaximum(taskSize);   
        
        textLog.setText("");
    }

    @Override
    public void notifyProcessed() {
        int value = progressBar.getValue();
        value = value < progressBar.getMaximum() ? value + 1 : 1;
        progressBar.setValue(value);
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void close() {
        canceled = true;
        DcSettings.set(DcRepository.Settings.stMergeItemsDialogSize, getSize());
        super.close();
    }

    private void build() {
        //**********************************************************
        //Input panel
        //**********************************************************
        cbItems = ComponentFactory.getObjectCombo(module.getIndex());

        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());
        
        panelInput.add(ComponentFactory.getLabel(DcResources.getText("lblMergeTarget")), Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));
        panelInput.add(cbItems, Layout.getGBC(1, 0, 1, 1, 100.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 0, 0, 0), 0, 0));
        
        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        
        buttonApply.addActionListener(this);
        buttonApply.setActionCommand("replace");
        
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");

        panelActions.add(buttonApply);
        panelActions.add(buttonClose);
        
        //**********************************************************
        //Progress panel
        //**********************************************************        
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(Layout.getGBL());
        panelProgress.add(progressBar, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));  
        
        //**********************************************************
        //Log Panel
        //**********************************************************
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());

        textLog.setEditable(false);
        JScrollPane scroller = new JScrollPane(textLog);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelLog.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblLog")));
        panelLog.add(scroller, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0));
        
        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());
        this.getContentPane().add(panelInput  ,Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
              ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
               new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelActions,Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
              ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
               new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelProgress,Layout.getGBC(0, 3, 1, 1, 1.0, 1.0
              ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
               new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelLog,Layout.getGBC(0, 4, 1, 1, 20.0, 20.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close")) {
            close();
        } else if (ae.getActionCommand().equals("replace")) {
            replace();
        }
    }
    
    private class MergeTask extends Thread {
        
        private Collection<DcObject> items;
        private DcObject target;
        
        private IClient client;
        
        private MergeTask(IClient client, Collection<DcObject> items, DcObject target) {
            this.items = items;
            this.target = target;
            this.client = client;
        }
        
        @Override
        public void run() {
            Collection<DcObject> result = merge();
            
            if (result.size() > 0) {
                save(result);    
            } else {
                client.notify(DcResources.getText("msgSavingItemsNotNeeded"));
            }
            
            deleteItems();
            
            if (target.getModule().getType() == DcModule._TYPE_PROPERTY_MODULE) {
                String alternatives = (String) target.getValue(DcProperty._C_ALTERNATIVE_NAMES);
                alternatives = alternatives == null ? "" : alternatives;
                String altName;
                for (DcObject item : items) {
                    altName = (String) item.getValue(DcProperty._A_NAME);
                    
                    if (!CoreUtilities.isEmpty(altName) && !alternatives.toLowerCase().contains(";" + altName.toLowerCase() + ";")) {
                        alternatives += alternatives.endsWith(";") ? "" : ";";
                        alternatives += altName + ";";
                    }
                }
                
                target.setValue(DcProperty._C_ALTERNATIVE_NAMES, alternatives);
                
                try {
                    DcConfig.getInstance().getConnector().saveItem(target);
                } catch (Exception e) {
                    client.notifyError(e);
                }
            }
            
            client.notify(DcResources.getText("msgMergeCompleted"));
            GUI.getInstance().displayMessage(DcResources.getText("msgMergeCompleted"));
            client.notifyTaskCompleted(true, "");
            
            client = null;
            items = null;
            target = null;
        }
        
        private void deleteItems() {
            client.notifyTaskStarted(items.size());
            client.notify(DcResources.getText("msgDeletingReplacedItems", String.valueOf(items.size())));
            
            Connector connector = DcConfig.getInstance().getConnector();
            for (DcObject dco : items) {
                
                if (client.isCancelled()) break;
                
                try {
                    connector.deleteItem(dco);
                    
                    try {
                        sleep(100);
                    } catch (Exception e) {
                        logger.error(e, e);
                    }
                    
                } catch (Exception e) {
                    GUI.getInstance().displayErrorMessage(e.getMessage());
                    logger.error(e, e);
                }
            }
            
        }
        
        private void save(Collection<DcObject> c) {
            
            client.notifyTaskStarted(items.size());
            
            try {
                
                Connector connector = DcConfig.getInstance().getConnector();
                
                for (DcObject dco : c) {
                    
                    if (client.isCancelled()) break;
                    
                    client.notify(DcResources.getText("msgSavingItem", dco.toString()));
                    dco.setUpdateGUI(false);
                    connector.saveItem(dco);
                    
                    try {
                        sleep(100);
                    } catch (Exception e) {
                        logger.error(e, e);
                    }
                }
            
            } catch (Exception e) {
                GUI.getInstance().displayErrorMessage(e.getMessage());
                logger.error(e, e);
            } finally {
                GUI.getInstance().getSearchView(DcModules.getCurrent().getIndex()).refresh();
            }
        }
        
        private Collection<DcObject> getApplicableTemplates(DcModule module, DcField field) {
            Collection<DcObject> result = new ArrayList<DcObject>();
            
            List<DcTemplate> templates = Templates.getTemplates(module.getIndex());
            boolean hasReference = false;
            for (DcTemplate template : templates) {
                
                if (client.isCancelled()) break;
                
                Object o = template.getValue(field.getIndex());
                
                if (o == null) continue;
                
                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                    Collection<DcObject> mappings = (Collection<DcObject>) template.getValue(field.getIndex());
                    
                    if (mappings == null) continue;
                    
                    for (DcObject mapping : mappings) { // loop through mappings
                        for (DcObject reference : items) { // loop through to be replaced values
                            if (mapping.getValue(DcMapping._B_REFERENCED_ID).equals(reference.getID()))
                                hasReference = true;
                        }
                    }
                } else {
                    for (DcObject reference : items) {
                        if (o.equals(reference))
                            hasReference = true;
                    }
                }
                
                if (hasReference)
                    result.add(template);
            }
            
            return result;
        }
        
        private Collection<DcObject> merge() {
            Collection<DcObject> c = new ArrayList<DcObject>();
            
            try {
                // loop through all modules
                for (DcModule mm : DcModules.getReferencingModulesAll(module.getIndex())) {
                    
                    if (client.isCancelled()) break;
                    
                    if (    mm.isAbstract() || 
                            mm.getType() == DcModule._TYPE_MAPPING_MODULE ||
                            mm.getType() == DcModule._TYPE_TEMPLATE_MODULE) continue;
                    
                    client.notify(DcResources.getText("msgProcessingItemsFromModule", mm.getName()));
                    
                    try {
                        sleep(100);
                    } catch (Exception e) {
                        logger.error(e, e);
                    }
                    
                    // loop through all the field of the module
                    client.notifyProcessed();
                    Connector connector = DcConfig.getInstance().getConnector();
                    for (DcField  field : mm.getFields()) {
                        
                        if (client.isCancelled()) break;
                        
                        if (field.getReferenceIdx() == module.getIndex()) {
                            DataFilter df = new DataFilter(mm.getIndex());
                           
                            // check for references to one of the to be removed items
                            for (DcObject reference : items)
                                df.addEntry(new DataFilterEntry(DataFilterEntry._OR, mm.getIndex(), field.getIndex(), Operator.CONTAINS, reference));
                           
                            Collection<Integer> include = new ArrayList<Integer>();
                            include.add(field.getIndex());
                            int[] fields = mm.getMinimalFields(include);
                            
                            client.notify(DcResources.getText("msgQueryingForReferences"));
                            Collection<DcObject> mmItems = connector.getItems(df, fields);
                            
                            // add the templates - check if they have a reference and in case they do add them to the overall result.
                            mmItems.addAll(getApplicableTemplates(mm, field));
                            
                            // loop through each of the referencing items
                            Collection<DcObject> removals = new ArrayList<DcObject>();
                            for (DcObject dco : mmItems) {
                    
                                if (client.isCancelled()) break;
                                
                                client.notify(DcResources.getText("msgProcessingItem", dco.toString()));
                                
                                // remove old reference for multi-reference fields
                                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                                
                                    Collection<DcObject> mappings = (Collection<DcObject>) dco.getValue(field.getIndex());
                                    
                                    if (mappings == null) continue;
                                   
                                    for (DcObject mapping : mappings) { // loop through mappings
                                        for (DcObject reference : items) { // loop through to be replaced values
                                            if (mapping.getValue(DcMapping._B_REFERENCED_ID).equals(reference.getID()))
                                                removals.add(mapping);
                                        }
                                    }
                                   
                                    for (DcObject removal : removals)
                                        mappings.remove(removal);
                                }
                                
                                client.notifyProcessed();
                               
                                if (client.isCancelled()) break;
                                
                                // apply the target value and add to the results.
                                dco.createReference(field.getIndex(), target);
                                dco.setChanged(field.getIndex(), true);
                                c.add(dco);
                                try {
                                    sleep(100);
                                } catch (Exception e) {
                                    logger.error(e, e);
                                }
                            }   
                        }
                    }
                }
                
                client.notify(DcResources.getText("msgProcessingOfItemsSuccess"));
                
            } catch (Exception e) {
                GUI.getInstance().displayErrorMessage(e.getMessage());
                client.notify(e.getMessage());
                logger.error(e, e);
            }
            
            return c;
        }
    }
}