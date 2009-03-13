/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse;

import name.nirav.opath.parse.Scanner.Token;
import name.nirav.opath.parse.Scanner.Type;
import name.nirav.opath.parse.ast.EqualsExpression;
import name.nirav.opath.parse.ast.Expression;
import name.nirav.opath.parse.ast.GreaterThanExpression;
import name.nirav.opath.parse.ast.LessThanExpression;
import name.nirav.opath.parse.ast.LiteralExpression;
import name.nirav.opath.parse.ast.MethodInvocationExpression;
import name.nirav.opath.parse.ast.NotEqualsExpression;
import name.nirav.opath.parse.ast.NumberExpression;
import name.nirav.opath.parse.ast.QNameExpression;

/**
 * @author Nirav Thaker
 * 
 */
public class PredicateExpressionParser {

	private final Scanner scanner;

	public PredicateExpressionParser(Scanner scanner) {
		this.scanner = scanner;
	}

	public Expression parse() {
		return expr();
	}

	private Expression expr() {
		Token currentToken = scanner.getCurrentToken();
		switch (currentToken.type) {
		case NUMBER:
		case QNAME:
		case LITERAL:
		case MI:
			Expression expr = sExpr();
			return exprOpt(expr);
		default:
			throw new IllegalArgumentException("Found unknown symbol: " + currentToken
					+ " when expecting one of the following: " + Type.NUMBER + "," + Type.LITERAL
					+ "," + Type.QNAME + "," + Type.MI);
		}
	}

	private Expression sExpr() {
		Token currentToken = scanner.getCurrentToken();
		switch (currentToken.type) {
		case NUMBER:
			scanner.moveNext();
			return new NumberExpression(currentToken.value);
		case QNAME:
			scanner.moveNext();
			return new QNameExpression(currentToken.value);
		case LITERAL:
			Token next = scanner.moveNext();
			if (scanner.moveNext().type != Scanner.Type.LITERAL)
				throw new IllegalArgumentException("Literal didn't end properly, found : "
						+ scanner.getCurrentToken());
			scanner.moveNext();
			return new LiteralExpression(next.value);
		case MI:
			Token methodName = scanner.moveNext();
			scanner.moveNext();
			return new MethodInvocationExpression(methodName.value);
		default:
			throw new IllegalArgumentException("Found unknown symbol: " + currentToken
					+ " when expecting one of the following: " + Type.NUMBER + "," + Type.LITERAL
					+ "," + Type.QNAME + "," + Type.MI);
		}
	}

	private Expression exprOpt(Expression expr) {
		Token currentToken = scanner.getCurrentToken();
		switch (currentToken.type) {
		case EQ:
			scanner.moveNext();
			Expression equalsExpr = sExpr();
			return exprOpt(new EqualsExpression(expr, equalsExpr));
		case NEQ:
			scanner.moveNext();
			Expression notEqualsExpr = sExpr();
			return exprOpt(new NotEqualsExpression(expr, notEqualsExpr));
		case LT:
			scanner.moveNext();
			Expression ltExpr = sExpr();
			return exprOpt(new LessThanExpression(expr, ltExpr));
		case GT:
			scanner.moveNext();
			Expression gtExpr = sExpr();
			return exprOpt(new GreaterThanExpression(expr, gtExpr));
		}
		return expr;
	}
}
