/**
 * 
 */
package name.nirav.opath.parse;

import static org.junit.Assert.*;

import java.util.Collection;

import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.Value;
import name.nirav.opath.Variable;

import org.junit.Test;

/**
 * @author nrulezz
 * 
 */
public class CyclicDebugModelTest {
	@Test
	public void testCyclicGraph() {
		Variable a = new Variable("a");
		Value value = new Value();
		Variable b = new Variable("b", a);
		value.addVariable(b);
		Variable c = new Variable("c", a);
		value.addVariable(c);
		Variable d = new Variable("d", a);
		value.addVariable(d);
		a.setValue(value);

		value = new Value();
		Variable e = new Variable("e", c);
		value.addVariable(e);
		Variable f = new Variable("f", c);
		value.addVariable(f);
		c.setValue(value);

		Variable g = new Variable("g", b);
		value = new Value();
		value.addVariable(g);
		b.setValue(value);
		Variable h = new Variable("h", d);
		value = new Value();
		value.addVariable(h);
		d.setValue(value);

		Variable x = new Variable("x");
		x.setParent(e);
		value = new Value();
		value.addVariable(x);
		e.setValue(value);

		x.setParent(f);
		f.setValue(value);

		x.setParent(g);
		g.setValue(value);

		x.setParent(h);
		h.setValue(value);

		Variable context = new Variable("Root");
		value = new Value();
		value.addVariable(a);
		context.setValue(value);
		
		Collection<Variable> result = new OPathInterpreter().evaluate("//*", context).getResult();
		assertEquals(9, result.size());
	}
}
