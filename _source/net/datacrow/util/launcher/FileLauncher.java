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

package net.datacrow.util.launcher;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import net.datacrow.console.GUI;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;
import net.datacrow.settings.definitions.ProgramDefinition;
import net.datacrow.settings.definitions.ProgramDefinitions;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class FileLauncher extends Launcher {

	private static Logger logger = Logger.getLogger(FileLauncher.class.getName());
	
    private String filename;
    private File file;
    
    public FileLauncher(File file) {
        this.filename = file != null ? file.toString() : null;
        this.file = file;
    }
    
    public FileLauncher(String filename) {
        this.filename = filename;
        
        if (filename.startsWith("./") || filename.startsWith(".\\")) {
            this.filename = new File(DcConfig.getInstance().getInstallationDir(), filename.substring(2, filename.length())).toString();
        }
        
        this.file = new File(this.filename);
    }
    
    @Override
    public void launch() {
        if (CoreUtilities.isEmpty(filename)) {
            GUI.getInstance().displayWarningMessage("msgNoFilename");
            return;
        }
        
        if (!file.exists()) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgFileDoesNotExist", filename));
            return;
        }
            
        ProgramDefinitions definitions = (ProgramDefinitions) DcSettings.getDefinitions(DcRepository.Settings.stProgramDefinitions);
        ProgramDefinition pd = null;
        String extension = Utilities.getExtension(file);
        if (definitions != null && !CoreUtilities.isEmpty(extension)) 
            pd = definitions.getDefinition(extension);

        Desktop desktop = getDesktop();
        if (pd == null) {
            boolean launched = true;
            if (desktop != null) {
                try {
                    desktop.open(file);
                } catch (Exception exp) {
                	logger.debug("Could not launch file using the Dekstop class [" + file + "]", exp);
                    launched = false;
                }
            }

            if (!launched) {
                try {
                    // a direct launch based on the filename
                    runCmd(new String[] {filename});
                } catch (Exception ignore) {
                    GUI.getInstance().displayWarningMessage(DcResources.getText("msgNoProgramDefinedForExtension", Utilities.getExtension(file)));
                }
            }
        } else { // a program has been defined to open the specified file
            try {
                if (pd.hasParameters()) {
                    StringTokenizer st = new StringTokenizer(pd.getParameters(), " ");
                    Collection<String> c = new ArrayList<String>();
                    c.add(pd.getProgram());
                    c.add(filename);
                    while (st.hasMoreTokens()) {
                        c.add(st.nextToken());
                    }
                    runCmd(c.toArray(new String[0]));
                } else {
                    runCmd(new String[] {pd.getProgram(), filename});
                }
            } catch (Exception ignore) {
                GUI.getInstance().displayWarningMessage(DcResources.getText("msgErrorWhileExecuting", filename));
            } 
        }
    }   
}
