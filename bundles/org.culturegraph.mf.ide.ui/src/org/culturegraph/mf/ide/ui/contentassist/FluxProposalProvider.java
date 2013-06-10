/* Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0 */

package org.culturegraph.mf.ide.ui.contentassist;

import java.io.IOException;
import java.util.Map.Entry;

import org.culturegraph.mf.ide.domain.FluxCommandMetadata;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControl;

/**
 * Customized content assist for Flux commands.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class FluxProposalProvider extends AbstractFluxProposalProvider {
	// see http://www.eclipse.org/Xtext/documentation.html#contentAssist

	@Override
	public void complete_Pipe(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.complete_Pipe(model, ruleCall, context, acceptor);
		try {
			createFluxCommandProposals(context, acceptor);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			acceptor.accept(createCompletionProposal(e.getMessage(), context));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createFluxCommandProposals(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) throws IOException,
			ClassNotFoundException {
		for (Entry<String, FluxCommandMetadata> entry : FluxCommandMetadata
				.commands().entrySet()) {
			ConfigurableCompletionProposal proposal =
					(ConfigurableCompletionProposal) createCompletionProposal(entry
							.getKey().toString(), context);
			if (proposal != null) {
				proposal
						.setAdditionalProposalInfo(entry.getValue().format().toString());
				acceptor.accept(proposal);
			}
		}
	}

	@Override
	protected ConfigurableCompletionProposal doCreateProposal(String proposal,
			StyledString displayString, Image image, int replacementOffset,
			int replacementLength) {
		/* hook into proposal creation to return HTML-capable widget below: */
		return new FluxCompletionProposal(proposal, replacementOffset,
				replacementLength, replacementOffset + replacementLength);
	}

	private static class FluxCompletionProposal extends
			ConfigurableCompletionProposal {

		public FluxCompletionProposal(String replacementString,
				int replacementOffset, int replacementLength, int cursorPosition) {
			super(replacementString, replacementOffset, replacementLength,
					cursorPosition);
		}

		@Override
		public IInformationControlCreator getInformationControlCreator() {
			return new AbstractReusableInformationControlCreator() {
				@Override
				public IInformationControl doCreateInformationControl(Shell parent) {
					return new XtextBrowserInformationControl(parent,
							JFaceResources.DEFAULT_FONT, false);
				}
			};
		}
	}

}
