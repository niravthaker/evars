/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;

import name.nirav.opath.parse.ast.expr.Expression;



public class ASTPredicateStep extends ASTStep {
	private Expression expr;
	public void setExpr(Expression expr) {
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}

	@Override
	public StepType getType() {
		return StepType.Predicate;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public String getQname() {
		return toString();
	}
	@Override
	public String toString() {
		return "[" + expr + ']';
	}
}