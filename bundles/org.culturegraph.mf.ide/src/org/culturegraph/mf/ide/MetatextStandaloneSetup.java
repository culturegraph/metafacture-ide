
package org.culturegraph.mf.ide;

import org.culturegraph.mf.ide.MetatextStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class MetatextStandaloneSetup extends MetatextStandaloneSetupGenerated{

	public static void doSetup() {
		new MetatextStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

