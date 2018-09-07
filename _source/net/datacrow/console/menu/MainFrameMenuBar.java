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

package net.datacrow.console.menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcButton;
import net.datacrow.console.components.DcMenu;
import net.datacrow.console.components.DcTitledBorder;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.fileimporter.FileImporters;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.objects.template.Templates;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.core.reporting.Reports;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.synchronizers.Synchronizers;
import net.datacrow.plugins.PluginHelper;
import net.datacrow.util.launcher.URLLauncher;

import org.apache.log4j.Logger;

public class MainFrameMenuBar extends net.datacrow.console.components.DcMenuBar {

    private static Logger logger = Logger.getLogger(MainFrameMenuBar.class.getName());
    
	public static final int _SEARCHPANEL = 0;
    public static final int _INSERTPANEL = 1;
    public static final int _NOTEPANEL = 2;
    
    private final DcModule module;

    public MainFrameMenuBar(DcModule module) {
        this.module = module;
        
        TitledBorder border = new DcTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "");
        this.setBorder(border);
        
    	build();
    }
    
    private void build() {
        DcMenu menuAdministration = new AdministrationMenu(module);
        DcMenu menuTools = ComponentFactory.getMenu(DcResources.getText("lblTools"));
        DcMenu menuModules = ComponentFactory.getMenu(DcResources.getText("lblModules"));
        DcMenu menuSettings = ComponentFactory.getMenu(DcResources.getText("lblSettings"));
        DcMenu menuFile = ComponentFactory.getMenu(DcResources.getText("lblFile"));
        DcMenu menuFilter = ComponentFactory.getMenu(DcResources.getText("lblFilter"));
        DcMenu menuPlugins = ComponentFactory.getMenu(DcResources.getText("lblPlugins"));
        DcMenu menuUser = ComponentFactory.getMenu(DcResources.getText("lblUser"));
        DcMenu menuXp = ComponentFactory.getMenu(DcResources.getText("lblXpMode"));
        DcMenu menuHelp = ComponentFactory.getMenu(DcResources.getText("lblHelp"));
        
        // xp menu
        PluginHelper.add(menuXp, "BeginnerMode");
        PluginHelper.add(menuXp, "ExpertMode");
        
        // view menu
        DcMenu menuView = ComponentFactory.getMenu(DcResources.getText("lblView"));
        for (int view : module.getSupportedViews()) {
            PluginHelper.add(menuView, "ChangeView", null, null, null, view, -1, Plugin._VIEWTYPE_SEARCH);
        }
        
        menuView.addSeparator();
        PluginHelper.add(menuView, "ToggleQuickView");
        PluginHelper.add(menuView, "ToggleGroupingPane");
        PluginHelper.add(menuView, "ToggleToolbar");

        // modules menu
        DcMenu subMenuModule = ComponentFactory.getMenu(DcResources.getText("lblActiveModule"));
        for (DcModule module : DcModules.getModules()) {
            if (module.isSelectableInUI())
                PluginHelper.add(subMenuModule, "OpenModule", module.getIndex());
        }
        
        if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT) {
            PluginHelper.add(menuModules, "CreateModuleWizard");
            PluginHelper.add(menuModules, "CopyModuleWizard");
            PluginHelper.add(menuModules, "AlterModuleWizard");
            PluginHelper.add(menuModules, "RelateModuleWizard");
            PluginHelper.add(menuModules, "DeleteModuleWizard");
            menuModules.addSeparator();
            PluginHelper.add(menuModules, "ExportModuleWizard");
            PluginHelper.add(menuModules, "ImportModuleWizard");   
            menuModules.addSeparator();
        }
        
        menuModules.add(subMenuModule);
        
        // help menu
        PluginHelper.add(menuHelp, "Help");
        PluginHelper.add(menuHelp, "TipOfTheDay");
        menuHelp.addSeparator();
        PluginHelper.add(menuHelp, "About");
        PluginHelper.add(menuHelp, "Donate");
        menuHelp.addSeparator();
        PluginHelper.add(menuHelp, "Support");
        menuHelp.addSeparator();
        PluginHelper.add(menuHelp, "ToolSelectWizard");
        
        // filter menu
        PluginHelper.add(menuFilter, "Filter");
        PluginHelper.add(menuFilter, "FindReplace", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        PluginHelper.add(menuFilter, "ApplyFilter");
        menuFilter.addSeparator();
        PluginHelper.add(menuFilter, "UndoFilter");
        
        // tools menu
        if (FileImporters.getInstance().hasImporter(module.getIndex()))
            PluginHelper.add(menuTools, "FileImport");
        
        if (module.hasOnlineServices()) {
            menuTools.addSeparator();

            if (module.getIndex() == DcModules._MUSIC_ALBUM)
            	PluginHelper.add(menuTools, "RetrieveAudioCDInformation");

            PluginHelper.add(menuTools, "OnlineSearch");
            menuTools.addSeparator();
        }
        
        if (Synchronizers.getInstance().hasSynchronizer(module.getIndex())) {
            PluginHelper.add(menuTools, "MassUpdate");
            menuTools.addSeparator();
        }   
        
        // file menu
        DcMenu menuCreateNew = ComponentFactory.getMenu(IconLibrary._icoAdd, 
                DcResources.getText("lblNewItem", module.getObjectName()));
        boolean templatesPresent = false;
        if (module.getTemplateModule() != null) {
            Templates.refresh();
            for (DcTemplate template : Templates.getTemplates(module.getTemplateModule().getIndex())) {
                templatesPresent = true;
                PluginHelper.add(menuCreateNew, "CreateNew", null, null, template, -1, module.getIndex(), Plugin._VIEWTYPE_SEARCH);
            }
        }        
        
        PluginHelper.add(menuFile, "NewItemWizard", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        menuFile.addSeparator();
        PluginHelper.add(menuFile, "FileLauncher", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        menuFile.addSeparator();

        if (templatesPresent)
            menuFile.add(menuCreateNew);
        
        PluginHelper.add(menuFile, "CreateNew", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        PluginHelper.add(menuFile, "OpenItem", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        PluginHelper.add(menuFile, "EditItem", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        PluginHelper.add(menuFile, "SaveAll", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        
        if (!module.isAbstract())
            PluginHelper.add(menuFile, "Delete", module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        
        menuFile.addSeparator();
        PluginHelper.add(menuFile, "Log");
        PluginHelper.add(menuFile, "Exit");

        // settings menu
        PluginHelper.add(menuSettings, "Settings");
        
        if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT)
            PluginHelper.add(menuSettings, "UserDirSettings");
        
        if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT)
            PluginHelper.add(menuSettings, "FieldSettings", module.getIndex());
        
        menuSettings.addSeparator();
        
        if (!module.isAbstract()) {
            PluginHelper.add(menuSettings, "QuickViewSettings");
        }
        
        PluginHelper.add(menuSettings, "ViewSettings", module.getIndex());
        PluginHelper.add(menuSettings, "ItemFormSettings", module.getIndex());

        if (module.isParentModule())
            PluginHelper.add(menuSettings, "ItemFormSettings", module.getChild().getIndex());
        
        menuSettings.addSeparator();
        PluginHelper.add(menuSettings, "ChangeLookAndFeel");
        menuSettings.addSeparator();
        PluginHelper.add(menuSettings, "ResourceEditor");

        if (new Reports().hasReports(module.getIndex()))
                PluginHelper.add(menuTools, "Report");
        
        PluginHelper.add(menuTools, "Charts");
        
        menuTools.addSeparator();
        PluginHelper.add(menuTools, "ItemExporterWizard");
        PluginHelper.add(menuTools, "ItemImporterWizard");
        
        if (DcModules.get(DcModules._LOAN).isEnabled()) 
            PluginHelper.add(menuTools, "ICalendarExporter");
        
        if (!module.isAbstract()) {
            menuTools.addSeparator();
            PluginHelper.add(menuTools, "NewItems");
            menuTools.addSeparator();
            PluginHelper.add(menuTools, "UpdateAll");
            PluginHelper.add(menuTools, "FindReplace");
            
            if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT) {
                menuTools.addSeparator();
                PluginHelper.add(menuTools, "AutoIncrementer");
                
                if (module.getType() == DcModule._TYPE_ASSOCIATE_MODULE)
                    PluginHelper.add(menuTools, "AssociateNameRewriter");
                else
                    PluginHelper.add(menuTools, "TitleRewriter");
            }
        }
        
        Collection<Plugin> plugins = Plugins.getInstance().getUserPlugins(null, -1, module.getIndex(), Plugin._VIEWTYPE_SEARCH);
        for (Plugin plugin : plugins) {
            if (plugin.isShowInMenu())
                menuPlugins.add(ComponentFactory.getMenuItem(plugin));
        }
        
        if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT) {
            menuTools.addSeparator();
            PluginHelper.add(menuTools, "BackupAndRestore");
            menuTools.addSeparator();
            PluginHelper.add(menuTools, "DatabaseEditor");
            PluginHelper.add(menuTools, "DriveManager");
        }
        
        if (module.isFileBacked())
            PluginHelper.add(menuTools, "FileRenamer", module.getIndex());
        else if (module.getChild() != null && module.getChild().isFileBacked())
            PluginHelper.add(menuTools, "FileRenamer",module.getChild().getIndex());
        
        // item information menu
        menuAdministration.addSeparator();
        PluginHelper.add(menuAdministration, "LoanInformation");
        
        // user menu
        PluginHelper.add(menuUser, "ChangePassword");
        
        menuFile.setEnabled(menuFile.getItemCount() > 0);
        menuModules.setEnabled(menuModules.getItemCount() > 0);
        menuView.setEnabled(menuView.getItemCount() > 0);
        menuFilter.setEnabled(menuFilter.getItemCount() > 0);
        menuSettings.setEnabled(menuSettings.getItemCount() > 0);
        menuTools.setEnabled(menuTools.getItemCount() > 0);
        menuHelp.setEnabled(menuHelp.getItemCount() > 0);
        
        if (menuFile.isEnabled())
            this.add(menuFile);
        
        if (menuAdministration.isEnabled())
            this.add(menuAdministration);
        
        this.add(menuXp);
        
        if (menuModules.isEnabled())
            this.add(menuModules);
        
        if (menuView.isEnabled())
            this.add(menuView);
        
        if (menuFilter.isEnabled())
            this.add(menuFilter);

        if (menuSettings.isEnabled())
            this.add(menuSettings);
        
        if (menuUser.isEnabled())
            this.add(menuUser);
        
        if (menuTools.isEnabled())
            this.add(menuTools);
        
        if (plugins.size() > 0)
            this.add(menuPlugins);
        
        this.add(menuHelp);
        
        
        DcButton btTwitter = ComponentFactory.getIconButton(IconLibrary._icoTwitter);
        btTwitter.addActionListener(new TwitterActionListener());
        this.add(btTwitter);
    }
    
    private class TwitterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                URLLauncher launcher = new URLLauncher(new URL("http://twitter.com/data_crow"));
                launcher.launch();
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
    }
}
