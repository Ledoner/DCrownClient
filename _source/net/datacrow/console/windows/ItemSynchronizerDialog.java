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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcProgressBar;
import net.datacrow.console.components.panels.OnlineServicePanel;
import net.datacrow.console.components.panels.OnlineServiceSettingsPanel;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.ISynchronizerClient;
import net.datacrow.core.console.IView;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.OnlineServices;
import net.datacrow.core.services.Region;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.Servers;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.synchronizers.Synchronizer;
import net.datacrow.core.synchronizers.Synchronizers;
import net.datacrow.settings.DcSettings;
import net.datacrow.settings.Settings;

public class ItemSynchronizerDialog extends DcDialog implements ActionListener {
    
    private DcProgressBar progressBar = new DcProgressBar();
    private JTextArea textLog = ComponentFactory.getTextArea();
    private JButton buttonStart = ComponentFactory.getButton(DcResources.getText("lblRun"));
    private JButton buttonStop = ComponentFactory.getButton(DcResources.getText("lblStop"));
    private JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    private DcLongTextField textHelp = ComponentFactory.getLongTextField();
    
    private JComboBox cbItemPickMode = ComponentFactory.getComboBox();
    
    private JCheckBox checkReparseFiles = ComponentFactory.getCheckBox(DcResources.getText("lblReparseMusicFiles"));
    
    private OnlineServiceSettingsPanel panelOnlineServiceSettings;
    private OnlineServicePanel panelServer;
    private Synchronizer synchronizer;
    
    private final int module;
    private boolean cancelled = false;
    private final boolean canParseFiles;
    
    public ItemSynchronizerDialog(DcModule module) {
        super(GUI.getInstance().getMainFrame());

        this.module = module.getIndex();
        
        Synchronizers synchronizers = Synchronizers.getInstance();
        this.synchronizer = synchronizers.getSynchronizer(module.getIndex());
        
        this.canParseFiles = synchronizer.canParseFiles();

        setTitle(synchronizer.getTitle());
        setHelpIndex(synchronizer.getHelpIndex());

        buildDialog(Servers.getInstance().getOnlineServices(module.getIndex()));
        
        pack();
        
        setSize(module.getSettings().getDimension(DcRepository.ModuleSettings.stSynchronizerDialogSize));
        setCenteredLocation();
        
        enableActions(true);
    }
    
    private void saveSettings() {
        panelOnlineServiceSettings.save();

        Settings settings = DcModules.get(module).getSettings();
        if (panelServer.getServer() != null)
            settings.set(DcRepository.ModuleSettings.stMassUpdateServer, panelServer.getServer().getName());
        if (panelServer.getRegion() != null)
            settings.set(DcRepository.ModuleSettings.stMassUpdateRegion, panelServer.getRegion().getCode());
        if (panelServer.getMode() != null)
            settings.set(DcRepository.ModuleSettings.stMassUpdateMode, panelServer.getMode().getDisplayName());
        
    }
    
    protected void synchronize() {
        saveSettings();
        buttonStop.setEnabled(true);
        buttonStart.setEnabled(false);
        synchronizer.synchronize(new ItemSynchronizerMediator(this));
    }
    
    protected boolean isReparseFiles() {
        return checkReparseFiles.isSelected();
    }
    
    protected boolean useOnlineService() {
        return panelServer.useOnlineService();
    }
    
    protected int getItemPickMode() {
        return cbItemPickMode.getSelectedIndex() < 1 ?
               Synchronizer._ALL : Synchronizer._SELECTED;
    }

    protected void initialize() {
        cancelled = false;
        initProgressBar(0);
        enableActions(false);
    }
    
    protected void cancel() {
        cancelled = true;
        enableActions(true);
    }
    
