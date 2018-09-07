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

package net.datacrow.console.windows.filerenamer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.console.components.DcFilePatternField;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.console.IView;
import net.datacrow.core.filerenamer.FilePattern;
import net.datacrow.core.filerenamer.FilePatternPart;
import net.datacrow.core.filerenamer.FilePatterns;
import net.datacrow.core.filerenamer.FileRenamer;
import net.datacrow.core.filerenamer.IFileRenamerListener;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class FileRenamerDialog extends DcFrame implements ActionListener, IFileRenamerListener {

    private static final int _ALL = 0;
    private static final int _SELECTED = 1;
    
    private static Logger logger = Logger.getLogger(FileRenamerDialog.class.getName());
    
    private final DcFilePatternField patternFld;
    
    private final JTextArea logFld = ComponentFactory.getTextArea();
    private final JProgressBar progressBar = new JProgressBar();

    private final JLabel labelPatterms = ComponentFactory.getLabel(DcResources.getText("lblExistingPatterns"));
    private final JComboBox cbPatterns = ComponentFactory.getComboBox();
    
    private final JButton buttonDeletePattern = ComponentFactory.getIconButton(IconLibrary._icoRemove);
    private final JButton buttonApplyPattern = ComponentFactory.getIconButton(IconLibrary._icoAccept);
    private final JButton buttonSavePattern = ComponentFactory.getIconButton(IconLibrary._icoSave);
    
    private final JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    private final JButton buttonStart = ComponentFactory.getButton(DcResources.getText("lblStart"));
    private final JButton buttonStop = ComponentFactory.getButton(DcResources.getText("lblStop"));
    
    private final JComboBox cbItemPickMode = ComponentFactory.getComboBox();
    
    private final JRadioButton rbOriginalLoc = 
        ComponentFactory.getRadioButton(DcResources.getText("lblUseOriginalLocation"), null);
    private final JRadioButton rbAlternateLoc = 
        ComponentFactory.getRadioButton(DcResources.getText("lblUseAlternateLocation"), null);
    
    private final DcFileField fileField = ComponentFactory.getFileField(false, true);
    
    private final TitledBorder borderLog = ComponentFactory.getTitleBorder(DcResources.getText("lblLog"));
    private final TitledBorder borderPattern = ComponentFactory.getTitleBorder(DcResources.getText("lblPattern"));
    private final TitledBorder borderPathConfig = ComponentFactory.getTitleBorder(DcResources.getText("lblPathConfiguration"));
    
    private int module;
    
    private boolean canceled = false;
    
    public FileRenamerDialog(int module) {
        super(DcResources.getText("lblFileRenamer", DcModules.get(module).getObjectName()), 
              IconLibrary._icoFileRenamer);
        
        setHelpIndex("dc.tools.filerenamer");
        
        this.module = module;
        this.patternFld = ComponentFactory.getFilePatternField(module);
        
        build();

        setSize(DcSettings.getDimension(DcRepository.Settings.stFileRenamerDialogSize));
        setCenteredLocation();

        patternFld.setText(DcModules.get(module).getSettings().getString(DcRepository.ModuleSettings.stFileRenamerPattern));
    }
    
   
    public int getItemPickMode() {
        return cbItemPickMode.getSelectedIndex() < 1 ? _ALL : _SELECTED;
    }
    
    private boolean isValidPattern(String pattern) {
        boolean valid = false;
        if (pattern.length() == 0) {
            GUI.getInstance().displayMessage("msgNoPatternEntered");
        } else if (pattern.endsWith("/") || pattern.endsWith("\\")) { 
            GUI.getInstance().displayMessage("msgInvalidPatternEndsWithDir");
        } else {
            valid = true;
        }
        return valid;
    }

    private void start() {
    	
    	logFld.setText("");
    	canceled = false;
        File baseDir = null;
        if (rbAlternateLoc.isSelected()) {
        	baseDir = fileField.getFile();
            if (baseDir == null) {
                GUI.getInstance().displayMessage("msgSelectDirFirst");
                return;
            }
        }
        
        String pattern = patternFld.getText();

        if (isValidPattern(pattern)) {   
            FilePattern fp = new FilePattern(pattern, module);
            new Task(baseDir, fp, this).start();
        }
    }
    
    private class Task extends Thread {
    	
    	private File baseDir;
    	private FilePattern pattern;
    	private FileRenamerDialog parent;
    	
    	public Task(File baseDir, FilePattern pattern, FileRenamerDialog parent) {
    		this.baseDir = baseDir;
    		this.pattern = pattern;
    		this.parent = parent;
    	}
    	
    	@Override
    	public void run() {
    		parent.notifyJobStarted();
    		
			final Collection<DcObject> result = new ArrayList<DcObject>(); 
	        Collection<String> keys = new ArrayList<String>();
	        
	        IView view = DcModules.get(module).getParent() != null ?
	                    GUI.getInstance().getSearchView(DcModules.get(module).getParent().getIndex()).getCurrent() :
	                    GUI.getInstance().getSearchView(module).getCurrent();
	        if (getItemPickMode() == _ALL)
	        	 keys.addAll(view.getItemKeys());
	        else 
	        	 keys.addAll(view.getSelectedItemKeys());
	        
	        Collection<FilePatternPart> patternParts = pattern.getParts();
	        Collection<Integer> addFields = new ArrayList<Integer>();
	        for (FilePatternPart fpp : patternParts) {
	        	addFields.add(fpp.getField().getIndex());
	        }
	        
	        if (!addFields.contains(DcObject._SYS_FILENAME))
	        	addFields.add(DcObject._SYS_FILENAME);
	    
	        int[] fields = DcModules.get(module).getMinimalFields(addFields);
	        
	        notifyTaskSize(keys.size());
	        parent.notify(DcResources.getText("msgRetrievingItems"));
	        for (String key : keys) {
	        	
	        	if (canceled) break;
	        	
	        	Connector connector = DcConfig.getInstance().getConnector();
	        	if (DcModules.get(module).getParent() != null) {
	        		for (DcObject child : connector.getChildren(key, DcModules.get(module).getIndex(), fields)) {
	            		if (child.isFilled(DcObject._SYS_FILENAME));
	        				result.add(child);
	        		}
	        	} else {
	        		DcObject dco = connector.getItem(module, key, fields);
	        		if (dco.isFilled(DcObject._SYS_FILENAME))
	        			result.add(dco);
	        	}
	        	        	
	            try {
					sleep(10);
				} catch (InterruptedException e) {}
				
	            notifyProcessed();
	        }
	        
	        parent.notifyJobStopped(); 
	        
            if (result.size() == 0) {
                GUI.getInstance().displayMessage("msgNoItemsToRename");
            } else {
            	
            	if (!canceled) {
            	
	            	SwingUtilities.invokeLater(
	                    new Thread(new Runnable() { 
	                        @Override
	                        public void run() {
	                        	FileRenamerPreviewDialog dlg = new FileRenamerPreviewDialog(parent, result, pattern, baseDir);
	    		                dlg.setVisible(true);
	    		                
	    		                if (dlg.isAffirmative()) {
	    		                	FileRenamer task = FileRenamer.getInstance();
	    		                    task.start(parent, baseDir, pattern, dlg.getObjects());
	    		                    dlg.clear();
	    		                } else {
	    		                	parent.notifyJobStopped(); 
	    		                }
	                        }
	                    }));
            	}
        	}
    	}
    }
    
    private void stop() {
    	canceled = true;
        FileRenamer.getInstance().cancel();
    }

    private void toggleLocation() {
        setEnabled(rbAlternateLoc.isSelected());
    } 
    
    private void checkPatternButtonStatus() {
        buttonDeletePattern.setEnabled(cbPatterns.getSelectedItem() != null);
        buttonApplyPattern.setEnabled(cbPatterns.getSelectedItem() != null);
    }
    
    private void deletePattern() {
        FilePattern fp = (FilePattern) cbPatterns.getSelectedItem();
        if (fp != null) {
            FilePatterns.delete(fp);
            cbPatterns.removeItem(fp);
            checkPatternButtonStatus();
        }
    }
    
    private void applyPattern() {
        FilePattern fp = (FilePattern) cbPatterns.getSelectedItem();
        if (fp != null)
            patternFld.setText(fp.getPattern());
    }

    private void savePattern() {
        String pattern = patternFld.getText();
        if (isValidPattern(pattern)) {
            FilePattern fp = new FilePattern(pattern, module);
            if (!FilePatterns.exists(fp)) {
                FilePatterns.add(fp);
                cbPatterns.addItem(fp);
                checkPatternButtonStatus();
            }
        }
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (patternFld != null) {
            patternFld.setFont(ComponentFactory.getStandardFont());
            logFld.setFont(ComponentFactory.getStandardFont());
            buttonClose.setFont(ComponentFactory.getSystemFont());
            buttonStart.setFont(ComponentFactory.getSystemFont());
            buttonStop.setFont(ComponentFactory.getSystemFont());
            borderLog.setTitleFont(ComponentFactory.getSystemFont());
            borderPattern.setTitleFont(ComponentFactory.getSystemFont());
            borderPathConfig.setTitleFont(ComponentFactory.getSystemFont());
            rbAlternateLoc.setFont(ComponentFactory.getSystemFont());
            rbOriginalLoc.setFont(ComponentFactory.getSystemFont());
            fileField.setFont(ComponentFactory.getSystemFont());
            labelPatterms.setFont(ComponentFactory.getSystemFont());
            cbPatterns.setFont(ComponentFactory.getStandardFont());
            cbItemPickMode.setFont(ComponentFactory.getStandardFont());
        }
    }

    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stFileRenamerDialogSize, getSize());
        DcModules.get(module).getSettings().set(DcRepository.ModuleSettings.stFileRenamerPattern, 
                      patternFld.getText());
        
        setVisible(false);
    }
    
    protected void build() {
        //**********************************************************
        //Pattern panel
        //**********************************************************
        JPanel panelPattern = new JPanel();

        panelPattern.setLayout(Layout.getGBL());

        JScrollPane sp = new JScrollPane(patternFld);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        sp.setPreferredSize(new Dimension(100, 60));
        sp.setMinimumSize(new Dimension(100, 60));
        sp.setMaximumSize(new Dimension(800, 60));

        panelPattern.add(sp,                    Layout.getGBC( 0, 0, 5, 1, 10.0, 10.0
                ,GridBagConstraints.NORTH,      GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelPattern.add(labelPatterms,         Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
                 new Insets( 5, 7, 5, 5), 0, 0));
        panelPattern.add(cbPatterns,            Layout.getGBC( 1, 1, 1, 1, 20.0, 1.0
                ,GridBagConstraints.NORTH,      GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelPattern.add(buttonDeletePattern,   Layout.getGBC( 2, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST,  GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelPattern.add(buttonApplyPattern,    Layout.getGBC( 3, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST,  GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelPattern.add(buttonSavePattern,     Layout.getGBC( 4, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST,  GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        
        buttonDeletePattern.addActionListener(this);
        buttonApplyPattern.addActionListener(this);
        buttonSavePattern.addActionListener(this);
        
        buttonDeletePattern.setActionCommand("deletePattern");
        buttonApplyPattern.setActionCommand("applyPattern");
        buttonSavePattern.setActionCommand("savePattern");

        for (FilePattern pattern : FilePatterns.get(module))
            cbPatterns.addItem(pattern);
        
        panelPattern.setBorder(borderPattern);
        
        //**********************************************************
        //Task panel
        //**********************************************************
        JPanel panelTask = new JPanel();
        
        buttonStart.addActionListener(this);
        buttonStart.setActionCommand("start");

        buttonStop.addActionListener(this);
        buttonStop.setActionCommand("stop");

        panelTask.add(buttonStart);
        panelTask.add(buttonStop);
        
        
        //**********************************************************
        //Config panel
        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(Layout.getGBL());
        panelSettings.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblSettings")));
        
        panelSettings.add(ComponentFactory.getLabel(DcResources.getText("lblRenameFilesFor")), 
                Layout.getGBC(0, 0, 1, 1, 1.0, 1.0, 
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panelSettings.add(cbItemPickMode, 
                Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        cbItemPickMode.addItem(DcResources.getText("lblAllItemsInView"));
        cbItemPickMode.addItem(DcResources.getText("lblRenameFilesSelectedItems"));
        
                //**********************************************************
        //Config panel
        //**********************************************************
        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(Layout.getGBL());
        
        rbOriginalLoc.addActionListener(this);
        rbAlternateLoc.addActionListener(this);
        rbOriginalLoc.setActionCommand("useOriginalLocation");
        rbAlternateLoc.setActionCommand("useAlternateLocation");
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbOriginalLoc);
        bg.add(rbAlternateLoc);
        
        rbOriginalLoc.setSelected(true);
        
        panelConfig.add(rbOriginalLoc, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        panelConfig.add(rbAlternateLoc, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        panelConfig.add(fileField,          Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 5, 5, 5), 0, 0));

        panelConfig.setBorder(borderPathConfig);
        
        //**********************************************************
        //Log panel
        //**********************************************************
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());
        JScrollPane scroller = new JScrollPane(logFld);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelLog.add(scroller,     Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        panelLog.setBorder(borderLog);
        
        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelAction = new JPanel();
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        panelAction.add(buttonClose);

        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());

        this.getContentPane().add(panelPattern,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelTask,     Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelSettings, Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelConfig,   Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelLog,      Layout.getGBC( 0, 4, 1, 1, 10.0, 10.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(progressBar,   Layout.getGBC( 0, 5, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelAction,   Layout.getGBC( 0, 6, 1, 1, 0.0, 0.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));
        
        checkPatternButtonStatus();
    }

    @Override
    public void notify(Exception e) {
        notify(CoreUtilities.isEmpty(e.getMessage()) ? e.toString() : e.getMessage());
        logger.error(e, e);
    }

    @Override
    public void notify(String msg) {
        logFld.insert(msg + '\n', 0);
        logFld.setCaretPosition(0);
    }

    @Override
    public void notifyJobStarted() {
        logFld.setText("");
        buttonStart.setEnabled(false);
        buttonClose.setEnabled(false);
        buttonStop.setEnabled(true);
    }

    @Override
    public void notifyJobStopped() {
        buttonStart.setEnabled(true);
        buttonClose.setEnabled(true);
        buttonStop.setEnabled(false);
    }

    @Override
    public void notifyProcessed() {
        progressBar.setValue(progressBar.getValue() + 1);
    }
    
    @Override
    public void notifyTaskSize(int max) {
        progressBar.setValue(0);
        progressBar.setMaximum(max);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("start"))
            start();
        else if (ae.getActionCommand().equals("stop"))
            stop();
        else if (ae.getActionCommand().equals("useOriginalLocation")) 
            toggleLocation();
        else if (ae.getActionCommand().equals("useAlternateLocation"))
            toggleLocation();     
        else if (ae.getActionCommand().equals("deletePattern"))
            deletePattern();     
        else if (ae.getActionCommand().equals("applyPattern"))
            applyPattern();     
        else if (ae.getActionCommand().equals("savePattern"))
            savePattern();     
    }
}
