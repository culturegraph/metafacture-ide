/* Copyright 2013 Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.launching;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.widgets.Composite;

/**
 * The main tab for the Flux launch configuration.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxMainTab extends AbstractLaunchConfigurationTab {

	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public void createControl(Composite parent) {
		// TODO implement control to set the Flux file to run
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		// TODO set Flux file to run from configuration
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO apply changes to the Flux file name
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO is there any sensible default here?
	}

}
