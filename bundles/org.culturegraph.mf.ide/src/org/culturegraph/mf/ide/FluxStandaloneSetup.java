package org.culturegraph.mf.ide;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class FluxStandaloneSetup extends FluxStandaloneSetupGenerated {

	/** Initialize the Flux standalone setup. */
	public static void doSetup() {
		new FluxStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}
