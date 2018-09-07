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

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.modules.ModuleJar;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.resources.DcResources;

public class CopyModuleWizard extends CreateModuleWizard {

    public CopyModuleWizard() {
        super();
        
        setTitle(DcResources.getText("lblModuleCopyWizard"));
        setHelpIndex("dc.modules.copy");
    }
    
    @Override
    protected boolean isRestartSupported() {
        return false;
    }

    @Override
    public void finish() throws WizardException {
        XmlModule module = (XmlModule) getCurrent().apply();
        module.setIndex(DcModules.getAvailableIdx(module));
        module.setProductVersion(DcConfig.getInstance().getVersion().getFullString());

        try {
            if (module.getModuleClass().equals(DcPropertyModule.class))
                module.setServingMultipleModules(true);
            
            new ModuleJar(module).save();
            
            for (XmlField field : module.getFields()) {
                
                field.setModule(module.getIndex());
                
                if (field.getModuleReference() !=  0 && field.getModuleReference() != module.getIndex()) {
                    DcModule m = DcModules.get(field.getModuleReference()) == null ? 
                                 DcModules.get(field.getModuleReference() + module.getIndex()) : 
                                 DcModules.get(field.getModuleReference());
                    
                    if (m != null && m.getXmlModule() != null)
                        new ModuleJar(m.getXmlModule()).save();
                }  else {
                    field.setModuleReference(module.getIndex());
                }
            }
            
            module.setServingMultipleModules(true);

            DcModules.register(DcModules.convert(module));
            DcModules.registerPropertyModules(DcModules.convert(module));
            
            close();
        } catch (Exception e) {
            throw new WizardException(DcResources.getText("msgCouldNotWriteModuleFile", e.getMessage()));
        }     
    }

    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new PanelSelectModuleTemplate(this));
        panels.add(new PanelBasicInfo(this, false));
        panels.add(new PanelFields(this, false));
        return panels;
    }
}
