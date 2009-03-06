/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;



public class ASTPredicateStep extends ASTStep {
	private Object expr;
	public void setExpr(Object expr) {
		this.expr = expr;
	}
	
	public Object getExpr() {
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