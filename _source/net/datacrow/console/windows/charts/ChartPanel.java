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

package net.datacrow.console.windows.charts;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.DcThread;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


public class ChartPanel extends JPanel implements ActionListener {
    
    private static Logger logger = Logger.getLogger(ChartPanel.class.getName());
    
    private JComboBox comboFields;
    private JComboBox comboTypes;
    private JButton btnAccept = ComponentFactory.getIconButton(IconLibrary._icoAccept);
    
    private ThreadGroup tg = new ThreadGroup("chart-builders");
    private org.jfree.chart.ChartPanel chartPanel;
    
    private final int module;
    
    public ChartPanel(int module) {
        this.module = module;
        build();
    }
    
    @Override
    public void setEnabled(boolean b) {
        comboFields.setEnabled(b);
        comboTypes.setEnabled(b);
        btnAccept.setEnabled(b);
    }
    
    private void buildChart() {
        DcField field = (DcField) comboFields.getSelectedItem();
        
        if (field == null) return;
        
        if (comboTypes.getSelectedIndex() == 1)
            buildBar(field);
        else
            buildPie(field);
    }
    
    private void deinstall() {
        if (chartPanel != null) {
        	chartPanel.removeAll();
            remove(chartPanel);
            chartPanel = null;
            repaint();
        }
    }
    
    private void install() {
    	add(chartPanel, Layout.getGBC( 0, 1, 2, 1, 40.0, 40.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
     
        setEnabled(true);
        revalidate();
    }
    
    private void buildBar(DcField field) {
        deinstall();
        setEnabled(false);

        new BarChartBuilder(field.getIndex()).start();
    }
    
    private void buildPie(DcField field) {
        deinstall();
        setEnabled(false);

        new PieChartBuilder(field.getIndex()).start();
    }
    
    private Map<String, Integer> getDataMap(DcField field) {
        DcModule mainModule = DcModules.get(field.getModule());
        DcModule referenceModule = DcModules.get(field.getReferenceIdx());
        DcModule mappingModule = DcModules.get(DcModules.getMappingModIdx(module, field.getReferenceIdx(), field.getIndex()));
        
        String sql;
        if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
        	sql = "select sub." + referenceModule.getField(referenceModule.getDisplayFieldIdx()).getDatabaseFieldName() +
        	      ", count(parent.id) from " + mainModule.getTableName() + 
        	      " parent inner join " + referenceModule.getTableName() + " sub on " +
        	      " parent. " + field.getDatabaseFieldName() + " = sub.ID " +
        	      " group by sub." + referenceModule.getField(referenceModule.getDisplayFieldIdx()).getDatabaseFieldName() +
        	      " order by 1";
        } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
        	sql = "select sub." + referenceModule.getField(referenceModule.getDisplayFieldIdx()).getDatabaseFieldName() + 
        	      ", count(parent.id) from " + mainModule.getTableName() + " parent " +
        		  " inner join " + mappingModule.getTableName() + " mapping on " +
        		  " parent. ID = mapping." + mappingModule.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() +
        		  " inner join " + referenceModule.getTableName() + " sub on " +
        		  " mapping." + mappingModule.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName() + " = sub.ID " +
        		  " group by sub." + referenceModule.getField(referenceModule.getDisplayFieldIdx()).getDatabaseFieldName() +
        		  " order by 1";
        } else {
        	sql = "select " + field.getDatabaseFieldName() + ", count(ID) from " + mainModule.getTableName() + 
        	      " group by " + field.getDatabaseFieldName() + 
        	      " order by 1";
        }
        
        // create the data map. exclude zero counts
        Map<String, Integer> dataMap = new LinkedHashMap<String, Integer>();
        
        try {
        	Connector connector = DcConfig.getInstance().getConnector();
	        DcResultSet rs = connector.executeSQL(sql);
	        
	        int count;
	        int total = 0;
	        for (int row = 0; row < rs.getRowCount(); row++) {
	        	count = rs.getInt(row, 1);
	        	total += count;
	        	dataMap.put(rs.getString(row, 0) + " (" + String.valueOf(count) + ")", Integer.valueOf(count));
	        }
	        
	        count = connector.getCount(module, -1, null);
	        if (total < count) 
	        	dataMap.put(DcResources.getText("lblEmpty") + " (" + String.valueOf(count - total) + ")", Integer.valueOf(count - total));
	        
        } catch (Exception se) {
            GUI.getInstance().displayErrorMessage("msgChartCreationError");
            logger.error(DcResources.getText("msgChartCreationError"), se);
        }

        return dataMap.size() > 0 ? dataMap : null;
    }    
    
