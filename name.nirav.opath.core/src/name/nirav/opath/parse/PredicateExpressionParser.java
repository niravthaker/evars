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
import name.nirav.opath.parse.ast.ExpressionFactory;
import name.nirav.opath.parse.ast.expr.Expression;

/**
 * @author Nirav Thaker
 * 
 */
public class PredicateExpressionParser {

	private final Scanner scanner;
	private final ExpressionFactory factory;

	public PredicateExpressionParser(Scanner scanner, ExpressionFactory factory) {
		this.scanner = scanner;
		this.factory = factory;
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
			return exprOpt(sExpr());
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
			return factory.newNumberExpr(currentToken.value);
		case QNAME:
			scanner.moveNext();
			return factory.newQNameExpr(currentToken.value);
		case LITERAL:
			Token next = scanner.moveNext();
			boolean isRegEx = next.type == Type.ATR ? true : false;
			StringBuilder builder = new StringBuilder();
			builder.append(next.value);
			while (next.type != Type.LITERAL && next.type != Type.EOF) {
				next = scanner.moveNext();
				if(next.type != Type.LITERAL && next.type != Type.EOF)
					builder.append(next.value);
			}
			if (next.type != Scanner.Type.LITERAL)
				throw new IllegalArgumentException("Literal didn't end properly, found : "
						+ scanner.getCurrentToken());
			scanner.moveNext();
			return factory.newLiteralExpr(builder.toString(), isRegEx);
		case MI:
			Token methodName = scanner.moveNext();
			scanner.moveNext();
			return factory.newMethodInvocationExpr(methodName.value);
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
			return exprOpt(factory.newEqualsExpr(expr, equalsExpr));
		case NEQ:
			scanner.moveNext();
			Expression notEqualsExpr = sExpr();
			return exprOpt(factory.newNotEqualsExpr(expr, notEqualsExpr));
		case LT:
			scanner.moveNext();
			Expression ltExpr = sExpr();
			return exprOpt(factory.newLessThanExpr(expr, ltExpr));
		case GT:
			scanner.moveNext();
			Expression gtExpr = sExpr();
			return exprOpt(factory.newGreaterThanExpr(expr, gtExpr));
		}
		return expr;
	}
}
