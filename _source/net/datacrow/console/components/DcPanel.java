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

package net.datacrow.console.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.datacrow.console.Layout;

public class DcPanel extends JPanel {

	private String helpIndex = "";

    private DcProgressBar progress = new DcProgressBar();
    private String title;
    private ImageIcon icon;

    private final JPanel pnlProgress = new JPanel();

    public DcPanel() {
        buildProgressPanel();
    }
    
    public DcPanel(String title) {
        this.title = title;
        buildProgressPanel();
    }

    public void setTitle(String s) {
        this.title = s;
    }
    
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    public String getTitle() {
    	return title;
    }

    public ImageIcon getIcon() {
    	return icon;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
    }

    public void setHelpIndex(String helpIndex) {
        this.helpIndex = helpIndex;
    }

    public String getHelpIndex() {
    	return helpIndex;
    }
    
    public void initProgressBar(final int maxValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            if (progress != null) progress.setValue(0);
            if (progress != null) progress.setMaximum(maxValue);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progress != null) progress.setValue(0);
                    if (progress != null) progress.setMaximum(maxValue);
                }
            });
        }
    }

    public void updateProgressBar() {
        if (SwingUtilities.isEventDispatchThread()) {
            if (progress != null) progress.setValue(progress.getValue() + 1);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progress != null) progress.setValue(progress.getValue() + 1);                }
            });
        }
    }
    
    public void updateProgressBar(final int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            if (progress != null) progress.setValue(value);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progress != null) progress.setValue(value);
                }
            });
        }
    }

    public JPanel getProgressPanel() {
    	return pnlProgress;
    }
    
    public void clear() {
        helpIndex = null;
        progress = null;
        title = null;
        icon = null;
    }

    private void buildProgressPanel() {
        pnlProgress.setLayout(Layout.getGBL());
        pnlProgress.add(progress, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
    }
}
