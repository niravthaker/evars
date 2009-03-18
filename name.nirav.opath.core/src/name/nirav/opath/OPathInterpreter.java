/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import name.nirav.opath.parse.OPathParser;
import name.nirav.opath.parse.ast.ASTAllTestStep;
import name.nirav.opath.parse.ast.ASTAttributeStep;
import name.nirav.opath.parse.ast.ASTCurrentStep;
import name.nirav.opath.parse.ast.ASTParentStep;
import name.nirav.opath.parse.ast.ASTPredicateStep;
import name.nirav.opath.parse.ast.ASTQNameStep;
import name.nirav.opath.parse.ast.ASTStep;
import name.nirav.opath.parse.ast.ASTVisitor;
import name.nirav.opath.parse.ast.StepType;
import name.nirav.opath.parse.ast.expr.EqualsExpression;
import name.nirav.opath.parse.ast.expr.Expression;
import name.nirav.opath.parse.ast.expr.ExpressionVisitor;
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
public class OPathInterpreter extends ASTVisitor implements ExpressionVisitor {
	private static final SimpleNameMatcher SIMPLE_NAME_MATCHER = new SimpleNameMatcher();
	protected Collection<? extends Variable> context;
	protected Collection<Variable> filtered;
	protected Collection<Variable> tempStepList;
	private boolean matchAllDescendants;
	private Variable predicateContext;
	private Stack<Boolean> exprResultStack;

	interface IMatchingStrategy {
		public boolean match(Variable var, ASTStep step);
	}

	public static final class SimpleNameMatcher implements IMatchingStrategy {
		public boolean match(Variable var, ASTStep step) {
			return var.getName().equals(step.getQname());
		}
	}

	public Collection<Variable> getResult() {
		return filtered;
	}

	public OPathInterpreter evaluate(String expr, Variable context) {
		CycleDetector.getInstance().clear();
		filtered = new LinkedList<Variable>();
		tempStepList = new LinkedList<Variable>();
		this.exprResultStack = new Stack<Boolean>();
		this.context = Arrays.asList(new Variable[] { context });
		filtered.add(context);
		interprete(expr);
		return this;
	}

	protected void interprete(String expr) {
		ASTStep parse = new OPathParser().parse(expr);
		while (true) {
			parse = parse.getNext();
			if (parse == null)
				break;
			matchAllDescendants = parse.isMultilevel();
			if (parse.getType() == StepType.SlashStep || parse.getType() == StepType.Predicate)
				CycleDetector.getInstance().clear();
			if (parse.getType() == StepType.SlashStep && matchAllDescendants) {
				parse = parse.getNext();
			}
			parse.accept(this);
			filtered.clear();
			for (Variable variable : tempStepList) {
				filtered.add(variable);
			}
		}
	}

	@Override
	public void visit(ASTQNameStep step) {
		tempStepList.clear();
		for (Variable var : this.filtered) {
			matchNodeSet(step, var, SIMPLE_NAME_MATCHER);
		}
	}

	@Override
	public void visit(ASTAllTestStep step) {
		tempStepList.clear();
		for (Variable var : this.filtered) {
			matchNodeSet(step, var, new IMatchingStrategy() {
				public boolean match(Variable var, ASTStep step) {
					return true;
				}
			});
		}
	}

	@Override
	public void visit(ASTAttributeStep step) {
		tempStepList.clear();
		for (Variable var : filtered) {
			matchNodeSet(step, var, new IMatchingStrategy() {
				public boolean match(Variable var, ASTStep step) {
					return var.getName().matches(step.getQname());
				}
			});
		}
	}

	@Override
	public void visit(ASTCurrentStep step) {
		tempStepList.clear();
		tempStepList.addAll(context);
	}

	@Override
	public void visit(ASTParentStep step) {
		tempStepList.clear();
		for (Variable var : filtered) {
			Variable parent = var.getParent();
			if (parent != null && !tempStepList.contains(parent)) {
				tempStepList.add(parent);
			}
		}
	}

	@Override
	public void visit(ASTPredicateStep step) {
		tempStepList.clear();
		Expression pExpr = step.getExpr();
		for (Variable var : this.filtered) {
			List<Variable> children = var.getChildren();
			for (Variable variable : children) {
				this.predicateContext = variable;
				pExpr.accept(this);
				Boolean pop = this.exprResultStack.pop();
				if (pop.booleanValue()) {
					tempStepList.add(variable);
				}
			}
		}
	}

