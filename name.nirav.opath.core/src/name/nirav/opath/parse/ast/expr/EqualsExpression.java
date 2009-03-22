/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast.expr;

import name.nirav.opath.Variable;

/**
 * @author Nirav Thaker
 * 
 */
public class EqualsExpression extends Expression {

	private final Expression lhs;
	private final Expression rhs;

	public EqualsExpression(Expression expr, Expression rhs) {
		this.lhs = expr;
		this.rhs = rhs;
	}

	public Expression getLeftHandSide() {
		return lhs;
	}

	public Expression getRightHandSide() {
		return rhs;
	}

	@Override
	public String toString() {
		return "{" + getLeftHandSide() + "} = {" + getRightHandSide() + "}";
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Object evaluate(Variable context) {
		Object evaluate = getLeftHandSide().evaluate(context);
		evaluate = tryParsing(evaluate);
		Object evaluate2 = getRightHandSide().evaluate(context);
		evaluate2 = tryParsing(evaluate2);
		return compare(evaluate, evaluate2);
	}

	protected Object compare(Object lhsEval, Object rhsEval) {
		if (lhsEval instanceof Number && rhsEval instanceof Number) {
			return ((Number) lhsEval).doubleValue() == ((Number) rhsEval).doubleValue();
		}
		if (lhsEval instanceof String && rhsEval instanceof String) {
			String str1 = (String) lhsEval;
			String str2 = (String) rhsEval;
			if (str1 != null && str2 != null) {
				if (lhs instanceof LiteralExpression) {
					LiteralExpression expr = (LiteralExpression) lhs;
					if (expr.isRegEx())
						return str2.matches(str1);
				} else if (rhs instanceof LiteralExpression) {
					LiteralExpression expr = (LiteralExpression) rhs;
					if (expr.isRegEx())
						return str1.matches(str2);
				}
			}
		}
		return lhsEval.equals(rhsEval);
	}

	protected Object tryParsing(Object evaluate) {
		if (evaluate instanceof String) {
			String nm = (String) evaluate;
			try {
				evaluate = Long.valueOf(nm);
			} catch (NumberFormatException e) {
			}
		}
		return evaluate;
	}
}
