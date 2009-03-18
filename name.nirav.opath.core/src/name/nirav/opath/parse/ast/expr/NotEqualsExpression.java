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
public class NotEqualsExpression extends EqualsExpression {

	public NotEqualsExpression(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	@Override
	public String toString() {
		return getLeftHandSide() + " != " + getRightHandSide();
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	protected Object compare(Object evaluate, Object evaluate2) {
		if (evaluate instanceof Number && evaluate2 instanceof Number) {
			return ((Number) evaluate).doubleValue() != ((Number) evaluate2).doubleValue();
		}
		return !evaluate.equals(evaluate2);
	}

}
