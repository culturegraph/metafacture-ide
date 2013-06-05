/* Copyright 2013 Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.launching;

import java.util.ArrayList;
import java.util.List;

import org.culturegraph.mf.ide.launching.FluxLaunchConfigurationDelegate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;

/**
 * A launch shortcut to run Flux files (via Run -> Run As...).
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		launchFlow((IFile) element, mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		FileEditorInput fei = (FileEditorInput) input;
		launchFlow(fei.getFile(), mode);
	}

	private static void launchFlow(IFile file, String mode) {
		System.out.println("Launching flow: " + file + " in mode " + mode);
		try {
			ILaunchConfigurationType launchConfigType = getLaunchConfigType();
			ILaunchConfiguration[] configs =
					DebugPlugin.getDefault().getLaunchManager()
							.getLaunchConfigurations(launchConfigType);
			List<ILaunchConfiguration> configList =
					collectLaunchConfigs(file, configs);
			ILaunchConfiguration config =
					selectLaunchConfig(file, launchConfigType, configList);
			config.launch(mode, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private static ILaunchConfiguration selectLaunchConfig(IFile file,
			ILaunchConfigurationType launchConfigType,
			List<ILaunchConfiguration> configList) throws CoreException {
		ILaunchConfiguration config = null;
		if (configList.size() == 1) {
			config = configList.get(0);
		} else if (configList.size() == 0) {
			ILaunchConfigurationWorkingCopy wc =
					launchConfigType.newInstance(null,
							DebugPlugin.getDefault().getLaunchManager()
									.generateLaunchConfigurationName(file.getName()));
			wc.setAttribute(FluxLaunchConfigurationDelegate.FILE_NAME, file
					.getFullPath().toOSString());
			config = wc.doSave();
		} else {
			ILaunchConfiguration chosen = chooseConfiguration(configList);
			config = chosen;
		}
		return config;
	}

	private static List<ILaunchConfiguration> collectLaunchConfigs(IFile file,
			ILaunchConfiguration[] configs) throws CoreException {
		List<ILaunchConfiguration> configList =
				new ArrayList<ILaunchConfiguration>();
		for (ILaunchConfiguration launchConfiguration : configs) {
			if (launchConfiguration.getAttribute(
					FluxLaunchConfigurationDelegate.FILE_NAME, "").equals(
					file.getFullPath().toOSString())) {
				configList.add(launchConfiguration);
			}
		}
		return configList;
	}

	private static ILaunchConfigurationType getLaunchConfigType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType("org.culturegraph.mf.ide.launching");
	}

	private static ILaunchConfiguration chooseConfiguration(List<?> configList) {
		IDebugModelPresentation labelProvider =
				DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog =
				new ElementListSelectionDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Select Launch Configuration");
		dialog.setMessage("Select Launch Configuration");
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

}
