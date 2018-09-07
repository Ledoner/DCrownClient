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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.datacrow.DataCrow;
import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcFileField;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcProgressBar;
import net.datacrow.console.windows.messageboxes.NativeMessageBox;
import net.datacrow.console.windows.messageboxes.NativeQuestionBox;
import net.datacrow.core.DcConfig;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.DataDirectoryCreator;
import net.datacrow.core.utilities.Directory;
import net.datacrow.server.db.DatabaseManager;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Utilities;

public class DataDirSetupDialog extends NativeDialog implements ActionListener, IClient {
    
    private DcProgressBar progressBar = new DcProgressBar();
    private DcFileField selectDir = ComponentFactory.getFileField(false, true); 
    
    private JTextArea textLog = ComponentFactory.getTextArea();
    
    private DcCheckBox cbMoveFiles = ComponentFactory.getCheckBox("Move files from current to new user folder");
    private JButton buttonStart = ComponentFactory.getButton("OK");
    
    private boolean success = false;
    private boolean shutdown = false;
    private boolean restart = true;
    private boolean moveEnabled = true;
    
    private String[] args;
    private final String selectedDataDir;
    
    private boolean overwrite = false;
    
    static {
        // as the folder structure has not yet been setup, allow this method to initialize the settings
        
        if (DcSettings.getSettings() == null)
            DcSettings.initialize();
    }
    
    public DataDirSetupDialog(String[] args, 
                              String selectedDataDir) {
        setTitle("User Folder Configuration");
        setIconImage(new DcImageIcon(new File(DcConfig.getInstance().getInstallationDir(), "icons/datacrow64.png")).getImage());
        this.args = args;
        this.selectedDataDir = selectedDataDir;
    }
    
    public void setMoveEnabled(boolean b) {
        this.moveEnabled = b;
    }

    public void build() {
        buildDialog();
        pack();
        setSize(new Dimension(450, 300));
        setLocation(Utilities.getCenteredWindowLocation(getSize(), false));
        enableActions(true);
    }
    
    public void setRestart(boolean b) {
        this.restart = b;
    }
    
    public void setShutDown(boolean b) {
        this.shutdown = b;
    }
    
    @Override
    public void close() {
        if (restart)
        	setVisible(false); // makes this dialog 'hang' the current process. else, two startup processes will be running.
        else
            super.close();
    }

    protected void initialize() {
        progressBar.setValue(0);
        enableActions(false);
    }
    
    protected void cancel() {
        enableActions(true);
    }
    
    protected void enableActions(boolean b) {
        if (buttonStart != null)
            buttonStart.setEnabled(b);
        
        if (!b) {
            progressBar.setValue(0);
            textLog.setText("");
        }
    }  
    
    public boolean isSuccess() {
        return success;
    }
    
    public void stop() {
        close();
        
        DcConfig dcc = DcConfig.getInstance();
        dcc.clear();
        dcc.setRestartMode(true);
        
        String dataDir = selectDir.getFile().toString();
        dataDir = dataDir.endsWith("/") || dataDir.endsWith("\\") ? dataDir : dataDir + "/";
        
        dcc.setDataDir(dataDir);
        dcc.getClientSettings().save();
        
        if (dcc.getConnector() != null)
            dcc.getConnector().close();
        
        if (shutdown) {
            GUI.getInstance().getMainFrame().close();
        } else if (restart) {
            DatabaseManager.getInstance().closeDatabases(false);
            DataCrow.main(args);
        }
    }
    
    private boolean isMoveFiles() {
        return moveEnabled && cbMoveFiles.isSelected();
    }

