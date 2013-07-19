package org.culturegraph.mf.ide.tests;

import org.culturegraph.mf.ide.FluxInjectorProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Parse sample flux files.
 * 
 * @author Fabian Steeg (fsteeg)
 */
@RunWith(XtextRunner2.class)
@InjectWith(FluxInjectorProvider.class)
@SuppressWarnings("javadoc")
public class FluxParserTests extends XtextTest {

	public FluxParserTests() {
		super("FluxParserTests");
	}

	/* @formatter:off */
	@Test public void parse01() { testFile("count-gnd-types.flux"); }
	@Test public void parse02() { testFile("filter-morph.flux"); }
	@Test public void parse03() { testFile("format-gnd.flux"); }
	@Test public void parse04() { testFile("MARC21-EDM.flux"); }
	@Test public void parse05() { testFile("marcxml-online.flux"); }
	@Test public void parse06() { testFile("morph-marc21.flux"); }
	@Test public void parse07() { testFile("multiple-flows.flux"); }
	@Test public void parse08() { testFile("read-dirs.flux"); }
	@Test public void parse09() { testFile("read-marc21.flux"); }
	@Test public void parse10() { testFile("read-pica.flux"); }
	@Test public void parse11() { testFile("regexp.flux"); }
	@Test public void parse12() { testFile("sort-gnd.flux"); }
	@Test public void parse13() { testFile("tp2json.flux"); }
	/* @formatter:on */

	private static ClassLoader contextClassLoader;

	@BeforeClass
	public static void beforeTests() {
		final Thread currentThread = Thread.currentThread();
		contextClassLoader = currentThread.getContextClassLoader();
		currentThread.setContextClassLoader(FluxParserTests.class.getClassLoader());
	}

	@AfterClass
	public static void afterTests() {
		Thread.currentThread().setContextClassLoader(contextClassLoader);
	}

	@Before
	public void beforeTest() {
		suppressSerialization();
	}

	@After
	public void afterTest() {
		ignoreUnassertedWarnings();
	}
}
