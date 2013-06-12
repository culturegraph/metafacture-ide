/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.labeling;

import org.culturegraph.mf.ide.domain.FluxCommandMetadata;
import org.culturegraph.mf.ide.flux.Flow;
import org.culturegraph.mf.ide.flux.Pipe;
import org.culturegraph.mf.ide.flux.Tee;
import org.culturegraph.mf.ide.flux.VarDef;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * <p/>
 * see http://www.eclipse.org/Xtext/documentation.html#labelProvider for usage
 * 
 * @author Fabian Steeg (fsteeg)
 */
@SuppressWarnings("unused")
/* For `image` methods below */
public class FluxLabelProvider extends DefaultEObjectLabelProvider {
	/**
	 * @param delegate The delegate to use for label creation.
	 */
	@Inject
	public FluxLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	Object text(Flow flow) {
		return String.format(" workflow: %s pipes", flow.getPipes().size());
	}

	Object text(Tee tee) {
		return String.format(" tee: %s workflows", tee.getFlows().size());
	}

	Object text(Pipe pipe) {
		String commandId = Joiner.on(".").join(pipe.getQn().getIds());
		FluxCommandMetadata metadata = FluxCommandMetadata.COMMANDS.get(commandId);
		return metadata != null ? styled(formatInputOutput(commandId, metadata))
				: styled(String.format(" %s : <unknown>", commandId));
	}

	private static String formatInputOutput(String commandId,
			FluxCommandMetadata metadata) {
		Class<?> inputType = metadata.getInputType();
		Class<?> outputType = metadata.getOutputType();
		return String.format(" %s : %s -> %s", commandId, /**/
				inputType == null ? "?" : inputType.getSimpleName(),
				outputType == null ? "?" : outputType.getSimpleName());
	}

	private static StyledString styled(String format) {
		StyledString styled = new StyledString(format);
		int offset = format.indexOf(':');
		styled.setStyle(offset, format.length() - offset,
				StyledString.DECORATIONS_STYLER);
		return styled;
	}

	String image(Flow element) {
		return "flow.gif";
	}

	String image(Tee element) {
		return "tee.gif";
	}

	String image(Pipe element) {
		return "pipe.gif";
	}

	String image(VarDef element) {
		return "var.gif";
	}
}
