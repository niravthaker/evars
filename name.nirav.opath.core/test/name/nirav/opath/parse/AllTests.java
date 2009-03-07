package name.nirav.opath.parse;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for name.nirav.opath.parse");
		//$JUnit-BEGIN$
		suite.addTestSuite(OPathParserTest.class);
		suite.addTestSuite(CyclicDebugModelTest.class);
		suite.addTestSuite(ScannerTest.class);
		suite.addTestSuite(OPathInterpreterTest.class);
		//$JUnit-END$
		return suite;
	}

}
