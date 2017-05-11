/* Copyright 2013 Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.launching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.culturegraph.mf.runner.Flux;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Launch Flux files in the workspace by passing them to {@link Flux}.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxLaunchConfigurationDelegate
		implements ILaunchConfigurationDelegate {

	private static final String BUNDLE = "org.culturegraph.mf.ide";
	private static final ILog LOG = Platform.getLog(Platform.getBundle(BUNDLE));
	/** The key to use for the name of the Flux file to launch. */
	public static final String FILE_NAME = "filename";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		System.out.println(
				"Launching... config attributes: " + configuration.getAttributes());
		monitor.beginTask("Flux Workflow", 10);
		final String file = configuration.getAttribute(FILE_NAME, "");
		IResource member =
				ResourcesPlugin.getWorkspace().getRoot().findMember(file);
		monitor.worked(3);
		monitor.subTask("Running Workflow");
		runWorkflow(monitor, member);
		monitor.subTask("Refreshing Workspace");
		member.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		monitor.done();
	}

	private void runWorkflow(IProgressMonitor monitor, IResource member) {
		if (!monitor.isCanceled()) {
			File fluxFile = new File(member.getLocationURI());
			LOG.log(new Status(IStatus.INFO, BUNDLE, "Running file: " + fluxFile));
			try {
				String fluxWithAbsolutePaths =
						resolveDotInPaths(fluxFile.getAbsolutePath(), fluxFile.getParent());
				LOG.log(new Status(IStatus.INFO, BUNDLE,
						"Resolved file: " + fluxWithAbsolutePaths));
				Flux.main(new String[] { fluxWithAbsolutePaths });
			} catch (Exception e) {
				e.printStackTrace();
				LOG.log(new Status(IStatus.ERROR, BUNDLE, e.getMessage(), e));
				Throwable rootCause = findRootCause(e);
				MessageDialog.openError(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Workflow Error",
						rootCause.getMessage() + " (see error log for details).");
			}
		}
		monitor.worked(7);
	}

	private static String resolveDotInPaths(String fluxFileAbsolutePath,
			String parent) throws IOException {
		final String originalContent = read(fluxFileAbsolutePath);
		String resolvedContent = originalContent
				/* just a dot, in a var: "." or "./" */
				.replaceAll("\"\\./?\"", "\"" + parent + "/\"")
				/* leading dot in a path: "./etc" */
				.replaceAll("\"\\./", "\"" + parent + "/")
				/* somewhat odd case, but supported by Metaflow: */
				.replace("file://./", "file://" + parent + "/");
		return originalContent.equals(resolvedContent) ? fluxFileAbsolutePath
				: write(resolvedContent).getAbsolutePath();
	}

	private static File write(String content) throws IOException {
		File resolvedFile = File.createTempFile("metafacture-ide", ".flux");
		resolvedFile.deleteOnExit();
		FileWriter writer = new FileWriter(resolvedFile);
		try {
			writer.write(content);
		} finally {
			writer.close();
		}
		return resolvedFile;
	}

	private static String read(String flow) throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(new File(flow));
		try {
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine()).append("\n");
			}
		} finally {
			scanner.close();
		}
		return builder.toString();
	}

	private Throwable findRootCause(Throwable t) {
		return t.getCause() == null ? t : findRootCause(t.getCause());
	}

}
