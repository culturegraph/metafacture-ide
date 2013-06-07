/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.domain;

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
 * Metadata for Flux commands.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxCommandMetadata {
	private Class<?> impl;
	private Description desc;
	private In in;
	private Out out;

	/**
	 * @param command The Flux command (as used in a *.flux file and declared in
	 *          flux.properties)
	 * @throws ClassNotFoundException If the command's implementation class
	 *           declared in flux.properties can't be found
	 */
	public FluxCommandMetadata(String command) throws ClassNotFoundException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> commandImpl = loader.loadClass(command);
		this.impl = commandImpl;
		this.desc = commandImpl.getAnnotation(Description.class);
		this.in = commandImpl.getAnnotation(In.class);
		this.out = commandImpl.getAnnotation(Out.class);
	}

	/**
	 * @return An HTML-formatted summary of this command metadata
	 */
	public String format() {
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
							impl.getName());
			/* @formatter:on */
		return details;
	}

	/**
	 * @return A mapping of flux commands to their metadata
	 */
	public static Map<String, FluxCommandMetadata> commands() {
		Map<String, FluxCommandMetadata> result =
				new HashMap<String, FluxCommandMetadata>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			for (Entry<Object, Object> command : properties(loader).entrySet()) {
				result.put(command.getKey().toString(), new FluxCommandMetadata(command
						.getValue().toString()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return This command's output type, or null if no output type is set
	 */
	public Class<?> getOutputType() {
		return out == null ? null : out.value();
	}

	/**
	 * @return This command's input type, or null if no input type is set
	 */
	public Class<?> getInputType() {
		return in == null ? null : in.value();
	}

	/**
	 * @return This command's implementation type, or null if no implementation
	 *         type is set
	 */
	public Class<?> getImplementationType() {
		return impl;
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

}
