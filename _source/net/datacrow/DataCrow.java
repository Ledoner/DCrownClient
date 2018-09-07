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

package net.datacrow;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import net.datacrow.connector.ClientToServerConnector;
import net.datacrow.connector.DirectConnector;
import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.MainFrame;
import net.datacrow.console.windows.ChangeUserFolderQuestionBox;
import net.datacrow.console.windows.DataDirSetupDialog;
import net.datacrow.console.windows.DonateDialog;
import net.datacrow.console.windows.SelectLanguageDialog;
import net.datacrow.console.windows.TipOfTheDayDialog;
import net.datacrow.console.windows.drivemanager.DriveManagerDialog;
import net.datacrow.console.windows.help.StartupHelpDialog;
import net.datacrow.console.windows.loan.LoanInformationForm;
import net.datacrow.console.windows.log.LogPanel;
import net.datacrow.console.windows.messageboxes.NativeMessageBox;
import net.datacrow.console.wizards.tool.ToolSelectWizard;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.DcStarter;
import net.datacrow.core.IStarterClient;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.data.Operator;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.fileimporter.FileImporters;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.server.Connector;
import net.datacrow.core.synchronizers.Synchronizers;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.log.TextPaneAppender;
import net.datacrow.fileimporter.EbookImport;
import net.datacrow.fileimporter.ImageImporter;
import net.datacrow.fileimporter.MovieImporter;
import net.datacrow.fileimporter.MusicAlbumImporter;
import net.datacrow.fileimporter.SoftwareImporter;
import net.datacrow.settings.DcSettings;
import net.datacrow.synchronizers.AssociateSynchronizer;
import net.datacrow.synchronizers.BookSynchronizer;
import net.datacrow.synchronizers.MovieSynchronizer;
import net.datacrow.synchronizers.MusicAlbumSynchronizer;
import net.datacrow.synchronizers.SoftwareSynchronizer;
import net.datacrow.tabs.Tabs;

/**
 * This is the starting point of the application.
 * 
 * @author Robert Jan van der Waals
 */
public class DataCrow implements IStarterClient {

    private static Logger logger;

    private boolean userDirAsParameter = false;

    private static DcStarter starter;

    private static String[] args;

