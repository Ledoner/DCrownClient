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

package net.datacrow.console.wizards.itemimport;

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class ItemImporterWizard extends Wizard {

    public static final int _STEP_MAPPING = 2;
    
	private ItemImporterDefinition definition;
	
	public ItemImporterWizard(int moduleIdx) {
		super(moduleIdx);
		
		setTitle(getWizardName());
		setHelpIndex("dc.migration.wizard.importer");
		
		this.definition = new ItemImporterDefinition();
		setSize(DcSettings.getDimension(DcRepository.Settings.stItemImporterWizardFormSize));
		setCenteredLocation();
	}
	
	protected ItemImporterDefinition getDefinition() {
		return definition;
	}

	@Override
    protected boolean isRestartSupported() {
	    return false;
    }
	
    @Override
    public void finish() throws WizardException {
        if (definition != null && definition.getImporter() != null)
            definition.getImporter().cancel();

        if (!isCancelled()) {
            DcModule m = getModule();
            IMasterView view = GUI.getInstance().getSearchView(m.getIndex());
            
            if (view != null) view.refresh();
        }
        
        definition = null;
        close();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("lblItemImportWizard");
    }
    
    @Override
    public void next() throws WizardException {
        if (getDefinition() != null && getDefinition().getImporter() != null) {
            if (!getDefinition().getImporter().requiresMapping()) {
                if (!skip.contains(Integer.valueOf(_STEP_MAPPING)))
                    skip.add(Integer.valueOf(_STEP_MAPPING));
            } else {
                while (skip.contains(Integer.valueOf(_STEP_MAPPING)))
                    skip.remove(Integer.valueOf(_STEP_MAPPING));
            }
        }
        
        super.next();
    }

    @Override
    protected List<IWizardPanel> getWizardPanels() {
    	List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
    	panels.add(new ItemImporterSelectionPanel(this));
    	panels.add(new ItemImporterDefinitionPanel(this));
    	panels.add(new ItemImporterMappingPanel(this));
    	panels.add(new ItemImporterTaskPanel(this));
    	return panels;
    }

    @Override
    protected void initialize() {}

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stItemImporterWizardFormSize, getSize());
    }
}
