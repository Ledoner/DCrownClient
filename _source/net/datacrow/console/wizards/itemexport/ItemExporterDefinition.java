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

package net.datacrow.console.wizards.itemexport;

import java.io.File;

import net.datacrow.core.migration.itemexport.ItemExporter;
import net.datacrow.core.migration.itemexport.ItemExporterSettings;

public class ItemExporterDefinition {
    
    private File file;
    private ItemExporterSettings settings = new ItemExporterSettings();
    private ItemExporter exporter;
    private int[] fields;
    
    public ItemExporterDefinition() {}

    public File getFile() {
        return file;
    }

    public int[] getFields() {
        return fields;
    }

    public void setFields(int[] fields) {
        this.fields = fields;
    }

    public void setExporter(ItemExporter exporter) {
        this.exporter = exporter; 
    }
    
    public ItemExporter getExporter() {
        return exporter;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ItemExporterSettings getSettings() {
        return settings;
    }

    public void setSettings(ItemExporterSettings settings) {
        this.settings = settings;
    }
}
