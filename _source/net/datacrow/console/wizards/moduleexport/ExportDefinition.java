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

package net.datacrow.console.wizards.moduleexport;

import net.datacrow.core.modules.DcModule;

public class ExportDefinition {
    
    private String path;
    
    private int module;
    private boolean exportDataMainModule = false;
    private boolean exportDataRelatedModules = true;

    public ExportDefinition() {}

    public int getModule() {
        return module;
    }

    public void setModule(DcModule module) {
        this.module = module.getIndex();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isExportDataMainModule() {
        return exportDataMainModule;
    }

    public void setExportDataMainModule(boolean exportDataMainModule) {
        this.exportDataMainModule = exportDataMainModule;
    }

    public boolean isExportDataRelatedModules() {
        return exportDataRelatedModules;
    }

    public void setExportDataRelatedModules(boolean exportDataRelatedModules) {
        this.exportDataRelatedModules = exportDataRelatedModules;
    }
}
