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
public interface ExpressionVisitor {
	public void visit(EqualsExpression expr);
	public void visit(NotEqualsExpression expr);
	public void visit(GreaterThanExpression expr);
	public void visit(LessThanExpression expr);
	public void visit(LiteralExpression expr);
	public void visit(MethodInvocationExpression expr);
	public void visit(NumberExpression expr);
	public void visit(QNameExpression expr);
}
