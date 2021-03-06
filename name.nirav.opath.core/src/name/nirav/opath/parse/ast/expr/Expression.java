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
public abstract class Expression {

	private Object value;

	public Expression(Object value) {
		this.setValue(value);
	}

	public Expression() {
	}
	public abstract void accept(ExpressionVisitor visitor);

	public abstract Object evaluate(Variable context);

	@Override
	public String toString() {
		return "(" + getValue().toString() + ")";
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
}
