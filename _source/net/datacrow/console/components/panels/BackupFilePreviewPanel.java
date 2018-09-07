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

package net.datacrow.console.components.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;

import org.apache.log4j.Logger;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;

public class BackupFilePreviewPanel extends JPanel implements PropertyChangeListener {
    
    private static Logger logger = Logger.getLogger(BackupFilePreviewPanel.class.getName());
    
    private DcLongTextField preview = ComponentFactory.getLongTextField();
    
    public BackupFilePreviewPanel() {
        build();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        
        // Make sure we are responding to the right event.
        if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            
            preview.setText("");
            
            File selection = (File)e.getNewValue();
            
            try {
                if (selection == null || !selection.isFile() || selection.toString().toLowerCase().endsWith(".bck")) return;
                
                File entry = new TFile(selection.toString() + File.separator + "version.txt");
                Reader reader = new TFileReader(entry);
                try {
                    int data = reader.read();
                    StringBuffer sb = new StringBuffer();
                    while(data != -1){
                        sb.append((char) data);
                        data = reader.read();
                    }
                    preview.setText(sb.toString());
                } finally {
                    reader.close();
                }
            } catch (Exception exp) {
                logger.error(exp, exp);
            }
        }
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        JScrollPane scroller = new JScrollPane(preview);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 5, 0, 5), 0, 0));
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        preview = null;
        removeAll();
    }
}