    protected boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void close() {
        
        saveSettings();
        
        cancel();

        if (cbItemPickMode.getSelectedIndex() > -1)
            DcSettings.set(DcRepository.Settings.stMassUpdateItemPickMode, 
                           Long.valueOf(cbItemPickMode.getSelectedIndex()));
        
        cancelled = true;
        
        progressBar = null;
        textLog = null;
        buttonStart = null;
        buttonClose = null;
        textHelp = null;
        buttonStop = null;
        
        panelOnlineServiceSettings.save();
        panelOnlineServiceSettings.clear();
        panelOnlineServiceSettings = null;
        
        panelServer.clear();
        panelServer = null;
        synchronizer = null;
        cbItemPickMode = null;
        
        DcModules.get(module).setSetting(DcRepository.ModuleSettings.stSynchronizerDialogSize, getSize());

        super.close();
    }    
    
    protected IServer getServer() {
        return panelServer.getServer();
    }

    protected SearchMode getSearchMode() {
        return panelServer.getMode();
    }
    
    protected Region getRegion() {
        return panelServer.getRegion();
    }
    
    protected void enableActions(boolean b) {
        if (buttonStart != null)
            buttonStart.setEnabled(b);
        
        if (buttonStop != null)
            buttonStop.setEnabled(!b);
        
        if (!b) {
            progressBar.setValue(0);
            textLog.setText("");
        }
    }    
    
    public void addMessage(String message) {
        if (textLog != null) 
            textLog.insert(message + '\n', 0);
    }

    public void initProgressBar(int maxValue) {
        progressBar.setValue(0);
        progressBar.setMaximum(maxValue);
    }

    public void updateProgressBar() {
        if (progressBar != null)
            progressBar.setValue(progressBar.getValue() + 1);
    }
    
