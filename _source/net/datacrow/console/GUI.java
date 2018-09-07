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

package net.datacrow.console;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import net.datacrow.console.components.lists.DcObjectList;
import net.datacrow.console.components.panels.QuickViewPanel;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.console.menu.MainFrameMenuBar;
import net.datacrow.console.views.CachedChildView;
import net.datacrow.console.views.MasterView;
import net.datacrow.console.views.View;
import net.datacrow.console.windows.FindReplaceDialog;
import net.datacrow.console.windows.IDialog;
import net.datacrow.console.windows.SplashScreen;
import net.datacrow.console.windows.UpdateAllDialog;
import net.datacrow.console.windows.fileimport.FileImportDialog;
import net.datacrow.console.windows.filerenamer.FileRenamerDialog;
import net.datacrow.console.windows.filtering.FilterDialog;
import net.datacrow.console.windows.itemforms.DcMinimalisticItemView;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.console.windows.messageboxes.MessageBox;
import net.datacrow.console.windows.messageboxes.QuestionBox;
import net.datacrow.console.windows.onlinesearch.OnlineSearchForm;
import net.datacrow.console.windows.security.UserForm;
import net.datacrow.console.windows.settings.SettingsView;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.ISimpleItemView;
import net.datacrow.core.console.IView;
import net.datacrow.core.console.IWindow;
import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.fileimporter.FileImporters;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.OnlineServices;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class GUI {
	
	private static Logger logger = Logger.getLogger(GUI.class.getName());
    
    private static GUI instance = new GUI();
    
    private Map<Integer, MasterView> insertViews = new HashMap<Integer, MasterView>();
    private Map<Integer, MasterView> searchViews = new HashMap<Integer, MasterView>();
    private Map<Integer, FileRenamerDialog> fileRenamerDialogs = new HashMap<Integer, FileRenamerDialog>();
    private Map<Integer, FilterDialog> filterDialogs = new HashMap<Integer, FilterDialog>();
    private Map<Integer, QuickViewPanel> quickViewPanels = new HashMap<Integer, QuickViewPanel>();
     
    private SettingsView settingsView;
    
    private MainFrame mf;
    
    private SplashScreen splashScreen;
    
    public static GUI getInstance() {
        return instance;
    }
    
    /**
     * Is the splash screen currently being shown?
     */
    public boolean isSplashScreenActive() {
        return splashScreen != null;
    }

    /**
     * Hides or shows the splash screen.
     */
    public void showSplashScreen(boolean b) {
        if (splashScreen != null) {
            splashScreen.setVisible(b);
            if (b) splashScreen.toFront();
        }
    }

    /**
     * Creates and shows the splash screen.
     */
    public void showSplashScreen(){
        splashScreen = splashScreen == null ? new SplashScreen() : splashScreen;
        splashScreen.splash();
    }

    /**
     * Removes the focus from the splash screen.
     */
    public void moveSplashScreenToBack() {
        if (splashScreen != null)
            splashScreen.toBack();
    }

    /**
     * Pushes the splash screen to the front.
     */
    public void moveSplashScreenToFront() {
        if (splashScreen != null)
            splashScreen.toFront();
    }
    
    public void closeSplashScreen() {
        SplashScreenCloser scc = new SplashScreenCloser();
        new Thread(scc).start();
    }
    
    private final class SplashScreenCloser implements Runnable {
        @Override
        public void run() {
            if (splashScreen != null) 
                splashScreen.dispose();
                
            splashScreen = null;
        }
    }
    
    public void showSplashMsg(String msg) {
        splashScreen.setStatusMsg(msg);
    }
    
    public void setMainFrame(MainFrame mf) {
        this.mf = mf;
    }
    
    public MainFrame getMainFrame() {
        return mf;
    }
    
    public Collection<IMasterView> getViews() {
        Collection<IMasterView> views = new ArrayList<IMasterView>();
        views.addAll(searchViews.values());
        views.addAll(insertViews.values());
        return views;
    }
    
    public FileRenamerDialog getFileRenamerDialog(int moduleIdx) {
        FileRenamerDialog dlg = fileRenamerDialogs.get(moduleIdx);
        if (dlg == null) {
            dlg = new FileRenamerDialog(moduleIdx);
            fileRenamerDialogs.put(moduleIdx, dlg);
        }
        
        return dlg;
    }
    
    /**
     * @param module
     * @return
     */
    public QuickViewPanel getQuickView(int module) {
        QuickViewPanel qvp = quickViewPanels.get(Integer.valueOf(module));
        if (qvp == null) {
            qvp = new QuickViewPanel(true);
            quickViewPanels.put(Integer.valueOf(module), qvp);
        }
        return qvp;
    }
    
    public void applySettings() {
        if (settingsView != null) settingsView.applySettings();
        
        for (MasterView mv : searchViews.values())
            mv.applySettings();
        
        for (MasterView mv : insertViews.values())
            mv.applySettings();
        
        filterDialogs.clear();
        quickViewPanels.clear();
        
        if (settingsView != null) {
            settingsView.close();
            settingsView = null;
        }
    }
    
    public boolean isSearchViewInitialized(int moduleIdx) {
        return searchViews.get(moduleIdx) != null;
    }
    
    public boolean isInsertViewInitialized(int moduleIdx) {
        return insertViews.get(moduleIdx) != null;
    }
    
    public void applyLAF() {
        Collection<Component> dialogs = new ArrayList<Component>();
        dialogs.addAll(filterDialogs.values());
        dialogs.addAll(fileRenamerDialogs.values());
        
        for (MasterView mv : searchViews.values())
            for (IView v : mv.getViews()) dialogs.add((JComponent) v);
        
        for (MasterView mv : insertViews.values())
            for (IView v : mv.getViews()) dialogs.add((JComponent) v);
        
        for (Component c : dialogs)
            SwingUtilities.updateComponentTreeUI(c);
    }
    
    public JMenuBar getMenuBar(DcModule module) {
        return module.isTopModule() ? new MainFrameMenuBar(module) : null;
    }
    
    private GUI() {
    }
    
    public FilterDialog getFilterDialog(int moduleIdx) {
        DcModule module = DcModules.get(moduleIdx);
        FilterDialog dlg = filterDialogs.get(moduleIdx);
        if (dlg == null) {
            dlg = new FilterDialog(module, GUI.getInstance().getSearchView(moduleIdx));
            filterDialogs.put(moduleIdx, dlg);
        }
        
        return dlg;
    }
    
    public IMasterView getSearchView(int moduleIdx) {
        IMasterView mv = searchViews.get(moduleIdx);
        if (mv == null) {
            initialize(DcModules.get(moduleIdx));
            mv = searchViews.get(moduleIdx);
        }
        return mv;
    }

    public IMasterView getInsertView(int moduleIdx) {
        IMasterView mv = insertViews.get(moduleIdx);
        if (mv == null) {
            initialize(DcModules.get(moduleIdx));
            mv = insertViews.get(moduleIdx);
        }
        return mv;
    }
    
    
    public UpdateAllDialog getUpdateAllDialog(IView view) {
        UpdateAllDialog dialog = new UpdateAllDialog(view);
        return dialog;
    }     
    
    public FindReplaceDialog getFindReplaceDialog(int moduleIdx) {
        IView view = getSearchView(moduleIdx).getCurrent();
        FindReplaceDialog dialog = new FindReplaceDialog(view);
        return dialog;
    } 
    
    public FileImportDialog getFileImportDialog(int moduleIdx) {
        FileImporters importers = FileImporters.getInstance();
        FileImporter importer = importers.getFileImporter(moduleIdx);
        FileImportDialog dlg = new FileImportDialog(importer);
        return dlg;
    }
    
    public void openSettingsView() {
          SettingsView view = new SettingsView(
                  DcResources.getText("lblDataCrowSettings"), 
                  DcSettings.getSettings().getSettings());
          
          ImageIcon icon = new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons/logo.jpg");
          view.setDisclaimer(icon);
  
          view.setSize(new Dimension(875, 470));
          view.setCenteredLocation();
          view.setVisible(true);
    }
    
    private void initialize(DcModule m) {
        if (m.isTopModule() || m.isChildModule()) {
            MasterView insertView = insertViews.get(m.getIndex());
           
            if (insertView == null && m.hasInsertView()) {
                insertView = new MasterView(m.getIndex(), DcRepository.ModuleSettings.stDefaultInsertView);

                for (int viewType : m.getSupportedViews()) {
                	
                	if (viewType == IMasterView._TABLE_VIEW) {
                        DcTable table = new DcTable(m, false, true);
                        table.setDynamicLoading(false);
                        
                        View tableView = m.isChildModule() ?
                                new CachedChildView(insertView,
                                        View._TYPE_INSERT, table,
                                        MasterView._TABLE_VIEW) :
                                new View(insertView,
                                        View._TYPE_INSERT, table,
                                        MasterView._TABLE_VIEW);
                        table.setView(tableView);
                        insertView.addView(MasterView._TABLE_VIEW, tableView);
                	}
                	insertViews.put(m.getIndex(), insertView);
                }
            }

            MasterView searchView = searchViews.get(m.getIndex());
            if (searchView == null && m.hasSearchView()) {
                searchView = new MasterView(m.getIndex(), DcRepository.ModuleSettings.stDefaultSearchView);
                searchView.setTreePanel(m);

                for (int viewType : m.getSupportedViews()) {
                    
                    if (viewType == IMasterView._TABLE_VIEW) {
                        DcTable table = new DcTable(m, false, true);
                        View tableView = new View(searchView,
                               View._TYPE_SEARCH, table,
                               MasterView._TABLE_VIEW);
                        table.setView(tableView);
    
                        searchView.addView(MasterView._TABLE_VIEW, tableView);
                    } else {
                        DcObjectList list = new DcObjectList(m, DcObjectList._CARDS, true, true);
                        View listView = new View(searchView, View._TYPE_SEARCH, list, MasterView._LIST_VIEW);
                        list.setView(listView);
                        searchView.addView(MasterView._LIST_VIEW, listView);
                    }

                    searchViews.put(m.getIndex(), searchView);
                }
            }
        } else if (
            m.getType() == DcModule._TYPE_ASSOCIATE_MODULE ||
            m.getType() == DcModule._TYPE_PROPERTY_MODULE) {
       }
   }
    
    public ItemForm getItemForm(int module, boolean readonly, boolean update, DcObject dco, boolean applyTemplate) {
    	return module == DcModules._USER ? 
    	        new UserForm(readonly, dco, update, applyTemplate) :
    	        new ItemForm(readonly, update, dco, applyTemplate);
    }
    
    public ISimpleItemView getItemViewForm(int moduleIdx) {
        return new DcMinimalisticItemView(moduleIdx, false);
    }
    
      /**
      * Returns an instance of the online search form. For specific implementations this
      * method can be overridden to return a specific implementation of the {@link OnlineSearchForm} class.
      * @see OnlineSearchForm
      * @param dco The item to be updated or null when searching for new items only.
      * @param itemForm The item form from which the search is started or null
      * @param advanced Indicates if the advanced options should be shown.
      */
     public OnlineSearchForm getOnlineSearchForm(OnlineServices os, DcObject dco, ItemForm itemForm, boolean advanced) {
         return new OnlineSearchForm(os, dco, itemForm, advanced);
     }
     
     private JFrame rootFrame = null;
     
     private List<IWindow> openWindows = new ArrayList<IWindow>();

     public void setRootFrame(JFrame f) {
         rootFrame = f;
     }
     
     public List<IWindow> getOpenWindows() {
         return openWindows;
     }
     
     public void addOpenWindow(IWindow window) {
         openWindows.add(window);
         logger.debug("Added opened Window " + window);
     }

     public void removeOpenWindow(IWindow window) {
         openWindows.remove(window);
         logger.debug("Removed registered open Window " + window);
     }
     
     public JFrame getRootFrame() {
         if (rootFrame != null) 
             return rootFrame;
         
         return mf;
     }

     /**
      * Opens a Question dialog. The message can either be a string or a resource key. 
      * @param msg Message string or resource key.
      * @return
      */
     public boolean displayQuestion(String msg) {
         QuestionBox mb = new QuestionBox(msg.startsWith("msg") ? DcResources.getText(msg) : msg);
         open(mb);
         return mb.isAffirmative();
     }

     /**
      * Opens an information dialog. The message can either be a string or a resource key. 
      * @param msg Message string or resource key.
      * @return
      */    
     public void displayMessage(String msg) {
         if (msg == null) return;
         
         MessageBox mb = new MessageBox(msg.startsWith("msg") ? DcResources.getText(msg) : msg, MessageBox._INFORMATION);
         open(mb);
     }

     /**
      * Opens an error dialog. The message can either be a string or a resource key. 
      * @param msg Message string or resource key.
      * @return
      */    
     public void displayErrorMessage(String msg) {
         if (msg == null) return;
         
         MessageBox mb = new MessageBox(msg.startsWith("msg") ? DcResources.getText(msg) : msg, MessageBox._ERROR);
         open(mb);
     }
     
     /**
      * Opens a warning dialog. The message can either be a string or a resource key. 
      * @param msg Message string or resource key.
      * @return
      */    
     public void displayWarningMessage(String msg) {
         if (msg == null) return;
         
         String text = msg != null && msg.startsWith("msg") ? DcResources.getText(msg) : msg;
         MessageBox mb = new MessageBox(text, MessageBox._WARNING);
         open(mb);
     }    

     /**
      * Opens a dialog in the right way:

      * - When the GUI has not been initialized the dialog is opened in a native way.
      * - If we are not in the event dispatching thread the SwingUtilities way of opening 
      *   dialogs is used.
      * - Else we just open the dialog and wait for it to finish.
      * 
      * @param dialog Any dialog implementing the IDialog interface.
      */
     private void open(final IDialog dialog) {
         if (mf == null) {
             openDialogNativeModal(dialog);
         } else if (!SwingUtilities.isEventDispatchThread()) {
             try {
                 SwingUtilities.invokeAndWait(
                         new Thread(new Runnable() { 
                             @Override
                             public void run() {
                                 dialog.setVisible(true);
                             }
                         }));
             } catch (Exception e) {
                 logger.error(e, e);
             }
         } else {
             dialog.setVisible(true);
         }
     }

     /**
      * Opens a dialog in a native fashion. The dialog blocks all input and any 
      * current running operation. This way of opening dialogs is ideal for the startup process
      * where there is no main window yet to use as the blocking source.
      *
      * @param dialog
      */
     public void openDialogNativeModal(final IDialog dialog) {
         
         GUI.getInstance().showSplashScreen(false);
         
         try {
             final AtomicBoolean active = new AtomicBoolean(true);
             
             dialog.setModal(true);
             dialog.setModal(active);
             
             if (!SwingUtilities.isEventDispatchThread()) {
                 SwingUtilities.invokeAndWait(new Runnable() {
                     @Override
                     public void run() {
                         dialog.setVisible(true);
                     }
                 });
                 
                 synchronized (active) {
                     while (active.get() == true)
                         active.wait();
                 }
                 
             } else {
                 dialog.setVisible(true);
             }

         } catch (Exception ite) {
             // can't depend on the logger; most likely the logger has not yet been initialized
             ite.printStackTrace();
         }
         
         if (GUI.getInstance().isSplashScreenActive()) 
             GUI.getInstance().showSplashScreen(true);
     }
    
     public Graphics setRenderingHint(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         try {
             if (DcSettings.getBoolean(DcRepository.Settings.stHighRenderingQuality)) {
                 g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
             }

             int renderingValue = (int) DcSettings.getLong(DcRepository.Settings.stFontRendering);
             if (renderingValue == 0)
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);    
             else if (renderingValue == 1)
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
             else if (renderingValue == 2)
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
             else if (renderingValue == 3)
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
             else if (renderingValue == 4)
                 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
                 
         } catch (Throwable e) {
             logger.error(e, e);
         }
         
         return g;
     }

}
