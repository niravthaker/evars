package name.nirav.opath.parse;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for name.nirav.opath.parse");
		//$JUnit-BEGIN$
		suite.addTestSuite(OPathInterpreterTest.class);
		suite.addTestSuite(OPathParserTest.class);
		suite.addTestSuite(ScannerTest.class);
		//$JUnit-END$
		return suite;
	}

}
