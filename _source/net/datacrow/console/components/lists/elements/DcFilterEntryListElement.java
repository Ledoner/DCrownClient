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

package net.datacrow.console.components.lists.elements;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;

public class DcFilterEntryListElement extends DcListElement {
    
    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private static final Dimension dim = new Dimension(600, 30);
    
    private DataFilterEntry entry;
    
    public DcFilterEntryListElement(DataFilterEntry entry) {
        this.entry = entry;
        
        setPreferredSize(dim);
        setMinimumSize(dim);
        
        build();
    }

    public DataFilterEntry getEntry() {
        return entry;
    }
    
    @Override
    public void build() {
        setLayout(layout);
  
        DcModule module = DcModules.get(entry.getModule());
        DcField field = module.getField(entry.getField());
        Operator operator = entry.getOperator();
        
        
        JLabel labelAnd = ComponentFactory.getLabel(entry.getAndOr());
        JLabel labelMod = ComponentFactory.getLabel(module.getLabel()); 
        JLabel labelFld = ComponentFactory.getLabel(field.getLabel());
        JLabel labelOp = ComponentFactory.getLabel(operator.toString());
        JLabel labelVal = ComponentFactory.getLabel(
                entry.getValue() != null ? entry.getValue().toString() : "");
        
        labelAnd.setPreferredSize(new Dimension(30, 25));
        labelMod.setPreferredSize(new Dimension(100, 25));
        labelFld.setPreferredSize(new Dimension(120, 25));
        labelOp.setPreferredSize(new Dimension(100, 25));
        labelVal.setPreferredSize(new Dimension(200, 25));
        
        Font font = labelFld.getFont();
        Font fontBold = new Font(font.getFamily(), Font.BOLD, font.getSize());
        Font fontCurs = new Font(font.getFamily(), Font.ITALIC, font.getSize());
        
        labelAnd.setFont(fontBold);
        labelFld.setFont(fontBold);
        labelMod.setFont(fontCurs);
        labelVal.setFont(fontBold);
        
        add(labelAnd);
        add(labelMod);
        add(labelFld);
        add(labelOp);
        add(labelVal);
    }
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		entry = null;
	}
} 
