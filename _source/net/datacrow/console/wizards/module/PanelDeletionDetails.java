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

package net.datacrow.console.wizards.module;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.StringReader;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class PanelDeletionDetails extends ModuleWizardPanel {

    private static Logger logger = Logger.getLogger(PanelDeletionDetails.class.getName());
    
    private JTextPane details = ComponentFactory.getTextPane();
    private HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument document = new HTMLDocument();
    
    public PanelDeletionDetails(Wizard wizard) {
        super(wizard);
        
        details.setEditorKit(kit);
        details.setDocument(document);
        details.setEditable(false);
        details.setBounds(1,1,1,10);
        
        build();
    }
    
    @Override
    public void setModule(XmlModule module) {
        super.setModule(module);
        setDetails(module);
    }   
    
    private void setDetails(XmlModule module) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><body ");
        sb.append(Utilities.getHtmlStyle());
        sb.append(">");
        
        sb.append("<table>");
        sb.append("<tr><td>");
        sb.append(DcResources.getText("msgReferencedModulesDelete"));
        sb.append("</td>");
        
        for (DcModule reference : DcModules.getReferencingModules(module.getIndex())) {
            sb.append("<tr><td>");
            sb.append("<b>");
            sb.append(reference.toString());
            sb.append("</b>");
            sb.append("</td></tr>");
        }
        
        sb.append("</table>");
        sb.append("</body></html>");
        
        try {
            StringReader sr = new StringReader(sb.toString());
            details.read(sr, "datacrow");
            sr.close();
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgModuleDeletionDetails");
    }

    @Override
    public void destroy() {
        details = null;
        kit = null;
        document = null;
    }

    @Override
    public Object apply() {
        return getModule();
    }
    
    private void build() {
        JScrollPane sp = new JScrollPane(details);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        setLayout(Layout.getGBL());
        add(sp, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 5, 5, 5, 5), 0, 0));
    }
}
