/* Copyright 2013 Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * The Flux launch configuration tab group (Run configuration... -> Flux).
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs =
				new ILaunchConfigurationTab[] { new FluxMainTab(), new CommonTab() };
		setTabs(tabs);
	}
}
