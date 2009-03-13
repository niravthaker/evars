package name.nirav.opath.parse;

import junit.framework.TestCase;

import name.nirav.opath.parse.OPathParser;
import name.nirav.opath.parse.Scanner;

import org.junit.Test;

public class OPathParserTest extends TestCase {
	private OPathParser pathParser;

	@Test
	public void testParseSteps() throws Exception {
		try {
			oparse().parse("/company/vendor/contact").pp();
			oparse().parse("/company/vendor/contact").pp();
			oparse().parse("//company/vendor/contact").pp();
			oparse().parse("/company//vendor/contact").pp();
			oparse().parse("/company/vendor//contact").pp();
			oparse().parse("//company//vendor//contact").pp();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			oparse().parse("/+/company////vendor/////contact");
			fail("Parsed Invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oparse().parse("company/vendor/contact").pp();
		} catch (Exception e) {
			fail("Parse failed");
		}
	}

	@Test
	public void testFullSteps() {
		oparse().parse("//*/*/test").pp();
		try {
			oparse().parse("//**/test");
			fail("Parsed invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oparse().parse("//*/@test").pp();
			oparse().parse("//iamright/@*").pp();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Didn't parse valid");
		}
		try {
			oparse().parse("//*/@@test").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new OPathParser().parse(new Scanner(), "$andshit");
			fail("Invalid parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		oparse().parse("/author").pp();
		oparse().parse("bookstore//book/excerpt//emph").pp();
		oparse().parse("bookstore/*/title").pp();
		oparse().parse("*/*").pp();
		oparse().parse("/*/*/*/BBB").pp();
		oparse().parse("@style").pp();
		oparse().parse("price/@exchange").pp();
		oparse().parse("@*").pp();
		oparse().parse("//*").pp();
		oparse().parse("./../*").pp();
		oparse().parse("/author/book/../@test").pp();
	}

	@Test
	public void testPredicates() {
		oparse().parse(new Scanner(), "author[1]").pp();
		oparse().parse(new Scanner(), "author/at[2]").pp();
		oparse().parse(new Scanner(), "//author/at[0221]").pp();
		oparse().parse(new Scanner(), "//author/at['index']").pp();
		try {
			oparse().parse(new Scanner(), "//author/at[2").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			oparse().parse(new Scanner(), "//author/at['index]").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			oparse().parse(new Scanner(), "//author/at[/@index]").pp();
			fail("Invalid predicate literal parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		oparse().parse(new Scanner(), "/author/revise['index']/divide").pp();
		oparse().parse(new Scanner(), "/author[index]/divide").pp();
		oparse().parse(new Scanner(), "author[index]").pp();
	}
	@Test
	public void testPredicateExtensions() {
		oparse().parse("/author/revise[id=5]/divide").pp();
	}

	private OPathParser oparse() {
		pathParser = new OPathParser();
		return pathParser;
	}
}
