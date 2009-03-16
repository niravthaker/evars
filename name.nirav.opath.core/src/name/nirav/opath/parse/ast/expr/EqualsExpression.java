/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast.expr;


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
		return lhs + " = " + rhs;
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
