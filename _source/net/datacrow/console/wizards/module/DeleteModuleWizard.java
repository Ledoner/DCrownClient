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
import java.util.Collection;
import java.util.List;

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcMediaModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.ModuleJar;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class DeleteModuleWizard extends Wizard {

    private static Logger logger = Logger.getLogger(DeleteModuleWizard.class.getName());
    
    public DeleteModuleWizard() {
        super();
        setTitle(DcResources.getText("lblModuleDeleteWizard"));
        setHelpIndex("dc.modules.delete");

        setSize(DcSettings.getDimension(DcRepository.Settings.stModuleWizardFormSize));
        setCenteredLocation();
    }
    
    @Override
    protected void initialize() {}
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new PanelSelectModuleToDelete(this));
        panels.add(new PanelDeletionDetails(this));
        return panels;
    }
    
    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stModuleWizardFormSize, getSize());
    }

    @Override
    public void finish() throws WizardException {
        
        if (!GUI.getInstance().displayQuestion("msgDeleteModuleConfirmation"))
            return;
        
        XmlModule xmlModule = (XmlModule) getCurrent().apply();
        DcModule module = DcModules.get(xmlModule.getIndex()) == null ?
                          DcModules.getPropertyBaseModule(xmlModule.getIndex()) : 
                          DcModules.get(xmlModule.getIndex());
                          
        try {
            Connector connector = DcConfig.getInstance().getConnector();
            connector.deleteModule(module.getIndex());

            // update the referencing modules; remove fields holding a reference to this module
            Collection<XmlField> remove = new ArrayList<XmlField>();
            for (DcModule reference : DcModules.getReferencingModules(xmlModule.getIndex())) {
                if (reference != null && reference.getXmlModule() != null) {
                    for (XmlField field : reference.getXmlModule().getFields()){
                        if (reference.getField(field.getIndex()).getSourceModuleIdx() == xmlModule.getIndex())
                            remove.add(field);
                    }
                    reference.getXmlModule().getFields().removeAll(remove);
                    new ModuleJar(reference.getXmlModule()).save();
                }
            }
            
            // parent child relation should be undone
            undoParentChildRelation(module.getParent() != null ? module.getParent() : module.getChild());
            
            // make sure the 'hasDependingModules' property is corrected for modules holding
            // a referencing to this module
            DcModule reference;
            for (DcField field : module.getFields()) {
                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    reference = DcModules.getReferencedModule(field);
                    
                    if (reference.getXmlModule() == null) continue;

                    if (reference.hasDependingModules() && DcModules.getReferencingModules(reference.getIndex()).size() == 1)
                        reference.getXmlModule().setHasDependingModules(false);
                    
                    new ModuleJar(reference.getXmlModule()).save();
                }
            }
            
            // not needed for property base modules
            if (DcModules.get(xmlModule.getIndex()) != null) {
                DcModules.get(xmlModule.getIndex()).isEnabled(false);
                GUI.getInstance().getMainFrame().applySettings(false);
            }
            
            close();
        } catch (Exception e) {
            logger.error(e, e);
            throw new WizardException(DcResources.getText("msgCouldNotDeleteModule", e.getMessage()));
        }
    }
    
    private void undoParentChildRelation(DcModule module) throws Exception {
        if (module == null) return;
        
        if (module.getType() == DcModule._TYPE_MEDIA_MODULE) {
            module.getXmlModule().setModuleClass(DcMediaModule.class);
            module.getXmlModule().setObject(DcMediaObject.class);
        } else {
            module.getXmlModule().setModuleClass(DcModule.class);
            module.getXmlModule().setObject(DcObject.class);
        }
        
        module.getXmlModule().setChildIndex(-1);
        module.getXmlModule().setParentIndex(-1);
        
        new ModuleJar(module.getXmlModule()).save();
    }

    @Override
    public void next() {
        try {
            XmlModule module = (XmlModule) getCurrent().apply();

            if (module == null)
                return;
            
            current += 1;
            if (current <= getStepCount()) {
                ModuleWizardPanel panel;
                for (int i = 0; i < getStepCount(); i++) {
                    panel = (ModuleWizardPanel) getWizardPanel(i);
                    panel.setModule(module);
                    panel.setVisible(i == current);
                }
            } else {
                current -= 1;
            }

            applyPanel();
        } catch (WizardException wzexp) {
            if (wzexp.getMessage().length() > 1)
                GUI.getInstance().displayWarningMessage(wzexp.getMessage());
        }
    }

    @Override
    public void close() {
        if (!isCancelled() && !isRestarted())
            new RestartDataCrowDialog(this);
        
        super.close();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("msgModuleWizard",
                                   new String[] {String.valueOf(current + 1), String.valueOf(getStepCount())});
    }

    @Override
    protected void restart() {
        try {
            finish();
            saveSettings();
            DeleteModuleWizard wizard = new DeleteModuleWizard();
            wizard.setVisible(true);
        } catch (WizardException exp) {
            GUI.getInstance().displayWarningMessage(exp.getMessage());
        }
    }
}
