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

package net.datacrow.console.views.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import net.datacrow.console.views.IViewComponent;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;

import org.apache.log4j.Logger;

public class TableCopyPasteAction implements ActionListener {
    
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    private static Logger logger = Logger.getLogger(TableCopyPasteAction.class.getName());
    
    public TableCopyPasteAction() {
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        IViewComponent vc = (IViewComponent) e.getSource();
        if (e.getActionCommand().equals("copy"))
            copy(vc);
        if (e.getActionCommand().equals("paste"))
            paste(vc);
    }
    
    private void paste(IViewComponent vc) {
        DcModule module = vc.getModule();
        
        int[] fields = (int[]) vc.getModule().getSetting(DcRepository.ModuleSettings.stTableColumnOrder);
        
        try {
            String text = (String) (clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
            StringTokenizer rowTokenizer = new StringTokenizer(text, "\n");
            while (rowTokenizer.hasMoreTokens()) {
                String values = rowTokenizer.nextToken();
                DcObject dco = module.getItem();

                int fldCounter = 0;
                int index = values.indexOf("\t");
                while (index > -1) {
                    String value = values.substring(0, index);
                    if (fldCounter <= fields.length) {
                        DcField field = dco.getField(fields[fldCounter]);
                        if (field.getValueType() == DcRepository.ValueTypes._LONG) {
                            try { 
                                dco.setValue(fields[fldCounter], Long.valueOf(value.trim()));
                            } catch (Exception e) {
                                logger.warn("Could not set " + value + " for field " + fields[fldCounter], e);
                            }
                        } else {
                            dco.setValue(fields[fldCounter], value);
                        }
                    }

                    fldCounter++;
                    values = values.substring(index + 1, values.length());
                    index = values.indexOf("\t");
                }
                dco.setValue(fields[fldCounter], values);
                vc.update(dco.getID(), dco);
            }
        }
        catch (Exception exp) {
            logger.error("An error occurred while pasting data into the table", exp);
        }
    }
    
    private void copy(IViewComponent vc) {
        StringBuffer sb = new StringBuffer();
        
        int[] fields = (int[]) vc.getModule().getSetting(DcRepository.ModuleSettings.stTableColumnOrder);
        DcObject dco;
        int fieldCount;
        Object o;
        String value;
        for (int index : vc.getSelectedIndices()) {
            dco = vc.getItemAt(index);
            fieldCount = 0;
            
            for (int field : fields) {
                if (fieldCount > 0)
                    sb.append("\t");

                o = dco.getValue(field);
                value = o == null ||
                    dco.getField(field).getValueType() == DcRepository.ValueTypes._PICTURE ? "" : o.toString();
                sb.append(value);
                fieldCount++;
            }
            sb.append("\n");
        }
        StringSelection stSelection = new StringSelection(sb.toString());
        clipboard.setContents(stSelection, stSelection);        
    }
}
