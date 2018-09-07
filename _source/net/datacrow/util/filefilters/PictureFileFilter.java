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

package net.datacrow.util.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Utilities;

public class PictureFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else if (Utilities.getExtension(file).equals("jpg") ||
                   Utilities.getExtension(file).equals("jpeg")||
                   Utilities.getExtension(file).equals("png") ||
                   Utilities.getExtension(file).equals("gif") ||
                   Utilities.getExtension(file).equals("svg") ||
                   Utilities.getExtension(file).equals("bmp")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return DcResources.getText("lblPicFileFilter");
    }
}
