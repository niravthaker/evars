package name.nirav.opath.parse;

import java.util.Collection;

import junit.framework.TestCase;
import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.Value;
import name.nirav.opath.Variable;

import org.junit.Test;

public class OPathInterpreterTest extends TestCase {
	private Variable x;
	private Variable a;
	private Variable b;
	private Variable c;
	private Variable e;

	@Test
	public void testEvalSteps() {
		buildTestModel();
		OPathInterpreter interpreter = new OPathInterpreter();
		interpreter.evaluate("a/b", x);
		Collection<Variable> result = interpreter.getResult();
		assertEquals(b, result.toArray()[0]);

		interpreter = new OPathInterpreter();
		interpreter.evaluate("a/b/c", x);
		result = interpreter.getResult();
		assertEquals(c, result.toArray()[0]);

		interpreter = new OPathInterpreter();
		interpreter.evaluate("//c", x);
		result = interpreter.getResult();
		assertEquals(c, result.toArray()[0]);

		interpreter = new OPathInterpreter();
		interpreter.evaluate("a/b//e", x);
		result = interpreter.getResult();
		assertEquals(e, result.toArray()[0]);

		interpreter = new OPathInterpreter();
		interpreter.evaluate("a/b//*", x);
		result = interpreter.getResult();
		assertEquals(2, result.size());
		assertTrue(result.contains(c));
		assertTrue(result.contains(e));
	}

	private void buildTestModel() {
		x = new Variable("X");
		Value val = new Value();
		x.setValue(val);
		a = new Variable("a", x);
		val.addVariable(a);
		val = new Value();
		a.setValue(val);
		b = new Variable("b", a);
		val.addVariable(b);
		val = new Value();
		b.setValue(val);
		c = new Variable("c", b);
		val.addVariable(c);
		val = new Value();
		c.setValue(val);
		e = new Variable("e", c);
		val.addVariable(e);
	}

	public void testAllElementsTest() {
		buildTestModel();
		OPathInterpreter intr = new OPathInterpreter();
		intr.evaluate("a/*", x);
		Collection<Variable> result = intr.getResult();
		assertEquals(b, result.toArray()[0]);

		intr = new OPathInterpreter();
		intr.evaluate("//a/*", x);
		result = intr.getResult();
		assertEquals(b, result.toArray()[0]);

		intr = new OPathInterpreter();
		intr.evaluate("//*", x);
		result = intr.getResult();
		assertEquals(4, result.size());
	}

	public void testAttributeTest() throws Exception {
		buildTestModel();
		OPathInterpreter intr = new OPathInterpreter();
		Variable frame = new Variable("frame");
		Value value = new Value();
		Variable a = new Variable("a", frame);
		value.addVariable(a);
		frame.setValue(value);
		value = new Value();
		a.setValue(value);
		value.addVariable(new Variable("[0]", a));
		value.addVariable(new Variable("[1]", a));
		value.addVariable(new Variable("[2]", a));
		value.addVariable(new Variable("[3]", a));
		value.addVariable(new Variable("[4]", a));
		value.addVariable(new Variable("[5]", a));
		value.addVariable(new Variable("[6]", a));

		intr = new OPathInterpreter();
		intr.evaluate("a/@\\[[3-4]\\]", frame);
		Collection<Variable> result = intr.getResult();
		assertEquals(2, result.size());

		intr = new OPathInterpreter();
		intr.evaluate("//@\\[[0-5]\\]", frame);
		result = intr.getResult();
		assertEquals(6, result.size());
	}

	public void testCurrentParent() throws Exception {
		buildTestModel();
		OPathInterpreter intr = new OPathInterpreter();
		intr.evaluate("//c/../..", x);
		Collection<Variable> result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = new OPathInterpreter();
		intr.evaluate("//e/.././*", x);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);
	}

	public void testPredicates() throws Exception {
		OPathInterpreter intr = new OPathInterpreter();
		Variable frame = new Variable("frame");
		Value value = new Value();
		Variable a = new Variable("a", frame);
		value.addVariable(a);
		frame.setValue(value);
		value = new Value();
		a.setValue(value);
		value.addVariable(new Variable("[0]", a));
		value.addVariable(new Variable("[1]", a));
		value.addVariable(new Variable("[2]", a));
		value.addVariable(new Variable("[3]", a));
		value.addVariable(new Variable("[4]", a));
		value.addVariable(new Variable("[5]", a));
		Variable var6 = new Variable("[6]", a);
		value.addVariable(var6);
//		value = new Value();
//		var6.setValue(value);
		Variable name = new Variable("name",var6);
		value.addVariable(name);
		value = new Value() {
			@Override
			public Object getComparableValue() {
				return 1;
			}
		};
		name.setValue(value);

		intr.evaluate("a[6]", frame);
		Collection<Variable> result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(var6, result.toArray()[0]);
		
		intr.evaluate("a/[6]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(var6, result.toArray()[0]);
		
		intr.evaluate("a[name]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(name, result.toArray()[0]);

		intr.evaluate("a[name = 1]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(name, result.toArray()[0]);

		intr.evaluate("a[name != 6]", frame);
		result = intr.getResult();
		assertEquals(8, result.size());
	}
}
