/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.culturegraph.mf.framework.annotations.Description;
import org.culturegraph.mf.framework.annotations.In;
import org.culturegraph.mf.framework.annotations.Out;

import com.google.common.io.Closeables;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxRuntimeModule extends
		org.culturegraph.mf.ide.AbstractFluxRuntimeModule {
	// see http://www.eclipse.org/Xtext/documentation.html#dependencyInjection

	/**
	 * @return A mapping of flux commands to detailed information in HTML
	 * @throws ClassNotFoundException On errors loading the command classes
	 * @throws IOException On errors loading the flux-commands.properties
	 */
	public static Map<String, String> fluxCommands()
			throws ClassNotFoundException, IOException {
		Map<String, String> result = new HashMap<String, String>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (Entry<Object, Object> command : properties(loader).entrySet()) {
			Class<?> commandImpl = loader.loadClass(command.getValue().toString());
			Description description = commandImpl.getAnnotation(Description.class);
			In in = commandImpl.getAnnotation(In.class);
			Out out = commandImpl.getAnnotation(Out.class);
			result.put(command.getKey().toString(),
					details(commandImpl, description, in, out));
		}
		return result;
	}

	private static Properties properties(ClassLoader loader) throws IOException {
		Properties commands = new Properties();
		String propertiesFileName = "flux-commands.properties";
		InputStream propertiesFile = loader.getResourceAsStream(propertiesFileName);
		try {
			if (propertiesFile == null)
				throw new FileNotFoundException("Could not find " + propertiesFileName);
			commands.load(propertiesFile);
			return commands;
		} finally {
			Closeables.closeQuietly(propertiesFile);
		}
	}

	private static String details(Class<?> clazz, Description desc, In in, Out out) {
		/* @formatter:off */
		String details =
				String.format(
						"<div style=\"font-size:12px\">" +
								"<p><b>Description:</b> %s</p>" +
								"<p><b>In:</b> %s</p>" +
								"<p><b>Out:</b> %s</p>" +
								"<p><b>Implementation:</b> %s</p>" +
						"</div>",
						desc != null ? desc.value() : "<i>no description annotation</i>",
						in != null ? in.value() : "<i>no input annotation</i>",
						out != null ? out.value() : "<i>no output annotation</i>",
						clazz.getName());
		/* @formatter:off */
		return details;
	}

}
