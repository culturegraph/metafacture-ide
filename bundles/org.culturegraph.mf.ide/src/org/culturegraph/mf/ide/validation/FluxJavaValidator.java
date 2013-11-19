/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.validation;

import static org.culturegraph.mf.ide.domain.FluxCommandMetadata.COMMANDS;

import org.culturegraph.mf.framework.DefaultTee;
import org.culturegraph.mf.ide.domain.FluxCommandMetadata;
import org.culturegraph.mf.ide.flux.Flow;
import org.culturegraph.mf.ide.flux.FluxPackage;
import org.culturegraph.mf.ide.flux.Mainflow;
import org.culturegraph.mf.ide.flux.Pipe;
import org.eclipse.xtext.validation.Check;

import com.google.common.base.Joiner;

/**
 * Custom rules to statically validate domain specific constraints.
 * <p/>
 * See http://www.eclipse.org/Xtext/documentation.html#validation
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxJavaValidator extends AbstractFluxJavaValidator {

	private String pipeInput = null;
	private String teeInput = null;

	@Check
	private void checkFluxCommand(Pipe pipe) {
		String commandId = commandId(pipe);
		if (isUnknownCommand(commandId)) {
			error("Command not found: " + commandId, FluxPackage.Literals.PIPE__QN);
		} else if (isFirstCommand()) {
			validateInput(commandId, String.class); /* First command expects String */
		} else {
			validateInput(commandId, previousCommand().getOutputType());
		}
		remember(commandId);
	}

	private static String commandId(Pipe pipe) {
		return Joiner.on(".").join(pipe.getQn().getIds());
	}

	private static boolean isUnknownCommand(String commandId) {
		return !COMMANDS.containsKey(commandId);
	}

	private boolean isFirstCommand() {
		return pipeInput == null && teeInput == null;
	}

	private FluxCommandMetadata previousCommand() {
		return COMMANDS.get(pipeInput != null ? pipeInput : teeInput);
	}

	private void remember(String commandId) {
		if (isTee(commandId)) {
			/* At a tee, remember the command to continue from in the two branches: */
			teeInput = pipeInput != null ? pipeInput : teeInput;
		} else
			/* But never use the tee command itself as input (hence 'else'): */
			pipeInput = commandId;
	}

	private static boolean isTee(String commandId) {
		return DefaultTee.class.isAssignableFrom(COMMANDS.get(commandId)
				.getImplementationType());
	}

	private void validateInput(String commandId, Class<?> inputType) {
		FluxCommandMetadata metadata = COMMANDS.get(commandId);
		Class<?> expectedInputType = metadata.getInputType();
		if (inputType == null || expectedInputType == null
		/* Object is used as output type with generics, can't validate: */
		|| (inputType == Object.class && expectedInputType != Object.class)) {
			warning(String.format(
					"Unverifiable workflow: '%s' expects input type '%s', but got '%s'",
					commandId, expectedInputType, inputType),
					FluxPackage.Literals.PIPE__QN);
		} else if (!expectedInputType.isAssignableFrom(inputType)) {
			error(String.format(
					"Invalid workflow: '%s' expects input type '%s', but got '%s'",
					commandId, expectedInputType, inputType),
					FluxPackage.Literals.PIPE__QN);
		}
	}

	@Check
	private void resetAll(@SuppressWarnings("unused") Mainflow flow) {
		teeInput = null;
		pipeInput = null;
	}

	@Check
	private void resetCurrent(@SuppressWarnings("unused") Flow flow) {
		pipeInput = null;
	}
}
