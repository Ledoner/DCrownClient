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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcIconSelectField;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcAssociateModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

public class PanelBasicInfo extends ModuleWizardPanel {

    private static Logger logger = Logger.getLogger(PanelBasicInfo.class.getName());
    
    private DcIconSelectField pic32;
    private DcIconSelectField pic16;
    
    private DcLongTextField textDesc;
    private DcShortTextField textLabel;
    private DcShortTextField textObjectName;
    private DcShortTextField textObjectNamePlural;
    private DcCheckBox checkCanBeLended;
    private DcCheckBox checkContainerManaged;
    private DcCheckBox checkFileBacked;
    
    private boolean exists;
    
    private String moduleName;
    
    public PanelBasicInfo(Wizard wizard, boolean exists) {
        super(wizard);
        this.exists = exists;
        build();
    }
    
    @Override
    public void setModule(XmlModule module) {
        super.setModule(module);
        
        moduleName = module.getName();
        
        textDesc.setText(module.getDescription());
        textLabel.setText(module.getLabel());
        textObjectName.setText(module.getObjectName());
        textObjectNamePlural.setText(module.getObjectNamePlural());
        checkCanBeLended.setSelected(module.canBeLend());
        checkFileBacked.setSelected(module.isFileBacked());
        checkContainerManaged.setSelected(module.isContainerManaged());
        
        if (module.getIcon16() != null)
            pic16.setIcon(new DcImageIcon(module.getIcon16()));
        
        if (module.getIcon32() != null)
            pic32.setIcon(new DcImageIcon(module.getIcon32()));
        
        boolean simpleMod = getModule().getModuleClass().equals(DcPropertyModule.class) ||     
                                 getModule().getModuleClass().equals(DcAssociateModule.class);
        
        checkFileBacked.setVisible(!simpleMod);
        checkCanBeLended.setVisible(!simpleMod);
        checkContainerManaged.setVisible(!simpleMod);
        
        if (simpleMod) {
            checkFileBacked.setSelected(false);
            checkCanBeLended.setSelected(false);
            checkContainerManaged.setSelected(false);
        }
    }
    
    private String saveIcon(DcImageIcon icon, String suffix) throws WizardException {
        XmlModule module = getModule();
        
        File file = null;
        
        try {
            file = File.createTempFile("module_" + Utilities.toFilename(module.getName()) + suffix, ".png");
            byte[] bytes = icon.getBytes();
            
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.flush();
            bos.close();
       } catch (Exception e) {
            throw new WizardException("Error while saving icon " + (file != null ? file.toString() : ""));
       }
       
       return (file != null ? file.toString() : null);
    }
    
    @Override
    public Object apply() throws WizardException {
        XmlModule module = getModule();
        
        String label = textLabel.getText();
        String objectName = textObjectName.getText();
        checkValue(label, DcResources.getText("lblName"));
        
        checkValue(pic16.getIcon(), DcResources.getText("lblIcon"));
        checkValue(pic32.getIcon(), DcResources.getText("lblIcon"));
        checkValue(textObjectName.getText(), DcResources.getText("lblItemName"));
        checkValue(textObjectNamePlural.getText(), DcResources.getText("lblItemNamePlural"));

        String tableName = 
        		!(getWizard() instanceof CreateModuleWizard) && 
                !CoreUtilities.isEmpty(module.getTableName())?
                 module.getTableName() : 
                 CoreUtilities.getDatabaseTableName();
                                
        if (getWizard() instanceof CreateModuleWizard && DcModules.get(label) != null)
        	throw new WizardException(DcResources.getText("msgModuleNameNotUnique"));

        if (getWizard() instanceof CreateModuleWizard) {
            module.setName(label);
        } else {
            module.setName(moduleName);
        }
        
        module.setDescription(textDesc.getText());
        module.setEnabled(true);
        module.setObjectName(objectName);
        module.setObjectNamePlural(textObjectNamePlural.getText());
        module.setTableName(tableName);
        module.setTableNameShort(tableName);
        module.setLabel(label);
        module.setCanBeLend(checkCanBeLended.isSelected());
        module.setContainerManaged(checkContainerManaged.isSelected());
        module.setFileBacked(checkFileBacked.isSelected());
        module.setHasInsertView(true);
        module.setHasSearchView(true);

        ImageIcon icon16 = pic16.getIcon();
        ImageIcon icon32 = pic32.getIcon();

        try {
            if (pic16.isChanged() || getWizard() instanceof CreateModuleWizard) {
                module.setIcon16(CoreUtilities.getBytes(icon16.getImage(), DcImageIcon._TYPE_PNG));
                module.setIcon16Filename(saveIcon(new DcImageIcon(module.getIcon16()), "_small"));
            }
            
            if (pic32.isChanged() || getWizard() instanceof CreateModuleWizard) {
                module.setIcon32(CoreUtilities.getBytes(icon32.getImage(), DcImageIcon._TYPE_PNG));
                module.setIcon32Filename(saveIcon(new DcImageIcon(module.getIcon32()), ""));                
            }
        } catch (Exception e) {
        	logger.error("Error while reading the icons", e);
        	throw new WizardException("Could not store / use the selected icons");
        }

        return module;
    }
    
