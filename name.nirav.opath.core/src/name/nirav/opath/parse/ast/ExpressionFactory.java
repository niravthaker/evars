/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;

import name.nirav.opath.parse.ast.expr.EqualsExpression;
import name.nirav.opath.parse.ast.expr.Expression;
import name.nirav.opath.parse.ast.expr.GreaterThanExpression;
import name.nirav.opath.parse.ast.expr.LessThanExpression;
import name.nirav.opath.parse.ast.expr.LiteralExpression;
import name.nirav.opath.parse.ast.expr.MethodInvocationExpression;
import name.nirav.opath.parse.ast.expr.NotEqualsExpression;
import name.nirav.opath.parse.ast.expr.NumberExpression;
import name.nirav.opath.parse.ast.expr.QNameExpression;

/**
 * Factory to create implementations for expression tokens.
 * 
 * @author Nirav Thaker
 * 
 */
public interface ExpressionFactory {
	public EqualsExpression newEqualsExpr(Expression lhs, Expression rhs);

	public GreaterThanExpression newGreaterThanExpr(Expression lhs, Expression rhs);

	public LessThanExpression newLessThanExpr(Expression lhs, Expression rhs);

	public NotEqualsExpression newNotEqualsExpr(Expression lhs, Expression rhs);

	public QNameExpression newQNameExpr(Object value);

	public NumberExpression newNumberExpr(Object value);

	public LiteralExpression newLiteralExpr(Object value, boolean isRegEx);

	public MethodInvocationExpression newMethodInvocationExpr(Object value);
}