    public static void main(String[] args) {
        DataCrow.args = args;

        BasicConfigurator.configure();

        DataCrow dc = new DataCrow();

        String installationDir = "";
        String dataDir = "";
        String db = null;

        String serverAddress = null;
        int applicationServerPort = 9000;
        int imageServerPort = 9001;
        String username = null;
        String password = null;

        boolean client = false;
        boolean determiningInstallDir = false;
        boolean determiningUserDir = false;

        DcConfig dcc = DcConfig.getInstance();

        for (String arg : args) {
            if (arg.toLowerCase().startsWith("-dir:")) {
                installationDir = arg.substring(5, arg.length());
                determiningInstallDir = true;
                determiningUserDir = false;
            } else if (arg.toLowerCase().startsWith("-userdir:")) {
                dataDir = arg.substring("-userdir:".length(), arg.length());
                determiningUserDir = true;
                determiningInstallDir = false;
            } else if (arg.toLowerCase().startsWith("-imageserverport:")) {
                String s = "";
                try {
                    s = arg.substring("-imageserverport:".length(), arg.length());
                    imageServerPort = Integer.parseInt(s);
                } catch (NumberFormatException nfe) {
                    logger.error("Incorrect port number " + s, nfe);
                }
            } else if (arg.toLowerCase().startsWith("-server:")) {
                String server = arg.substring("-server:".length(), arg.length());
                int index = server.indexOf(":");
                serverAddress = index > -1 ? server.substring(0, index) : server;
                String port = index > -1 ? server.substring(index + 1) : "";

                try {
                    applicationServerPort = Integer.parseInt(port);
                } catch (NumberFormatException nfe) {
                    logger.error("Incorrect port number " + port, nfe);
                }

            } else if (arg.toLowerCase().startsWith("-db:")) {
                db = arg.substring("-db:".length());
            } else if (arg.equalsIgnoreCase("-client")) {
                client = true;
            } else if (arg.toLowerCase().startsWith("-help")) {
                StartupHelpDialog dialog = new StartupHelpDialog();
                GUI.getInstance().openDialogNativeModal(dialog);
                System.exit(0);
            } else if (arg.toLowerCase().startsWith("-debug")) {
                dcc.setDebug(true);
            } else if (arg.toLowerCase().startsWith("-credentials:")) {
                String credentials = arg.substring("-credentials:".length());
                int index = credentials.indexOf("/");
                username = index > -1 ? credentials.substring(0, index) : credentials;
                password = index > -1 ? credentials.substring(index + 1) : "";
            } else if (determiningInstallDir) {
                installationDir += " " + arg;
            } else if (determiningUserDir) {
                dataDir += " " + arg;
            } else {
                System.out.println("The following optional parameters can be used:");
                System.out.println("");
                System.out.println("-dir:<installdir>");
                System.out.println("Specifies the installation directory.");
                System.out.println("Example: java -jar datacrow.jar -dir:d:/datacrow");
                System.out.println("");
                System.out.println("-client");
                System.out.println("Indicates this installation will connect to a server.");
                System.out.println("");
                System.out.println("-userdir:<userdir>");
                System.out.println("Specifies the user directory. Start the name with a dot (.) to make the path relative to the installation folder.");
                System.out.println("Example: java -jar datacrow.jar -userdir:d:/datacrow");
                System.out.println("");
                System.out.println("-db:<databasename>");
                System.out.println("Forces Data Crow to use an alternative database.");
                System.out.println("Example: java -jar datacrow.jar -db:testdb");
                System.out.println("");
                System.out.println("-debug");
                System.out.println("Debug mode for additional logging information.");
                System.out.println("Example: java -jar datacrow.jar -debug");
                System.out.println("");
                System.out.println("-clearsettings");
                System.out.println("Loads the default Data Crow settings. Disgards all user settings.");
                System.out.println("Example: java -jar datacrow.jar -clearsettings");
                System.out.println("");
                System.out.println("-credentials:username/password");
                System.out.println("Specify the login credentials to start Data Crow without displaying the login dialog.");
                System.out.println("Example (username and password): java -jar datacrow.jar -credentials:sa/12345");
                System.out.println("Example (username without a password): java -jar datacrow.jar -credentials:sa");
                System.out.println("");
                System.out.println("-server:<IP address>:<port>");
                System.out.println("Specifies the address and port of the Data Crow application server.");
                System.out.println("Example: java -jar datacrow.jar -server:192.168.0.100:9000");
                System.out.println("");
                System.out.println("-imageserverport:<port number>");
                System.out.println("Specifies the port to be used by the HTTP images server.");
                System.out.println("Example: java -jar datacrow.jar -imageserverport:9001");
                System.exit(0);
            }
        }

        if (dataDir.length() > 0)
            dataDir = !dataDir.endsWith("/") && !dataDir.endsWith("\\") ? dataDir + File.separatorChar : dataDir; 
        
        if (db != null)
            dcc.setDatabaseName(db);
            
        dcc.setInstallationDir(installationDir);
        dcc.setDataDir(dataDir);

        if (CoreUtilities.isEmpty(serverAddress) && !client)
            dcc.setOperatingMode(DcConfig._OPERATING_MODE_STANDALONE);
        else
            dcc.setOperatingMode(DcConfig._OPERATING_MODE_CLIENT);

        starter = new DcStarter(dc);

        if (starter.initialize()) {
            
            GUI.getInstance().showSplashScreen();
            
            dc.initializeConnector(serverAddress, applicationServerPort, imageServerPort, username, password);
            dc.start();            
        }
    }