    private void buildDialog(OnlineServices servers) {
        getContentPane().setLayout(Layout.getGBL());

        //**********************************************************
        //Help panel
        //**********************************************************
        textHelp.setText(synchronizer.getHelpText());
        textHelp.setBorder(null);

        JScrollPane helpScroller = new JScrollPane(textHelp);
        helpScroller.setBorder(null);
        helpScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        helpScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        textHelp.setEditable(false);
        
        //**********************************************************
        //Settings panel
        //**********************************************************
        JPanel panelSettings = new JPanel();
        panelSettings.setLayout(Layout.getGBL());
        panelSettings.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblSettings")));
        
        panelSettings.add(ComponentFactory.getLabel(DcResources.getText("lblUpdatingWhichItems")), 
                Layout.getGBC(0, 0, 1, 1, 1.0, 1.0, 
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panelSettings.add(cbItemPickMode, 
                Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        cbItemPickMode.addItem(DcResources.getText("lblAllItemsInView"));
        cbItemPickMode.addItem(DcResources.getText("lblSelectedItemsOnly"));
        cbItemPickMode.setSelectedIndex(DcSettings.getInt(DcRepository.Settings.stMassUpdateItemPickMode));
        
        //**********************************************************
        //Online Server panel
        //**********************************************************
        panelServer = new OnlineServicePanel(servers, true, canParseFiles ? true : false);
        Settings settings = DcModules.get(module).getSettings();
        panelServer.setServer(settings.getString(DcRepository.ModuleSettings.stMassUpdateServer));
        panelServer.setMode(settings.getString(DcRepository.ModuleSettings.stMassUpdateMode));
        panelServer.setRegion(settings.getString(DcRepository.ModuleSettings.stMassUpdateRegion));

        //**********************************************************
        //Online Server Settings panel
        //**********************************************************
        panelOnlineServiceSettings = new OnlineServiceSettingsPanel(null, false, false, true, true, true, module);

        //**********************************************************
        //Re-parse panel
        //**********************************************************
        JPanel panelReparse = new JPanel();
        
        if (canParseFiles) {
            panelReparse.setLayout(Layout.getGBL());
            panelReparse.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblMusicFileProcessingConfig")));
            
            panelReparse.add(checkReparseFiles, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets(5, 5, 5, 5), 0, 0));
            checkReparseFiles.setSelected(true);
        }
        
        //**********************************************************
        //Log panel
        //**********************************************************
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());
        
        JScrollPane logScroller = new JScrollPane(textLog);
        logScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelLog.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblLog")));
        panelLog.add(logScroller, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new Insets(5, 5, 5, 5), 0, 0));


        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        
        buttonStart.addActionListener(this);
        buttonStart.setActionCommand("synchronize");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        buttonStop.addActionListener(this);
        buttonStop.setActionCommand("cancel");
        
        panelActions.add(buttonStart);
        panelActions.add(buttonStop);
        panelActions.add(buttonClose);
        
        //**********************************************************
        //Main
        //**********************************************************
        getContentPane().add(helpScroller,      Layout.getGBC(0, 0, 1, 1, 3.0, 3.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelSettings,     Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelServer,       Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelReparse,      Layout.getGBC(0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelOnlineServiceSettings,     Layout.getGBC(0, 4, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions,      Layout.getGBC( 0, 5, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(panelLog,          Layout.getGBC( 0, 6, 1, 1, 5.0, 5.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                 new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(progressBar,       Layout.getGBC( 0, 7, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("cancel"))
            cancel();
        else if (ae.getActionCommand().equals("synchronize"))
            synchronize();
    }
    
    public class ItemSynchronizerMediator implements ISynchronizerClient {
        
        private ItemSynchronizerDialog dlg;
        
        private boolean reparseFiles;
        private boolean useOnlineServices;
        private IServer server;
        private SearchMode searchMode;
        private Region region;
        private int itemPickMode;
        
        public ItemSynchronizerMediator(ItemSynchronizerDialog dlg) {
            this.dlg = dlg;
            
            this.reparseFiles = dlg.isReparseFiles();
            this.useOnlineServices = dlg.useOnlineService();
            this.server = dlg.getServer();
            this.searchMode = dlg.getSearchMode();
            this.region = dlg.getRegion();
            this.itemPickMode = dlg.getItemPickMode();
        }

        @Override
        public boolean isCancelled() {
            return dlg.isCancelled();
        }

        @Override
        public boolean isReparseFiles() {
            return reparseFiles;
        }

        @Override
        public IServer getServer() {
            return server;
        }

        @Override
        public Region getRegion() {
            return region;
        }

        @Override
        public SearchMode getSearchMode() {
            return searchMode;
        }

        @Override
        public int getItemPickMode() {
            return itemPickMode;
        }
    
        @Override
        public int getDirectoryUsage() {
            return 0;
        }

        @Override
        public boolean useOnlineServices() {
            return useOnlineServices;
        }

        @Override
        public DcObject getContainer() {
            return null;
        }

        @Override
        public DcObject getStorageMedium() {
            return null;
        }

        @Override
        public int getModuleIdx() {
            return module;
        }

        @Override
        public void notify(String msg) {
            addMessage(msg);
        }

        @Override
        public void notifyProcessed(DcObject dco) {
            GUI.getInstance().getSearchView(getModuleIdx()).getCurrent().update(dco.getID(), dco);
        }

        @Override
        public void notifyWarning(String msg) {
            addMessage(msg);
        }

        @Override
        public void notifyError(Throwable e) {
            addMessage(e.getMessage());
        }

        @Override
        public void notifyTaskCompleted(boolean success, String taskID) {
            buttonStop.setEnabled(false);
            buttonStart.setEnabled(true);
        }

        @Override
        public void notifyTaskStarted(int taskSize) {}

        @Override
        public void notifyProcessed() {}

        @Override
        public List<String> getItemKeys() {
            IView view = GUI.getInstance().getSearchView(getModuleIdx()).getCurrent();
            if (itemPickMode == Synchronizer._ALL) {
                return view.getItemKeys();
            } else {
                return view.getSelectedItemKeys();
            }
        }
    }
}
