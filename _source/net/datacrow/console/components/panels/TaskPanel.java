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

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.resources.DcResources;

public class TaskPanel extends JPanel {
    
    public static final int _DUPLICATE_PROGRESSBAR = 0;
    public static final int _SINGLE_PROGRESSBAR = 1;
    
    private JTextArea textLog = ComponentFactory.getTextArea();
    private JProgressBar pb = new JProgressBar();
    private JProgressBar pb2 = new JProgressBar();
    
    public TaskPanel(int type) {
        build(type);
    }
    
    public void destroy() {
        pb = null;
        pb2 = null;
        textLog = null;
    }

    private void build(int type) {
        setLayout(Layout.getGBL());

        //**********************************************************
        //Progress panel
        //**********************************************************
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(Layout.getGBL());
        panelProgress.add(pb, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        if (type == _DUPLICATE_PROGRESSBAR) {
            panelProgress.add(pb2, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 5, 5), 0, 0));
        }
        
        //**********************************************************
        //Log Panel
        //**********************************************************        
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());

        JScrollPane scroller = new JScrollPane(textLog);
        textLog.setEditable(false);

        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelLog.add(scroller, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0));

        panelLog.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblLog")));

        add(panelLog,      Layout.getGBC( 0, 0, 1, 1, 20.0, 20.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 5, 0), 0, 0));
        add(panelProgress, Layout.getGBC( 0, 1, 1, 1, 10.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 0, 0, 0), 0, 0));
    }
    
    public void addMessage(String msg) {
        if (textLog != null) {
            textLog.insert(msg + '\n', 0);
            textLog.setCaretPosition(0);
        }
    }

    public void clear() {
        if (textLog != null)
            textLog.setText("");
    }
    
    public void initializeTask(int count) {
        if (pb != null) {
            pb.setValue(0);
            pb2.setValue(0);
            pb.setMaximum(count);
        }
    }

    public void initializeSubTask(int count) {
        if (pb2 != null) {
            pb2.setValue(0);
            pb2.setMaximum(count);
        }
    }
    
    public void updateProgressSubTask() {
        if (pb2 != null) pb2.setValue(pb2.getValue() + 1);
    }
    
    public void updateProgressTask() {
        if (pb != null) pb.setValue(pb.getValue() + 1);
    }    
}