	protected void matchNodeSetIterative(ASTStep step, Variable var,
			IMatchingStrategy matchingStrategy) {
		Variable variable = var;
		List<Variable> children = new LinkedList<Variable>();
		children.addAll(variable.getChildren());
		while (children != null && children.size() != 0) {
			List<Variable> variables = new LinkedList<Variable>();
			for (Variable var1 : children) {
				CycleDetector.getInstance().acyclicAdd(var1);
				if (CycleDetector.getInstance().wasCycleDetected()) {
					System.out.println("Pruning from : " + buildList(var1));
					// variables.clear();
					CycleDetector.getInstance().clearCycleFlag();
					continue;
				}
				if (matchingStrategy.match(var1, step)) {
					tempStepList.add(var1);
				}
				if (matchAllDescendants) {
					List<Variable> list = var1.getChildren();
					if (list != null) {
						variables.addAll(list);
					}
				}
			}
			if (variables != null) {
				children = new LinkedList<Variable>(variables);
			}
		}
	}

	public static String buildList(Variable var1) {
		List<String> stck = new ArrayList<String>();
		String name = var1.getName();
		while (name != null && var1 != null) {
			stck.add(name);
			var1 = var1.getParent();
			if (var1 != null) {
				name = var1.getName();// + ":" + var1.getValue().getValue();
			}
		}
		Collections.reverse(stck);
		Iterator<String> elements = stck.iterator();
		StringBuilder builder = new StringBuilder();
		while (elements.hasNext()) {
			String string = (String) elements.next();
			builder.append(string);
			if (elements.hasNext())
				builder.append(" => ");
		}
		return builder.toString();
	}

	protected void matchNodeSet(ASTStep step, Variable var, IMatchingStrategy matchingStrategy) {
		matchNodeSetIterative(step, var, matchingStrategy);
		// matchNodeSetRecursive(step, var, matchingStrategy);
	}

	/*
	 * protected void matchNodeSetRecursive(ASTStep step, Variable var,
	 * IMatchingStrategy matchingStrategy) { Set<Variable> children = new
	 * HashSet<Variable>(var.getChildren()); for (Variable variable : children)
	 * { if (matchingStrategy.match(variable, step)) { if (variable.getValue()
	 * != null) CycleDetector.getInstance().acyclicAdd(variable); if
	 * (!CycleDetector.getInstance().wasCycleDetected()) {
	 * tempStepList.add(variable); } else {
	 * CycleDetector.getInstance().clearCycleFlag(); return; } } if
	 * (matchAllDescendants && !CycleDetector.getInstance().wasCycleDetected())
	 * { matchNodeSet(step, variable, matchingStrategy); } } }
	 */

	public void visit(EqualsExpression expr) {
		Object evaluate = expr.evaluate(predicateContext);
		if (evaluate instanceof Boolean) {
			this.exprResultStack.push((Boolean) evaluate);
		} else
			this.exprResultStack.push(Boolean.TRUE);
	}

	public void visit(NotEqualsExpression expr) {
		Object evaluate = expr.evaluate(predicateContext);
		if (evaluate instanceof Boolean) {
			this.exprResultStack.push((Boolean) evaluate);
		} else
			this.exprResultStack.push(Boolean.TRUE);
	}

	public void visit(GreaterThanExpression expr) {
		System.out.println(expr);
	}

	public void visit(LessThanExpression expr) {
		System.out.println(expr);
	}

	public void visit(LiteralExpression expr) {
		System.out.println(expr);
	}

	public void visit(MethodInvocationExpression expr) {
		System.out.println(expr);
	}

	public void visit(NumberExpression expr) {
		Integer value = (Integer) expr.getValue();
		if (this.predicateContext.getName().equals("[" + value + "]"))
			this.exprResultStack.push(Boolean.TRUE);
		else
			this.exprResultStack.push(Boolean.FALSE);
	}

	public void visit(QNameExpression expr) {
		String name = (String) expr.getValue();
		if (this.predicateContext.getName().equals(name)) {
			this.exprResultStack.push(Boolean.TRUE);
		}
		if (exprResultStack.size() == 0) {
			this.exprResultStack.push(Boolean.FALSE);
		}

	}
}
