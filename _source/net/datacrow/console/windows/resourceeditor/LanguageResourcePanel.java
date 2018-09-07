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

package net.datacrow.console.windows.resourceeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcList;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.resources.DcLanguageResource;
import net.datacrow.core.resources.DcResources;

public class LanguageResourcePanel extends JPanel implements ListSelectionListener {

    private int selected = 0;

    private DcList topicList = new DcList();
    private Collection<LanguageResourceEditPanel> panels = new ArrayList<LanguageResourceEditPanel>();
    
    private String language;
    
    public LanguageResourcePanel(String language) {
        this.language = language;
        
        build();
        load();
    }
    
    private void load() {
        DcLanguageResource resources = DcResources.getLanguageResource(language);
        for (LanguageResourceEditPanel panel : panels)
            panel.load(resources);
    }
    
    public void save() {
        DcLanguageResource resources = DcResources.getLanguageResource(language);
        
        for (LanguageResourceEditPanel panel : panels) {
            panel.save(resources);
        }

        resources.save();
    }
    
    private void setActiveTopic() {
        selected = topicList.getSelectedIndex();
        int counter = 0;
        for (LanguageResourceEditPanel panel : panels) {
            panel.setVisible(counter == selected);
            counter++;
        }
        
        revalidate();
        repaint();
    }
    
    private void setTopics() {
        Vector<JPanel> vector = new Vector<JPanel>();

        JPanel panelLabels = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelLabels.add(ComponentFactory.getLabel(IconLibrary._icoLabels));
        panelLabels.add(ComponentFactory.getLabel(DcResources.getText("lblLabels")));

        JPanel panelSystemLabels = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSystemLabels.add(ComponentFactory.getLabel(IconLibrary._icoLabels));
        panelSystemLabels.add(ComponentFactory.getLabel(DcResources.getText("lblSystemLabels")));      
        
        JPanel panelTooltips = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTooltips.add(ComponentFactory.getLabel(IconLibrary._icoTooltips));
        panelTooltips.add(ComponentFactory.getLabel(DcResources.getText("lblTooltips")));          
        
        JPanel panelMessages = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMessages.add(ComponentFactory.getLabel(IconLibrary._icoMessages));
        panelMessages.add(ComponentFactory.getLabel(DcResources.getText("lblMessages")));        

        JPanel panelTips = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTips.add(ComponentFactory.getLabel(IconLibrary._icoTips));
        panelTips.add(ComponentFactory.getLabel(DcResources.getText("lblTipsOfTheDay")));        
        
        vector.addElement(panelLabels);
        vector.addElement(panelSystemLabels);
        vector.addElement(panelTooltips);
        vector.addElement(panelMessages);
        vector.addElement(panelTips);
        
        // hack to make the module indices correspond to the model indices
        // (due to the not added Audio Track module)
        JLabel label = new JLabel();
        label.setMaximumSize(new Dimension(50,0));
        label.setPreferredSize(new Dimension(50,0));
        
        topicList.setListData(vector);
    }    
    
    public void clear() {
        topicList = null;
        panels.clear();   
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        
        panels.add(new LanguageResourceEditPanel("lbl"));
        panels.add(new LanguageResourceEditPanel("sys"));
        panels.add(new LanguageResourceEditPanel("tp"));
        panels.add(new LanguageResourceEditPanel("msg"));
        panels.add(new LanguageResourceEditPanel("tip"));

        //**********************************************************
        //Topic List Panel
        //**********************************************************
        JPanel panelTopics = new JPanel();
        topicList.addListSelectionListener(this);
        panelTopics.setLayout(new BorderLayout());

        JLabel label = new JLabel(DcResources.getText("lblTopics"));
        label.setFont(ComponentFactory.getSystemFont());
        label.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel panelHeader = new JPanel(Layout.getGBL());
        panelHeader.setLayout(Layout.getGBL());
        panelHeader.setBorder(BorderFactory.createEtchedBorder());
        panelHeader.add(label, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        
        panelTopics.add(panelHeader, BorderLayout.NORTH);
        panelTopics.add(topicList, BorderLayout.CENTER);

        //**********************************************************
        //Panel Input
        //**********************************************************
        for (LanguageResourceEditPanel panel : panels) {
            add(panel, Layout.getGBC( 1, 0, 1, 1, 100.0, 100.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets( 0, 0, 0, 0), 0, 0));
        }
        
        //**********************************************************
        //Main panel
        //**********************************************************
        add(panelTopics,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,
                 new Insets( 5, 5, 5, 5), 0, 0));

        setTopics();
        topicList.setSelectedIndex(0);        
    }
    
    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        setActiveTopic();
    }    
}
