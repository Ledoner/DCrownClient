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
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.resources.DcResources;

public class DcFontSelector extends JComponent implements IComponent, ActionListener {
    
    private JComboBox comboFontName = ComponentFactory.getComboBox();
    private JComboBox comboFontSize = ComponentFactory.getComboBox();
    private DcLongTextField textField = ComponentFactory.getLongTextField();
    private JComboBox comboFontStyle = ComponentFactory.getComboBox();

    private JLabel labelFont = ComponentFactory.getLabel(DcResources.getText("lblFont"));
    private JLabel labelFontSize = ComponentFactory.getLabel(DcResources.getText("lblFontSize"));
    private JLabel labelExample = ComponentFactory.getLabel(DcResources.getText("lblPreview"));
    private JLabel labelFontStyle = ComponentFactory.getLabel(DcResources.getText("lblFontStyle"));
    
    /**
     * Initializes this field
     */
    public DcFontSelector() {
        buildComponent();
        fillFontNameCombo();
        fillFontSizeCombo();
    }
    
    @Override
    public void setFont(Font font) {
        comboFontName.setFont(ComponentFactory.getStandardFont());
        comboFontSize.setFont(ComponentFactory.getStandardFont());
        comboFontStyle.setFont(ComponentFactory.getStandardFont());
        
        labelExample.setFont(ComponentFactory.getSystemFont());
        labelFontSize.setFont(ComponentFactory.getSystemFont());
        labelFont.setFont(ComponentFactory.getSystemFont());
        labelFontStyle.setFont(ComponentFactory.getSystemFont());
    }
    
    /**
     * Fills the fonts combo box with values retrieved form the OS
     */
    private void fillFontNameCombo() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().
                       getAllFonts();
        