    private void setupDataDir() {
        File target = selectDir.getFile();
        File installDir = new File(DcConfig.getInstance().getInstallationDir());
        
        if (target == null) {
            new NativeMessageBox("Warning", "Please select a folder before continuing");
        } else {
            
            if (CoreUtilities.isEmpty(target.getParent())) {
                NativeQuestionBox qb = new NativeQuestionBox(
                        "It is not recommended to select the root folder for the user folder of Data Crow. " +
                        "Continue anyway?");
                
                if (!qb.isAffirmative()) return;
            }
            
            Directory dir = new Directory(DcConfig.getInstance().getDatabaseDir(), false, new String[] {"script"});
            File f = new File(target, "database");
            boolean filesChecked = false;
            if (f.exists() && dir.read().size() > 0 && isMoveFiles()) {
                NativeQuestionBox qb = new NativeQuestionBox(
                        "It seems the target folder has been set up as a user folder before. Do you want to move " +
                        "the data from the old location to the new location?\n " +
                        "\"No\" = leave target directory as is, \n " +
                        "\"Yes\" = moves all files over from the old to the new location.");
                
                overwrite = qb.isAffirmative();
                filesChecked = true;
            }
            
            if (!filesChecked) {
                dir = new Directory(target.toString(), false, null);
                
                if (dir.read().size() > 0 && !f.exists()) {
                    NativeQuestionBox qb = new NativeQuestionBox(
                            "The selected target folder already contains files and / or directories. " +
                            "Do you want to continue?");
                    
                    if (!qb.isAffirmative()) return;
                }
            }
            
            if (target.equals(installDir)) {
                new NativeMessageBox("Warning", "The installation directory can't selected as the user folder. " +
                        "You CAN select a sub folder within the installation folder though.");
                return;
            }

            buttonStart.setEnabled(false);
            
            DataDirectoryCreator ddc = new DataDirectoryCreator(target, this);
            ddc.setMoveFiles(isMoveFiles());
            ddc.setAllowOverwrite(overwrite);
            ddc.start();
        }
    }
    
    private void buildDialog() {
        
        getContentPane().setLayout(Layout.getGBL());

        //**********************************************************
        //Main panel
        //**********************************************************
        JPanel panelMain = new JPanel();
        panelMain.setLayout(Layout.getGBL());

        buttonStart.addActionListener(this);
        buttonStart.setActionCommand("start");

        DcLongTextField helpText = ComponentFactory.getHelpTextField();
        helpText.setText("Please select the user folder where Data Crow will store its data. " +
                "Existing information can be migrated to the selected folder. You can also select a folder in the Data Crow installation folder (as per the old Data Crow standard) " +
                "but you will have to make sure that you have the correct priviliges.");
        
        if (selectedDataDir != null) 
            selectDir.setFile(new File(selectedDataDir));
        
        panelMain.add(helpText, Layout.getGBC(0, 0, 2, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        if (moveEnabled)
            panelMain.add(cbMoveFiles, Layout.getGBC(0, 1, 2, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0)); 
        
        panelMain.add(selectDir,     Layout.getGBC(0, 2, 1, 1, 10.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        panelMain.add(buttonStart,   Layout.getGBC(1, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));

        
        //**********************************************************
        //Log panel
        //**********************************************************
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());
        
        JScrollPane logScroller = new JScrollPane(textLog);
        logScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelLog.setBorder(ComponentFactory.getTitleBorder("Messages"));
        panelLog.add(logScroller, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new Insets(5, 5, 5, 5), 0, 0));

        
        //**********************************************************
        //Main
        //**********************************************************
        getContentPane().add(panelMain,         Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelLog,          Layout.getGBC( 0, 1, 1, 1, 5.0, 5.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                 new Insets(0, 0, 0, 0), 0, 0));  
        getContentPane().add(progressBar,       Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("start"))
            setupDataDir();
    }

    @Override
    public void notify(String msg) {
        if (textLog != null) 
            textLog.insert(msg + '\n', 0);
    }

    @Override
    public void notifyWarning(String msg) {
        new NativeMessageBox("Warning", msg);
    }

    @Override
    public void notifyError(Throwable t) {
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {   
        this.success = success;
        stop();
    }

    @Override
    public void notifyTaskStarted(int taskSize) {
        progressBar.setValue(0);
        progressBar.setMaximum(taskSize);
    }

    @Override
    public void notifyProcessed() {  
        if (progressBar != null)
            progressBar.setValue(progressBar.getValue() + 1);        
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
