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
 * 
 * 
 */
public class OPathASTFactory implements ExpressionFactory {

	public static OPathASTFactory getInstance() {
		return new OPathASTFactory();
	}

	public ASTStep createStartStep() {
		ASTStep step = new ASTStep();
		step.setType(StepType.Start);
		return step;
	}

	public ASTStep createCurrentStep(ASTStep prevStep) {
		ASTStep step = new ASTCurrentStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}

	public ASTStep createParentStep(ASTStep prevStep) {
		ASTStep step = new ASTParentStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}

	public ASTStep createRootContext(ASTStep prevStep) {
		ASTStep step = new ASTRootContextStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}

	public ASTStep createAllTestStep(ASTStep prevStep) {
		ASTStep step = new ASTAllTestStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}

	public ASTStep createQNameStep(ASTStep prevStep, String qname) {
		ASTStep step = new ASTQNameStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		step.setQname(qname);
		return step;
	}

	public ASTStep createAttributeStep(ASTStep prevStep, String qname) {
		ASTAttributeStep step = new ASTAttributeStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		step.setQname(qname);
		return step;
	}

	public ASTPredicateStep createPredicateStep(ASTStep step) {
		ASTPredicateStep predicateStep = new ASTPredicateStep();
		step.setNext(predicateStep);
		predicateStep.setPrevious(step);
		predicateStep.setType(StepType.Predicate);
		return predicateStep;
	}

	public EqualsExpression newEqualsExpr(Expression lhs, Expression rhs) {
		return new EqualsExpression(lhs, rhs);
	}

	public GreaterThanExpression newGreaterThanExpr(Expression lhs, Expression rhs) {
		return new GreaterThanExpression(lhs, rhs);
	}

	public LessThanExpression newLessThanExpr(Expression lhs, Expression rhs) {
		return new LessThanExpression(lhs, rhs);
	}

	public LiteralExpression newLiteralExpr(Object value) {
		return new LiteralExpression(value);
	}

	public MethodInvocationExpression newMethodInvocationExpr(Object value) {
		return new MethodInvocationExpression(value);
	}

	public NotEqualsExpression newNotEqualsExpr(Expression lhs, Expression rhs) {
		return new NotEqualsExpression(lhs, rhs);
	}

	public NumberExpression newNumberExpr(Object value) {
		return new NumberExpression(value);
	}

	public QNameExpression newQNameExpr(Object value) {
		return new QNameExpression(value);
	}

}
