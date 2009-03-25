package name.nirav.opath.reflect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleXMLParser extends TestCase {
	List<String> list = new LinkedList<String>();

	public void testSimpleXMLParserTest() throws Throwable {
		Element parse = new SimpleXMLParser().parse();
		Collection<Object> interpret = OPathReflectiveInterpreter.findAll(parse, "//@first.*");
		String find = (String) OPathReflectiveInterpreter.find(parse, "//data['@Com.*']");
		assertEquals(9,interpret.size());
		assertEquals("Computer",find);
	}

	private Element parse() throws Throwable {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(getClass().getResourceAsStream("sample.xml"));
		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}
}
