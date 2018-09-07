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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.enhancers.TitleRewriter;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class TitleRewriterDialog extends DcDialog implements ActionListener {

    private static Logger logger = Logger.getLogger(TitleRewriterDialog.class.getName());
    
    private boolean canceled = false;
    
    private JProgressBar progressBar = new JProgressBar();
    
    private JButton buttonRun = ComponentFactory.getButton(DcResources.getText("lblRun"));
    private JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    private JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
    
    private JCheckBox checkEnabled = ComponentFactory.getCheckBox(DcResources.getText("lblEnabled"));
    private DcLongTextField txtWordList = ComponentFactory.getLongTextField();
    
    private DcModule module = DcModules.getCurrent();

    public TitleRewriterDialog() {
        super(GUI.getInstance().getMainFrame());

        buildDialog();

        setHelpIndex("dc.tools.titlerewriter");
        setTitle(DcResources.getText("lblTitleRewriter"));
        
        setCenteredLocation();
        setModal(true);
    }

    private void cancel() {
        canceled = true;
    }
    
    @Override
    public void close() {
        checkEnabled = null;
        txtWordList = null;
        progressBar = null;
        module = null;
        
        super.close();
    }

    private TitleRewriter getTitleRewriter() throws Exception {
        if (txtWordList.getText() == null || txtWordList.getText().trim().length() == 0)
            throw new Exception(DcResources.getText("msgNoMemberWordsDefined"));
        
        return new TitleRewriter(checkEnabled.isSelected(), txtWordList.getText());
    }
    
    private void save() {
        try {
            TitleRewriter titleRewriter = getTitleRewriter();
            module.removeEnhancers();
            DcField field = module.getField(titleRewriter.getField());
            if (field == null) {
                GUI.getInstance().displayWarningMessage("msgCouldNotSaveTitleRewriter");
            } else {
                ValueEnhancers.registerEnhancer(field, titleRewriter);
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

        /***********************************************************************
         * Settings
         **********************************************************************/
        JPanel panelSettings = new JPanel(false);
        panelSettings.setLayout(Layout.getGBL());
        
        JLabel lblWords = ComponentFactory.getLabel(DcResources.getText("lblWords"));
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        
        JScrollPane textScroller = new JScrollPane(txtWordList);
        textScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        textScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panelSettings.add(checkEnabled,     Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelSettings.add(lblWords,         Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 0, 5), 0, 0));
        panelSettings.add(textScroller,     Layout.getGBC(0, 2, 1, 1, 5.0, 5.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));
        panelSettings.add(buttonSave,       Layout.getGBC(0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        
        /***********************************************************************
         * Rewrite Titles
         **********************************************************************/
        JPanel panelRewrite = new JPanel(false);
        panelRewrite.setLayout(Layout.getGBL());
        
        DcLongTextField explanation = ComponentFactory.getLongTextField();
        explanation.setText(DcResources.getText("msgTitleRewriterExplanation"));
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
            ValueEnhancers.getEnhancers(module.getIndex(), ValueEnhancers._TITLEREWRITERS);
        
        if (enhancers != null && enhancers.size() > 0)
            setEnhancers(enhancers.toArray()[0]);
        else 
            txtWordList.setText("the,un,de,le,a,la,une,de,het,een,der,die,das");
        
        setResizable(false);
        pack();
        setSize(new Dimension(500,400));
        setCenteredLocation();
    }
    
    private void setEnhancers(Object enhancer) {
        if (enhancer instanceof TitleRewriter) {
            TitleRewriter rewriter = (TitleRewriter) enhancer;
            checkEnabled.setSelected(rewriter.isEnabled());
            txtWordList.setText(rewriter.getWordList());
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
                    if (enhancers[i].isEnabled() && enhancers[i] instanceof TitleRewriter) {
                        active = true;
                        rewrite((TitleRewriter) enhancers[i], 
                                module.getField(((TitleRewriter) enhancers[i]).getField()));
                    }
                }
            }
            
            if (!active && !canceled) {
                GUI.getInstance().displayErrorMessage("msgNoTitleRewritersFound");
            } else {
                IMasterView mv = GUI.getInstance().getSearchView(module.getIndex());
                if (mv != null) mv.refresh();
            }            
        }
        
        private void rewrite(TitleRewriter rewriter, DcField field) {
            save();
            
            buttonClose.setEnabled(false);
            buttonRun.setEnabled(false);
            buttonSave.setEnabled(false);            
            
            Connector conn = DcConfig.getInstance().getConnector();
            try {
            	DataFilter df = new DataFilter(module.getIndex());
            	df.addEntry(new DataFilterEntry(
            			module.getIndex(), 
            			field.getIndex(), 
            			Operator.IS_FILLED, null));
            
            	List<DcObject> items = conn.getItems(df, new int[] {DcObject._ID, DcMediaObject._A_TITLE});
            	initProgressBar(items.size());
                
                String newTitle;
                String title;
                for (DcObject item : items) {
                	
                	if (canceled) break;

                     title = item.getDisplayString(field.getIndex());
                     newTitle = (String) rewriter.apply(field, title);
                     
                     if (!title.equalsIgnoreCase(newTitle)) {
                    	 item.setUpdateGUI(false);
                    	 item.setValue(field.getIndex(), newTitle);
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