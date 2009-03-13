/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse;

import java.util.ArrayList;
import java.util.List;

import name.nirav.opath.parse.Scanner.Token;
import name.nirav.opath.parse.Scanner.Type;
import name.nirav.opath.parse.ast.ASTPredicateStep;
import name.nirav.opath.parse.ast.ASTStep;
import name.nirav.opath.parse.ast.OPathAST;

/**
 * 
 * 
 */
public class OPathParser {

	private Scanner scanner;

	public ASTStep parse(String expression) {
		this.scanner = new Scanner(expression);
		scanner.moveNext();
		ASTStep start = OPathAST.createStartStep();
		iterate(start);
		Token eof = scanner.getCurrentToken();
		if (eof.type != Type.EOF)
			throw new IllegalStateException("Malformed expression.");
		return start;
	}

	public ASTStep parse(Scanner scanner, String expression) {
		this.scanner = scanner;
		return parse(expression);
	}

	private void showError(Token token, Type... expected) {
		String msgForExpected = " when expecting one of the following : ";
		String string = "Invalid token " + token;
		List<String> tokenList = new ArrayList<String>();
		for (Type type : expected)
			tokenList.add(type.toString());
		if (tokenList.size() > 0)
			throw new IllegalStateException(string + msgForExpected + tokenList);

	}

	protected ASTStep iterate(ASTStep instep) {
		Token token = scanner.getCurrentToken();
		switch (token.type) {
		case SLASH:
			return relativeLP(absoluteLP(instep));
		case DSLASH:
			return relativeLP(absoluteLP(instep));
		case ATR:
		case DOT:
		case DOTDOT:
		case STAR:
			return relativeLP(instep);
		case QNAME:
			return relativeLP(instep);
		default:
			showError(token, Type.SLASH, Type.DSLASH, Type.STAR, Type.QNAME, Type.ATR);
		}
		throw new IllegalStateException("Ran out of choices for LP.");
	}

	protected ASTStep absoluteLP(ASTStep instep) {
		Token token = scanner.getCurrentToken();
		switch (token.type) {
		case SLASH:
		case DSLASH:
			scanner.moveNext();
			ASTStep step = createPathStep(instep, token);
			return relativeLP(step);
		}
		return instep;
	}

	private ASTStep createPathStep(ASTStep instep, Token token) {
		ASTStep step = OPathAST.createRootContext(instep);
		step.setAbsolute(token.type == Type.SLASH);
		step.setMultilevel(token.type == Type.DSLASH);
		return step;
	}

	protected ASTStep relativeLP(ASTStep step) {
		return relativeLPOpt(step(step));
	}

	protected ASTStep relativeLPOpt(ASTStep next) {
		Token token = scanner.getCurrentToken();
		switch (token.type) {
		case SLASH:
		case DSLASH:
			scanner.moveNext();
			return relativeLP(step(createPathStep(next, token)));
		case EOF:
			return next;
		default:
			showError(token, Type.SLASH, Type.DSLASH);
		}
		return next;

	}

	protected ASTStep step(ASTStep step) {
		Token token = scanner.getCurrentToken();
		switch (token.type) {
		case DOT:
			scanner.moveNext();
			return OPathAST.createCurrentStep(step);
		case DOTDOT:
			scanner.moveNext();
			return OPathAST.createParentStep(step);
		case LSQBR:
			scanner.moveNext();
			return buildPredicate(OPathAST.createPredicateStep(step));
		case QNAME:
			scanner.moveNext();
			return step(OPathAST.createQNameStep(step, (String) token.value));
		case STAR:
			scanner.moveNext();
			return OPathAST.createAllTestStep(step);
		case ATR:
			scanner.moveNext();
			return OPathAST.createAttributeStep(step, (String) token.value);
		}
		return step;
	}

	private ASTStep buildPredicate(ASTPredicateStep predicate) {
		predicate.setExpr(new PredicateExpressionParser(scanner).parse());
		checkBracketEnd(scanner.getCurrentToken());
		scanner.moveNext();
		return predicate;
		/*
		switch (token.type) {
		case LITERAL:
			Token next = scanner.moveNext();
			predicate.setExpr(next.value);
			next = scanner.moveNext();
			if (next.type != Type.LITERAL)
				throw new IllegalArgumentException(
						"Literal didn't end properly in predicate, found : " + next.type);
			next = scanner.moveNext();
			checkBracketEnd(next);
			scanner.moveNext();
			return predicate;
		case NUMBER:
			next = scanner.moveNext();
			checkBracketEnd(next);
			scanner.moveNext();
			predicate.setExpr(token.value);
			return predicate;
		case QNAME:
			next = scanner.moveNext();
			checkBracketEnd(next);
			scanner.moveNext();
			predicate.setExpr(token.value);
			return predicate;
		default:
			throw new IllegalArgumentException("Invalid predicate expression");
		}
		*/
	}

	private void checkBracketEnd(Token next) {
		if (next.type != Type.RSQBR)
			throw new IllegalArgumentException("Predicate didn't end with ], found " + next.type);
	}

}
