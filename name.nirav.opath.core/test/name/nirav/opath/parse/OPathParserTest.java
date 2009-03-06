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
			extracted().parse("/company/vendor/contact").pp();
			extracted().parse("/company/vendor/contact").pp();
			extracted().parse("//company/vendor/contact").pp();
			extracted().parse("/company//vendor/contact").pp();
			extracted().parse("/company/vendor//contact").pp();
			extracted().parse("//company//vendor//contact").pp();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			extracted().parse("/+/company////vendor/////contact");
			fail("Parsed Invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			extracted().parse("company/vendor/contact").pp();
		} catch (Exception e) {
			fail("Parse failed");
		}
	}

	@Test
	public void testFullSteps() {
		extracted().parse("//*/*/test").pp();
		try {
			extracted().parse("//**/test");
			fail("Parsed invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			extracted().parse("//*/@test").pp();
			extracted().parse("//iamright/@*").pp();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Didn't parse valid");
		}
		try {
			extracted().parse("//*/@@test").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new OPathParser().parse(new Scanner(), "$andshit");
			fail("Invalid parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		extracted().parse("/author").pp();
		extracted().parse("bookstore//book/excerpt//emph").pp();
		extracted().parse("bookstore/*/title").pp();
		extracted().parse("*/*").pp();
		extracted().parse("/*/*/*/BBB").pp();
		extracted().parse("@style").pp();
		extracted().parse("price/@exchange").pp();
		extracted().parse("@*").pp();
		extracted().parse("//*").pp();
		extracted().parse("./../*").pp();
		extracted().parse("/author/book/../@test").pp();
	}

	@Test
	public void testPredicates() {
		extracted().parse(new Scanner(), "author[1]").pp();
		extracted().parse(new Scanner(), "author/at[2]").pp();
		extracted().parse(new Scanner(), "//author/at[0221]").pp();
		extracted().parse(new Scanner(), "//author/at['index']").pp();
		try {
			extracted().parse(new Scanner(), "//author/at[2").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			extracted().parse(new Scanner(), "//author/at['index]").pp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			extracted().parse(new Scanner(), "//author/at[/@index]").pp();
			fail("Invalid predicate literal parsed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		extracted().parse(new Scanner(), "/author/revise['index']/divide").pp();
		extracted().parse(new Scanner(), "/author[index]/divide").pp();
		extracted().parse(new Scanner(), "author[index]").pp();
	}

	private OPathParser extracted() {
		pathParser = new OPathParser();
		return pathParser;
	}
}
