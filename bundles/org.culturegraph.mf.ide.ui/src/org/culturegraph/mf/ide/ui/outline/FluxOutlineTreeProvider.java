/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.outline;

import org.culturegraph.mf.ide.flux.Mainflow;
import org.culturegraph.mf.ide.flux.Metaflow;
import org.culturegraph.mf.ide.flux.Pipe;
import org.culturegraph.mf.ide.flux.VarDef;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

/**
 * Customization of the default outline structure.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class FluxOutlineTreeProvider extends DefaultOutlineTreeProvider {
	// see http://www.eclipse.org/Xtext/documentation.html#outline

	/**
	 * Pipe nodes are leafs and not expandable
	 * 
	 * @param pipe The `Pipe` model element
	 * @return true
	 */
	protected boolean _isLeaf(Pipe pipe) {
		return true;
	}

	/**
	 * Skip the `Mainflow` model element in the outline structure
	 * 
	 * @param parent The outine parent node
	 * @param flow The `Metaflow` model element
	 */
	protected void _createChildren(IOutlineNode parent, Metaflow flow) {
		for (VarDef var : flow.getVars()) {
			createNode(parent, var);
		}
		for (Mainflow mainflow : flow.getFlows()) {
			createNode(parent, mainflow.getFlow()); // skip the `Mainflow`
		}
	}

}