        for (int i = 0; i < fonts.length; i++) {
            comboFontName.addItem(fonts[i].getName());
        }
    }

    /**
     * Fills the font size combo
     */
    private void fillFontSizeCombo() {
        comboFontSize.addItem(Integer.valueOf(8));
        comboFontSize.addItem(Integer.valueOf(9));
        comboFontSize.addItem(Integer.valueOf(10));
        comboFontSize.addItem(Integer.valueOf(11));
        comboFontSize.addItem(Integer.valueOf(12));
        comboFontSize.addItem(Integer.valueOf(13));
        comboFontSize.addItem(Integer.valueOf(14));
        comboFontSize.addItem(Integer.valueOf(15));
        comboFontSize.addItem(Integer.valueOf(16));
        comboFontSize.addItem(Integer.valueOf(17));
        comboFontSize.addItem(Integer.valueOf(18));
    }
    
    @Override
    public void clear() {
        comboFontName = null;
        comboFontSize = null;
        textField = null;
        comboFontStyle = null;
        labelFont = null;
        labelFontSize = null;
        labelExample = null;
        labelFontStyle = null;        
    }
    
    /**
     * Returns the selected Font (with the chosen size, thickness)
     * Unless the user has chosen otherwise, Arial font size 11 is returned.
     */
    @Override
    public Object getValue() {
        return getFont();
    }
    
    /**
     * Returns the selected Font (with the chosen size, thickness).
     * Unless the user has chosen otherwise, Arial font size 11 is returned.  
     */    
    @Override
    public Font getFont() {
    	Font font = new JTextField().getFont();
        if (comboFontName.getSelectedIndex() != -1) {
            String fontName = comboFontName.getSelectedItem().toString();
            
            int fontSize = 11;
            try {
            	fontSize= ((Integer) comboFontSize.getSelectedItem()).intValue();
            } catch (Exception exp) {}

            FontStyle style = (FontStyle) comboFontStyle.getSelectedItem();
            font = new Font(fontName, style.getIndex(), fontSize);
        } 
        return font;
    }
    
    private void fillFontStyleCombo() {
        comboFontStyle.addItem(new FontStyle(0, "Plain"));
        comboFontStyle.addItem(new FontStyle(1, "Bold"));
        comboFontStyle.addItem(new FontStyle(2, "Italic"));
    }
    
    /**
     * Applies a value to this field
     */
    @Override
    public void setValue(Object o) {
        Font font = (Font) o;
        try {
            if (font.isBold())
                comboFontStyle.setSelectedIndex(1);
            else if (font.isItalic())
                comboFontStyle.setSelectedIndex(2);
            else
                comboFontStyle.setSelectedIndex(0);
            
            String name = font.getName();
            String value;
            for (int i = 0; i < comboFontName.getItemCount(); i++) {
                value = (String) comboFontName.getItemAt(i);
                if (value.toLowerCase().equals(name.toLowerCase())) 
                    comboFontName.setSelectedIndex(i);
            }
            
            // default back to arial if not found
            if (comboFontName.getSelectedIndex() == -1) {
                for (int i = 0; i < comboFontName.getItemCount(); i++) {
                    value = (String) comboFontName.getItemAt(i);
                    if (value.toLowerCase().equals("arial")) 
                        comboFontName.setSelectedIndex(i);
                }
            }
            
            int size = font.getSize() == 0 ? 11 : font.getSize();
        	comboFontName.setSelectedItem(font.getName());
            comboFontSize.setSelectedItem(Integer.valueOf(size));
        } catch (Exception ignore) {}
    }
    
    /**
     * Applies the selected values on a sample text
     */
    private void setSampleText() {
        textField.setFont(getFont());
    }
    
    private void applySystemFont() {
        Font font = new JTextField().getFont();
        setValue(new Font(font.getName(), font.getSize(), Font.PLAIN));
    }

    @Override
    public void setEditable(boolean b) {
        comboFontName.setEditable(b);
        comboFontSize.setEditable(b);
        textField.setEditable(b);
        comboFontStyle.setEditable(b);
    }
    
    /**
     * Builds this component
     */
    private void buildComponent() {
        setLayout(Layout.getGBL());
        fillFontStyleCombo();
        
        comboFontName.addActionListener(this);
        comboFontName.setActionCommand("showSampleText");
        comboFontSize.addActionListener(this);
        comboFontSize.setActionCommand("showSampleText");
        comboFontStyle.addActionListener(this);
        comboFontStyle.setActionCommand("showSampleText");
        
        textField.setBorder(ComponentFactory.getTitleBorder(""));
        textField.setText("data crow DATA CROW data crow DATA CROW data crow DATA CROW data crow DATA CROW" +
                          "data crow DATA CROW data crow DATA CROW data crow DATA CROW data crow DATA CROW \n" + 
                          "01234561789 ^&@*(_!+~\\||[];");
        
        JScrollPane scrollPane = new JScrollPane(textField);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        add(labelFont,       Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
						     new Insets( 0, 0, 0, 5), 0, 0));
        add(comboFontName,   Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
    		                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
							 new Insets( 0, 0, 0, 0), 0, 0));
        add(labelFontSize,   Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets( 0, 0, 0, 5), 0, 0));
        add(comboFontSize,   Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
        		            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
						     new Insets( 0, 0, 0, 0), 0, 0));
        add(labelFontStyle,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets( 0, 0, 0, 5), 0, 0));
        add(comboFontStyle,  Layout.getGBC( 1, 2, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
							 new Insets( 0, 0, 0, 0), 0, 0));
        add(labelExample,    Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets( 0, 0, 0, 5), 0, 0));
        add(scrollPane,      Layout.getGBC( 1, 3, 3, 1, 2.0, 2.0
        		            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
							 new Insets( 0, 0, 0, 0), 0, 0));
    }
    
    private static class FontStyle {
        final int style;
        final String name;
        
        public FontStyle(int index, String name) {
            this.style = index;
            this.name = name;
        }
        
        public int getIndex() {
            return style;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("applyDefault")) {
            applySystemFont();
            setSampleText();
        } else if (e.getActionCommand().equals("showSampleText")) {
            setSampleText();
        }
    }
    
    @Override
    public void refresh() {}
}
