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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcNumberField;
import net.datacrow.core.migration.itemexport.ItemExporterSettings;
import net.datacrow.core.resources.DcResources;

public class ItemExporterImageSettingsPanel extends JPanel {
    
    private JCheckBox cbResizeImages = ComponentFactory.getCheckBox(DcResources.getText("lblScaleImages"));
    private JCheckBox cbCopyImages = ComponentFactory.getCheckBox(DcResources.getText("lblCopyImage"));
    private DcNumberField nfWidth = ComponentFactory.getNumberField();
    private DcNumberField nfHeight = ComponentFactory.getNumberField();
    
    private DcNumberField nfMaxTextLength = ComponentFactory.getNumberField();
    
    public ItemExporterImageSettingsPanel() {
        super();
        build();
    }
    
    private void applySelection() {
        cbResizeImages.setEnabled(cbCopyImages.isSelected());
        cbResizeImages.setSelected(!cbCopyImages.isSelected() ? false : cbResizeImages.isSelected());
        
        nfWidth.setEnabled(cbResizeImages.isSelected());
        nfHeight.setEnabled(cbResizeImages.isSelected());
    }
    
    public void saveSettings(ItemExporterSettings properties, boolean saveToDisk) {
        properties.set(ItemExporterSettings._COPY_IMAGES, cbCopyImages.isSelected());
        properties.set(ItemExporterSettings._SCALE_IMAGES, cbResizeImages.isSelected());
        properties.set(ItemExporterSettings._MAX_TEXT_LENGTH, nfMaxTextLength.getValue());
        properties.set(ItemExporterSettings._IMAGE_WIDTH, nfWidth.getValue());
        properties.set(ItemExporterSettings._IMAGE_HEIGHT,nfHeight.getValue());
        
        if (saveToDisk)
            properties.save();
    }
    
    public void applySettings(ItemExporterSettings properties) {
        cbCopyImages.setSelected(properties.getBoolean(ItemExporterSettings._COPY_IMAGES));
        cbResizeImages.setSelected(properties.getBoolean(ItemExporterSettings._SCALE_IMAGES));
        nfMaxTextLength.setValue(properties.getInt(ItemExporterSettings._MAX_TEXT_LENGTH));
        
        nfWidth.setValue(properties.getInt(ItemExporterSettings._IMAGE_WIDTH));
        nfHeight.setValue(properties.getInt(ItemExporterSettings._IMAGE_HEIGHT));
        
        applySelection();
    }
    
    @Override
    public void setEnabled(boolean b) {
        cbResizeImages.setEnabled(b);
        cbCopyImages.setEnabled(b);
        nfWidth.setEnabled(b);
        nfHeight.setEnabled(b);
        nfMaxTextLength.setEnabled(b);
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        
        ResizeListener rl = new ResizeListener();
        cbResizeImages.addActionListener(rl);
        cbCopyImages.addActionListener(rl);

        Dimension size = new Dimension(100, ComponentFactory.getPreferredFieldHeight());
        nfHeight.setMinimumSize(size);
        nfHeight.setPreferredSize(size);
        nfWidth.setMinimumSize(size);
        nfWidth.setPreferredSize(size);
        
        JPanel panelImages = new JPanel();
        panelImages.setLayout(Layout.getGBL());

        panelImages.add(cbCopyImages,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.add(cbResizeImages,  
                        Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.add(nfWidth,        Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.add(ComponentFactory.getLabel(DcResources.getText("lblWidth")), 
                        Layout.getGBC( 1, 2, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.add(nfHeight,       Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.add(ComponentFactory.getLabel(DcResources.getText("lblHeight")), 
                        Layout.getGBC( 1, 3, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelImages.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblImages")));


        nfMaxTextLength.setMinimumSize(new Dimension(40, ComponentFactory.getPreferredFieldHeight()));
        nfMaxTextLength.setPreferredSize(new Dimension(40, ComponentFactory.getPreferredFieldHeight()));
        
        JPanel panelText = new JPanel();
        panelText.setLayout(Layout.getGBL());
        panelText.add(ComponentFactory.getLabel(DcResources.getText("lblMaxTextLength")), 
                        Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        panelText.add(nfMaxTextLength, 
                        Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets( 5, 5, 5, 5), 0, 0));
        
        panelText.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblText")));
        
        add(panelImages, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                         new Insets( 5, 5, 5, 5), 0, 0));
        add(panelText,   Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                         new Insets( 5, 5, 5, 5), 0, 0));
    }
    
    private class ResizeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            applySelection();
        }
    }
}