    private void checkValue(Object o, String desc) throws WizardException { 
        if (o == null || o.toString().trim().length() == 0)
            throw new WizardException(DcResources.getText("msgXNotEntered", desc));
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgBasicModuleInfo");
    }
    
    @Override
    public void destroy() {
        pic32 = null;
        pic16 = null;
        textDesc = null;
        textLabel = null;
        textObjectName = null;
        textObjectNamePlural = null;
        checkCanBeLended = null;
    }    

    @Override
    public void onActivation() {
        if (getModule() != null && getModule().getModuleClass() != null) {
            if (    getModule().getModuleClass().equals(DcPropertyModule.class) ||
                    getModule().getModuleClass().equals(DcAssociateModule.class)) {
                checkCanBeLended.setSelected(false);
                checkCanBeLended.setVisible(false);
                checkContainerManaged.setSelected(false);
                checkContainerManaged.setVisible(false);
                checkFileBacked.setSelected(false);
                checkFileBacked.setVisible(false);
            } else {
                checkCanBeLended.setVisible(true);
                checkContainerManaged.setVisible(true);
                checkFileBacked.setVisible(true);
            }
        }
        
        revalidate();
        repaint();
        
        super.onActivation();
    }

    private void build() {
        // info panel
        setLayout(Layout.getGBL());
        
        textDesc = ComponentFactory.getLongTextField();
        JScrollPane scollDesc = new JScrollPane(textDesc);
        scollDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        textLabel = ComponentFactory.getShortTextField(25);
        textObjectName = ComponentFactory.getShortTextField(25);
        checkCanBeLended = ComponentFactory.getCheckBox(DcResources.getText("lblCanBeLended"));
        checkContainerManaged = ComponentFactory.getCheckBox(DcResources.getText("lblContainerManaged"));
        checkFileBacked = ComponentFactory.getCheckBox(DcResources.getText("lblFileBacked"));
        textObjectNamePlural = ComponentFactory.getShortTextField(25);
        
        int y = 0;
        add(ComponentFactory.getLabel(DcResources.getText("lblDescription")), 
                Layout.getGBC(0, y, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));  
        add(scollDesc,        
                Layout.getGBC(1, y++, 1, 1, 2.0, 2.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 5, 5, 5, 5), 0, 0));  
        
        
        if (!exists) {
            add(ComponentFactory.getLabel(DcResources.getText("lblName")), 
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 5, 5, 5, 5), 0, 0));
            add(textLabel,         
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets( 5, 5, 5, 5), 0, 0));
            add(ComponentFactory.getLabel(DcResources.getText("lblItemName")), 
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 5, 5, 5, 5), 0, 0));  
            add(textObjectName,         
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets( 5, 5, 5, 5), 0, 0));
            add(ComponentFactory.getLabel(DcResources.getText("lblItemNamePlural")), 
                    Layout.getGBC(0, y, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 5, 5, 5, 5), 0, 0));  
            add(textObjectNamePlural,         
                    Layout.getGBC(1, y++, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    new Insets( 5, 5, 5, 5), 0, 0));
        }
        
        add(checkContainerManaged, 
                Layout.getGBC(1, y++, 2, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));
        add(checkFileBacked, 
                Layout.getGBC(1, y++, 2, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));        
        add(checkCanBeLended, 
                Layout.getGBC(1, y++, 2, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5), 0, 0));

        pic16 = ComponentFactory.getIconSelectField(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons/icon16.png"));
        pic32 = ComponentFactory.getIconSelectField(new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons/icon32.png"));
            
        add(ComponentFactory.getLabel(DcResources.getText("lblIcon16")), 
                     Layout.getGBC(0, y, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                     new Insets(5, 5, 5, 5), 0, 0)); 
        add(pic16,   Layout.getGBC(1, y++, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0)); 
        add(ComponentFactory.getLabel(DcResources.getText("lblIcon32")), 
                     Layout.getGBC(0, y, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                     new Insets(5, 5, 5, 5), 0, 0)); 
        add(pic32,   Layout.getGBC(1, y++, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0)); 
    }
}
