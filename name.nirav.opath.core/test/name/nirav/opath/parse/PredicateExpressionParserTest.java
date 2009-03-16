/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse;

import junit.framework.TestCase;
import name.nirav.opath.parse.ast.expr.EqualsExpression;
import name.nirav.opath.parse.ast.expr.Expression;
import name.nirav.opath.parse.ast.expr.GreaterThanExpression;
import name.nirav.opath.parse.ast.expr.LessThanExpression;
import name.nirav.opath.parse.ast.expr.MethodInvocationExpression;
import name.nirav.opath.parse.ast.expr.NotEqualsExpression;

import org.junit.Test;

/**
 * @author Nirav Thaker
 * 
 */
public class PredicateExpressionParserTest extends TestCase{
	@Test
	public void testMi() throws Exception {
		Expression chore = chore("#test");
		assertTrue(chore instanceof MethodInvocationExpression);
	}

	@Test
	public void testRelationalOper() throws Exception {
		Expression chore = chore("test > 1");
		assertTrue(chore instanceof GreaterThanExpression);
		chore = chore("2 > 1");
		assertTrue(chore instanceof GreaterThanExpression);
		chore = chore("a = b");
		assertTrue(chore instanceof EqualsExpression);
		chore = chore("a = b > 1");
		assertTrue(chore instanceof EqualsExpression);
		chore = chore("3 < a = b > 1");
		assertTrue(chore instanceof GreaterThanExpression);
		chore = chore("'a' != 'b'");
		assertTrue(chore instanceof NotEqualsExpression);
	}

	@Test
	public void testInvalidExpr() throws Exception {
		try {
			chore("@test > 2");
			fail("Parsed invalid expression");
		} catch (Exception e) {
		}
		try {
			chore("test > '2sa ");
			fail("Parsed invalid literal expression");
		} catch (Exception e) {
		}
		try {
			chore("test > /2");
			fail("Parsed invalid literal expression");
		} catch (Exception e) {
		}

	}

	@Test
	public void testRelationalOpAndMi() throws Exception {
		Expression chore = chore("#test > 2");
		assertTrue(chore instanceof GreaterThanExpression);
		chore = chore("#test <2");
		assertTrue(chore instanceof LessThanExpression);
		chore = chore("#test !=2");
		assertTrue(chore instanceof NotEqualsExpression);
		chore = chore("#test =2");
		assertTrue(chore instanceof EqualsExpression);
		chore = chore("2=#xmap");
		assertTrue(chore instanceof EqualsExpression);
		chore = chore("'2' = #xmap");
		assertTrue(chore instanceof EqualsExpression);
	}

	private static Expression chore(String expr) {
		Scanner scanner = new Scanner(expr);
		scanner.moveNext();
		PredicateExpressionParser parser = new PredicateExpressionParser(scanner);
		return parser.parse();
	}
}
