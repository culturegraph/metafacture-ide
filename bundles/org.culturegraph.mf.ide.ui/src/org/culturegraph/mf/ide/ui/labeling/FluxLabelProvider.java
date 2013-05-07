/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.labeling;

import org.culturegraph.mf.ide.flux.Flow;
import org.culturegraph.mf.ide.flux.Pipe;
import org.culturegraph.mf.ide.flux.Tee;
import org.culturegraph.mf.ide.flux.VarDef;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
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
		return " " + Joiner.on(".").join(pipe.getQn().getIds());
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
