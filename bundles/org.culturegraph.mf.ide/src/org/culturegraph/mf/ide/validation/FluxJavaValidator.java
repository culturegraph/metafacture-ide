/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.validation;

import java.util.Map;

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

	private Map<String, FluxCommandMetadata> commands = FluxCommandMetadata
			.commands();

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

	private boolean isUnknownCommand(String commandId) {
		return !commands.containsKey(commandId);
	}

	private boolean isFirstCommand() {
		return pipeInput == null && teeInput == null;
	}

	private FluxCommandMetadata previousCommand() {
		return commands.get(pipeInput != null ? pipeInput : teeInput);
	}

	private void remember(String commandId) {
		if (isTee(commandId)) {
			/* At a tee, remember the command to continue from in the two branches: */
			teeInput = pipeInput != null ? pipeInput : teeInput;
		} else
			/* But never use the tee command itself as input (hence 'else'): */
			pipeInput = commandId;
	}

	private boolean isTee(String commandId) {
		return DefaultTee.class.isAssignableFrom(commands.get(commandId)
				.getImplementationType());
	}

	private void validateInput(String commandId, Class<?> inputType) {
		FluxCommandMetadata metadata = commands.get(commandId);
		checkInputAnnotation(commandId, metadata);
		checkOutputAnnotation(commandId, metadata);
		boolean isAssignable =
				/* If no annotations are given, we have to assume it might work: */
				inputType == null || metadata.getInputType() == null
						|| metadata.getInputType().isAssignableFrom(inputType);
		if (!isAssignable) {
			error(String.format(
					"Command '%s' expects input of type '%s', but got '%s'", commandId,
					metadata.getInputType().getName(), inputType == null ? null
							: inputType.getName()), FluxPackage.Literals.PIPE__QN);
		}
	}

	private void checkInputAnnotation(String currentCommandId,
			FluxCommandMetadata currentMeta) {
		if (currentMeta.getInputType() == null) {
			/* Warning for missing in, since that will always consume: */
			warning(
					String.format("Implementation class '%s' for command '%s' has no "
							+ "@In annotation, can't validate workflow",
							currentMeta.getImplementationType(), currentCommandId),
					FluxPackage.Literals.PIPE__QN);
		}
	}

	private void checkOutputAnnotation(String commandId,
			FluxCommandMetadata metadata) {
		if (metadata.getOutputType() == null) {
			/* Info only for missing out, since that might not be consumed: */
			info(String.format(
					"Implementation '%s' for command '%s' has no @Out annotation",
					metadata.getImplementationType().getName(), commandId),
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
