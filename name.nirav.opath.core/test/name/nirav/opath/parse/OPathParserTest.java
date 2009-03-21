package name.nirav.opath.parse;

import junit.framework.TestCase;

import name.nirav.opath.parse.OPathParser;
import name.nirav.opath.parse.Scanner;
import name.nirav.opath.parse.ast.OPathASTFactory;

import org.junit.Test;

public class OPathParserTest extends TestCase {
	private OPathParser pathParser;
	private OPathASTFactory factory = OPathASTFactory.getInstance();

	@Test
	public void testParseSteps() throws Exception {
		try {
			oparse().parse("/company/vendor/contact", factory).pp();
			oparse().parse("/company/vendor/contact", factory).pp();
			oparse().parse("//company/vendor/contact", factory).pp();
			oparse().parse("/company//vendor/contact", factory).pp();
			oparse().parse("/company/vendor//contact", factory).pp();
			oparse().parse("//company//vendor//contact", factory).pp();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			oparse().parse("/+/company////vendor/////contact", factory);
			fail("Parsed Invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oparse().parse("company/vendor/contact", factory).pp();
		} catch (Exception e) {
			fail("Parse failed");
		}
	}

	@Test
	public void testFullSteps() {
		oparse().parse("//*/*/test", factory).pp();
		try {
			oparse().parse("//**/test", factory);
			fail("Parsed invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oparse().parse("//*/@test", factory).pp();
			oparse().parse("//iamright/@*", factory).pp();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Didn't parse valid");
		}
		try {
			oparse().parse("//*/@@test", factory).pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new OPathParser().parse(new Scanner(), "$andshit", factory);
			fail("Invalid parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		oparse().parse("/author", factory).pp();
		oparse().parse("bookstore//book/excerpt//emph", factory).pp();
		oparse().parse("bookstore/*/title", factory).pp();
		oparse().parse("*/*", factory).pp();
		oparse().parse("/*/*/*/BBB", factory).pp();
		oparse().parse("@style", factory).pp();
		oparse().parse("price/@exchange", factory).pp();
		oparse().parse("@*", factory).pp();
		oparse().parse("//*", factory).pp();
		oparse().parse("./../*", factory).pp();
		oparse().parse("/author/book/../@test", factory).pp();
	}

	@Test
	public void testPredicates() {
		oparse().parse(new Scanner(), "author[1]", factory).pp();
		oparse().parse(new Scanner(), "author/at[2]", factory).pp();
		oparse().parse(new Scanner(), "//author/at[0221]", factory).pp();
		oparse().parse(new Scanner(), "//author/at['index']", factory).pp();
		try {
			oparse().parse(new Scanner(), "//author/at[2", factory).pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			oparse().parse(new Scanner(), "//author/at['index]", factory).pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			oparse().parse(new Scanner(), "//author/at[/@index]", factory).pp();
			fail("Invalid predicate literal parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		oparse().parse(new Scanner(), "/author/revise['index']/divide", factory).pp();
		oparse().parse(new Scanner(), "/author[index]/divide", factory).pp();
		oparse().parse(new Scanner(), "author[index]", factory).pp();
	}
	@Test
	public void testPredicateExtensions() {
		oparse().parse("/author/revise[id=5]/divide", factory).pp();
	}

	private OPathParser oparse() {
		pathParser = new OPathParser();
		return pathParser;
	}
}
