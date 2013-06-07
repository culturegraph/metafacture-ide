/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.validation;

import java.util.Map;

import org.culturegraph.mf.ide.domain.FluxCommandMetadata;
import org.culturegraph.mf.ide.flux.Flow;
import org.culturegraph.mf.ide.flux.FluxPackage;
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

	private Pipe previousPipe = null;

	private Map<String, FluxCommandMetadata> commands = FluxCommandMetadata
			.commands();

	@Check
	private void checkFluxCommand(Pipe pipe) {
		String commandId = commandId(pipe);
		if (!commands.containsKey(commandId)) {
			error("Command not found: " + commandId, FluxPackage.Literals.PIPE__QN);
		}
		if (firstCommand()) {
			validateInput(pipe, String.class); /* First command expects String */
		} else {
			validateInput(pipe, commands.get(commandId(previousPipe)).getOutputType());
		}
		previousPipe = pipe;
	}

	private boolean firstCommand() {
		return previousPipe == null;
	}

	private void validateInput(Pipe pipe, Class<?> input) {
		String commandId = commandId(pipe);
		FluxCommandMetadata metadata = commands.get(commandId);
		checkInputAnnotation(commandId, metadata);
		checkOutputAnnotation(commandId, metadata);
		boolean isAssignable =
				/* If no annotations are given, we have to assume it might work: */
				input == null || metadata.getInputType() == null
						|| metadata.getInputType().isAssignableFrom(input);
		if (!isAssignable) {
			error(String.format(
					"Command '%s' expects input of type '%s', but got '%s'", commandId,
					metadata.getInputType().getName(),
					input == null ? null : input.getName()),
					FluxPackage.Literals.PIPE__QN);
		}
	}

	private static String commandId(Pipe pipe) {
		return Joiner.on(".").join(pipe.getQn().getIds());
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

	@Check
	private void resetPipe(@SuppressWarnings("unused") Flow flow) {
		System.err.println("Resetting flow");
		previousPipe = null;
	}

}