    public boolean isSupported() {
        return getFields().size() > 0;
    }
    
    private Collection<DcField> getFields() {
        Collection<DcField> fields = new ArrayList<DcField>();
        for (DcField field : DcModules.get(module).getFields()) {
            if (  (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ||
                   field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                   field.getValueType() == DcRepository.ValueTypes._LONG ||
                   field.getValueType() == DcRepository.ValueTypes._BOOLEAN) && 
                   field.isEnabled()) {
                
                fields.add(field);
            }
        }
        return fields;
    }

    private void build() {
        setLayout(Layout.getGBL());
        
        JPanel panel = new JPanel();
        
        comboFields = ComponentFactory.getComboBox();
        comboTypes = ComponentFactory.getComboBox();
        
        panel.add(comboFields);
        panel.add(comboTypes);
        panel.add(btnAccept);
        
        for (DcField field : getFields()) {
        	if (!field.isUiOnly() || field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION)
        		comboFields.addItem(field);
        }
        
        comboTypes.addItem(DcResources.getText("lblPie"));
        comboTypes.addItem(DcResources.getText("lblBar"));
        
        btnAccept.addActionListener(this);
        btnAccept.setActionCommand("buildChart");
        add(   panel, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
              ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
               new Insets(5, 5, 5, 5), 0, 0));
    }   
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (comboFields != null) {
            comboFields.setFont(font);
            comboTypes.setFont(font);
            
            if (chartPanel != null)
            	chartPanel.setFont(font); 
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("buildChart")) {
            buildChart();
        } 
    }

    private class PieChartBuilder extends DcThread {
        
        private final int fieldIdx;
        
        public PieChartBuilder(int field) {
            super(tg, "");
            this.fieldIdx = field;
        }
        
        @Override
        public void run() {
            
            DcField field = DcModules.get(module).getField(fieldIdx);
            
            cancelOthers();
            
            Map<String, Integer> dataMap = getDataMap(field);
            
            if (dataMap == null) {
            	setEnabled(true);
            	return;
            }

            DefaultPieDataset dataset = new DefaultPieDataset();
            int value;
            int total = 0;
            for (String key : dataMap.keySet()) {
            	value = dataMap.get(key).intValue();
            	key = key == null ? DcResources.getText("lblEmpty") : key;
            	dataset.setValue(key, Integer.valueOf(value));
            	total += value;
            }
            
            Connector connector = DcConfig.getInstance().getConnector();
            int all = connector.getCount(module, -1, null);
            if (total < all)
            	dataset.setValue(DcResources.getText("lblEmpty") + " (" + String.valueOf(all - total) + ")" , Integer.valueOf(all - total));
            
            if (!isCanceled()) {
            	JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
            	chartPanel = new org.jfree.chart.ChartPanel(chart);
            	chartPanel.setFont(ComponentFactory.getStandardFont());

            	try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            install();
                        };
                    });
                } catch (Exception e) {
                    logger.error(e, e);
                }                    
            }
        }
    }
    
    private class BarChartBuilder extends DcThread {
        
        private final int fieldIdx;
        
        public BarChartBuilder(int field) {
            super(tg, "");
            this.fieldIdx = field;
        }
        
        @Override
        public void run() {
            
            cancelOthers();
            
            Map<String, Integer> dataMap = getDataMap(DcModules.get(module).getField(fieldIdx));
            
            if (dataMap == null) return;
            
            DcField field = DcModules.get(module).getField(fieldIdx);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            int total = 0;
            int value;
            for (String key : dataMap.keySet()) {
            	value = dataMap.get(key).intValue();
      	    	key = key == null ? DcResources.getText("lblEmpty") : key;
       	        dataset.addValue(value, key, field.getLabel());
       	        total += value;
            }
            
            Connector connector = DcConfig.getInstance().getConnector();
            int all = connector.getCount(module, -1, null);
            if (total < all)
            	dataset.addValue(all - total, DcResources.getText("lblEmpty"), field.getLabel());
            
            if (!isCanceled()) {
                JFreeChart chart = ChartFactory.createBarChart(
                		null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
            	chartPanel = new org.jfree.chart.ChartPanel(chart);
            	chartPanel.setFont(ComponentFactory.getStandardFont());
            	
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        install();
                    };
                });
            }
        }
    } 
}