    private void start() {

        try {
            installFonts();
            installLafs();

            checkLanguage();
            confirmUserDir(args);

            ComponentFactory.setLookAndFeel();
            Tabs.getInstance().initialize();

            initSynchronizers();
            initFileImporters();
            
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(
                        new Thread(new Runnable() { 
                            @Override
                            public void run() {
                                startGUI();
                                showStartupDialogs();
                            }
                        }));
            }
                        
            GUI.getInstance().closeSplashScreen();

            checkVersion();

            DcSettings.set(DcRepository.Settings.stGracefulShutdown, Boolean.FALSE);
            DcSettings.save();

            DcIconCache.getInstance().initialize();

        } catch (Throwable e) {
            System.out.println("Data Crow could not be started: " + e);
            e.printStackTrace();
            new NativeMessageBox("Error", "Data Crow could not be started: "
                    + e);
            try {
                DcSettings.set(DcRepository.Settings.stGracefulShutdown,
                        Boolean.FALSE);
                DcSettings.save();
            } catch (Exception exp) {
                logger.debug(exp, exp);
            }

            System.exit(0);
        }
    }

    private void checkVersion() {
        if (DcSettings.getBoolean(DcRepository.Settings.stCheckForNewVersion))
            new VersionChecker().start();
    }

    private void checkLanguage() {
        if (CoreUtilities.isEmpty(DcSettings.getString(DcRepository.Settings.stLanguage))) {
            SelectLanguageDialog dlg = new SelectLanguageDialog();
            GUI.getInstance().openDialogNativeModal(dlg);
        }
    }

    private void confirmUserDir(String[] args) {
        // ask the user to confirm the user directory
        if (    !userDirAsParameter && // not set through the parameters
                 DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT &&
                !DcConfig.getInstance().isRestarting() && // we are not restarting
                !DcSettings.getBoolean(DcRepository.Settings.stDoNotAskAgainChangeUserDir)) {
            changeUserDir(args);
        }
    }

    private void startGUI() {
        final MainFrame mf = new MainFrame();
        GUI.getInstance().setMainFrame(mf);
        mf.initialize();
        mf.setVisible(true);
        mf.load();
    }        


    private void showStartupDialogs() {
        try {
            if (DcSettings.getBoolean(DcRepository.Settings.stShowTipsOnStartup)) {
                TipOfTheDayDialog dlg = new TipOfTheDayDialog();
                dlg.setVisible(true);
            }

            if (DcSettings.getBoolean(DcRepository.Settings.stShowToolSelectorOnStartup)) {
                ToolSelectWizard wizard = new ToolSelectWizard();
                wizard.setVisible(true);
            }

            DataFilter df = new DataFilter(DcModules._LOAN);
            df.addEntry(new DataFilterEntry(DataFilterEntry._AND,
                    DcModules._LOAN, Loan._B_ENDDATE, Operator.IS_EMPTY, null));
            df.addEntry(new DataFilterEntry(DataFilterEntry._AND,
                    DcModules._LOAN, Loan._E_DUEDATE, Operator.IS_FILLED, null));

            Connector connector = DcConfig.getInstance().getConnector();
            for (DcObject loan : connector.getItems(df)) {
                if (((Loan) loan).isOverdue()) {
                    GUI.getInstance().displayWarningMessage("msgThereAreOverdueItems");
                    new LoanInformationForm().setVisible(true);
                    break;
                }
            }

            int usage = DcSettings.getInt(DcRepository.Settings.stUsage) + 1;
            DcSettings.set(DcRepository.Settings.stUsage, Long.valueOf(usage));

            boolean itsTime = usage == 15 || usage == 150 || usage == 1000
                    || usage == 1500 || usage == 500 || usage == 50;
            if (itsTime && DcSettings.getBoolean(DcRepository.Settings.stAskForDonation))
                new DonateDialog().setVisible(true);

            if (DcSettings.getBoolean(DcRepository.Settings.stDriveScannerRunOnStartup)) {
                DriveManagerDialog.getInstance();
                DriveManager.getInstance().startScanners();
            }

            if (DcSettings.getBoolean(DcRepository.Settings.stDrivePollerRunOnStartup)) {
                DriveManagerDialog.getInstance();
                DriveManager.getInstance().startDrivePoller();
            }
        } catch (Exception e) {
            logger.error("Could not start startup dialogs", e);
        }
    }

    private void initializeConnector(
            String serverAddress, 
            int applicationServerPort, 
            int imageServerPort, 
            String username, 
            String password) {
        
        Connector connector;
        if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT) {
            connector = new ClientToServerConnector();
        } else {
            connector = new DirectConnector();
        }

        if (username != null)
            connector.setUsername(username);

        connector.setPassword(password);
        connector.setServerAddress(serverAddress);
        connector.setApplicationServerPort(applicationServerPort);
        connector.setImageServerPort(imageServerPort);

        DcConfig.getInstance().setConnector(connector);

        connector.initialize();
    }

    private void changeUserDir(String[] args) {

        GUI.getInstance().showSplashScreen(false);

        ChangeUserFolderQuestionBox qb = new ChangeUserFolderQuestionBox();
        GUI.getInstance().openDialogNativeModal(qb);
        boolean answer = qb.isAffirmative();

        if (!answer) {
            DataDirSetupDialog dlg = new DataDirSetupDialog(args, DcConfig.getInstance().getDataDir());
            dlg.build();
            dlg.setMoveEnabled(true);
            GUI.getInstance().openDialogNativeModal(dlg);
        }
    }

    @Override
    public void configureLog4j() {
        Enumeration en = Logger.getRootLogger().getAllAppenders();
        while (en.hasMoreElements()) {
            Appender appender = (Appender) en.nextElement();
            if (appender instanceof TextPaneAppender) {
                ((TextPaneAppender) appender).addListener(LogPanel.getInstance());
            }
        }
    }
    
    private void installFonts() {
        File dirFonts = new File(DcConfig.getInstance().getInstallationDir(), "fonts");
        
        if (!dirFonts.exists()) {
            logger.info("No custom fonts could be found. The folder " + dirFonts + " does not exists");
        } else {
            String[] fonts = dirFonts.list();
            for (String font : fonts) {
                
                if (font.equals("do not delete.me") || font.equals("CVS")) continue;
                
                logger.info("Loading font " + font);
                
                try {
                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(dirFonts, font)));
                } catch (Exception e) {
                    logger.error("Could not load font " + font, e);
                }
            }
        }
    }
    
    private void initSynchronizers() {
        Synchronizers synchronizers = Synchronizers.getInstance();
        synchronizers.register(new AssociateSynchronizer(), DcModules._CONTACTPERSON);
        synchronizers.register(new BookSynchronizer(), DcModules._BOOK);
        synchronizers.register(new MovieSynchronizer(), DcModules._MOVIE);
        synchronizers.register(new MusicAlbumSynchronizer(), DcModules._MUSIC_ALBUM);
        synchronizers.register(new SoftwareSynchronizer(), DcModules._SOFTWARE);
    }
    
    private void initFileImporters() {
        FileImporters importers = FileImporters.getInstance();
        importers.register(new EbookImport(), DcModules._BOOK);
        importers.register(new ImageImporter(), DcModules._IMAGE);
        importers.register(new MovieImporter(), DcModules._MOVIE);
        importers.register(new MusicAlbumImporter(), DcModules._MUSIC_ALBUM);
        importers.register(new SoftwareImporter(), DcModules._SOFTWARE);
    }

    private void installLafs() {
        UIManager.installLookAndFeel("JTattoo - Smart",
                "com.jtattoo.plaf.smart.SmartLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Acryl",
                "com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Aero",
                "com.jtattoo.plaf.aero.AeroLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Aluminium",
                "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Bernstein",
                "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Fast",
                "com.jtattoo.plaf.fast.FastLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - HiFi",
                "com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - McWin",
                "com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Mint",
                "com.jtattoo.plaf.mint.MintLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Noire",
                "com.jtattoo.plaf.mint.MintLookAndFeel");
        UIManager.installLookAndFeel("JTattoo - Luna",
                "com.jtattoo.plaf.luna.LunaLookAndFeel");
    }

    @Override
    public void notifyLog4jConfigured() {
        logger = Logger.getLogger(DataCrow.class.getName());
    }

    @Override
    public void notifyWarning(String msg) {
        NativeMessageBox dialog = new NativeMessageBox("Warning", msg);
        GUI.getInstance().openDialogNativeModal(dialog);
        logger.warn(msg);
    }

    @Override
    public void notifyError(String msg) {
        NativeMessageBox dialog = new NativeMessageBox("Info", msg);
        GUI.getInstance().openDialogNativeModal(dialog);
        logger.info(msg);
    }

    @Override
    public void notifyFatalError(String msg) {
        new NativeMessageBox("Error", msg);
        System.out.println(msg);
        System.exit(0);
    }

    @Override
    public void requestDataDirSetup(String dataDir) {
        DataDirSetupDialog dlg = new DataDirSetupDialog(args, dataDir);
        dlg.setMoveEnabled(false);
        dlg.setRestart(true);
        dlg.setShutDown(false);
        dlg.build();
        
        GUI.getInstance().openDialogNativeModal(dlg);
    }
}
