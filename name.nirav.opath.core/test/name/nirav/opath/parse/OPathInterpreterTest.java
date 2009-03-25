package name.nirav.opath.parse;

import java.util.Collection;

import junit.framework.TestCase;
import name.nirav.opath.ModelVisitor;
import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.Value;
import name.nirav.opath.Variable;
import name.nirav.opath.parse.ast.OPathASTFactory;

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
		OPathInterpreter interpreter = intr();
		interpreter.evaluate("a/b", x);
		Collection<Variable> result = interpreter.getResult();
		assertEquals(b, result.toArray()[0]);

		interpreter = intr();
		interpreter.evaluate("a/b/c", x);
		result = interpreter.getResult();
		assertEquals(c, result.toArray()[0]);

		interpreter = intr();
		interpreter.evaluate("//c", x);
		result = interpreter.getResult();
		assertEquals(c, result.toArray()[0]);

		interpreter = intr();
		interpreter.evaluate("a/b//e", x);
		result = interpreter.getResult();
		assertEquals(e, result.toArray()[0]);

		interpreter = intr();
		interpreter.evaluate("a/b//*", x);
		result = interpreter.getResult();
		assertEquals(2, result.size());
		assertTrue(result.contains(c));
		assertTrue(result.contains(e));
	}

	private OPathInterpreter intr() {
		OPathInterpreter pathInterpreter = new OPathInterpreter();
		pathInterpreter.setASTFactory(OPathASTFactory.getInstance());
		return pathInterpreter;
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
		prettyPrint(x);
	}

	public void testAllElementsTest() {
		buildTestModel();
		OPathInterpreter intr = intr();
		intr.evaluate("a/*", x);
		Collection<Variable> result = intr.getResult();
		assertEquals(b, result.toArray()[0]);

		intr = intr();
		intr.evaluate("//a/*", x);
		result = intr.getResult();
		assertEquals(b, result.toArray()[0]);

		intr = intr();
		intr.evaluate("//*", x);
		result = intr.getResult();
		assertEquals(4, result.size());
	}

	public void testAttributeTest() throws Exception {
		buildTestModel();
		OPathInterpreter intr = intr();
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

		intr = intr();
		intr.evaluate("a/@\\[[3-4]\\]", frame);
		Collection<Variable> result = intr.getResult();
		assertEquals(2, result.size());

		intr = intr();
		intr.evaluate("//@\\[[0-5]\\]", frame);
		result = intr.getResult();
		assertEquals(6, result.size());
	}

	public void testCurrentParent() throws Exception {
		buildTestModel();
		OPathInterpreter intr = intr();
		intr.evaluate("//c/../..", x);
		Collection<Variable> result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = intr();
		intr.evaluate("//e/.././*", x);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);
	}

	public void testPredicates() throws Exception {
		OPathInterpreter intr = intr();
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
		Variable literalVal = new Variable("literal", var6);
		value.addVariable(literalVal);
		value.addVariable(var6);
		Variable name = new Variable("name", var6);
		value.addVariable(name);
		name.setValue(new Value() {
			@Override
			public Object getComparableValue() {
				return 1;
			}
		});
		literalVal.setValue(new Value() {
			@Override
			public String toString() {
				return getComparableValue().toString();
			}

			@Override
			public Object getComparableValue() {
				return "test";
			}
		});

		prettyPrint(frame);

		intr = intr();
		intr.evaluate("a[6]", frame);
		Collection<Variable> result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = intr();
		intr.evaluate("a/[6]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = intr();
		intr.evaluate("a[name]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = intr();
		intr.evaluate("a[name = 1]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
		assertEquals(a, result.toArray()[0]);

		intr = intr();
		intr.evaluate("a[name != 6]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());

		intr = intr();
		intr.evaluate("a[name > 0]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());

		intr = intr();
		intr.evaluate("a[name > 2]", frame);
		result = intr.getResult();
		assertEquals(0, result.size());

		intr = intr();
		intr.evaluate("a[name < 2]", frame);
		result = intr.getResult();
		assertEquals(1, result.size());

		intr = intr();
		intr.evaluate("a[literal = '@2']", frame);
		result = intr.getResult();
		assertEquals(0, result.size());

		intr = intr();
		intr.evaluate("a[literal = '@t.*']", frame);
		result = intr.getResult();
		assertEquals(1, result.size());
	}

	public static void prettyPrint(Variable frame) {
		frame.accept(new ModelVisitor() {
			int tab;

			@Override
			public void enter(Value value) {
				tab++;
			}

			@Override
			public void exit(Value value) {
				tab--;
			}

			@Override
			public void visit(Value value) {
				super.visit(value);
			}

			@Override
			public void visit(Variable variable) {
				print("Vari:" + variable.toString() + " => " + variable.getValue());
			}

			private void print(String str) {
				if (tab >= 1)
					System.out.print("|");
				for (int i = 1; i < tab; i++) {
					System.out.print("--");
				}
				System.out.println(str);
			}
		});
	}
}
