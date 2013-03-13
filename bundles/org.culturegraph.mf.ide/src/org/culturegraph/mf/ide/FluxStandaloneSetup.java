package org.culturegraph.mf.ide;

import org.culturegraph.mf.ide.FluxStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class FluxStandaloneSetup extends FluxStandaloneSetupGenerated {

	public static void doSetup() {
		new FluxStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}
