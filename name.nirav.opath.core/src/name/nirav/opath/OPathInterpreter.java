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

import name.nirav.opath.parse.OPathParser;
import name.nirav.opath.parse.ast.ASTAllTestStep;
import name.nirav.opath.parse.ast.ASTAttributeStep;
import name.nirav.opath.parse.ast.ASTCurrentStep;
import name.nirav.opath.parse.ast.ASTParentStep;
import name.nirav.opath.parse.ast.ASTPredicateStep;
import name.nirav.opath.parse.ast.ASTQNameStep;
import name.nirav.opath.parse.ast.ASTStep;
import name.nirav.opath.parse.ast.ASTVisitor;
import name.nirav.opath.parse.ast.OPathASTFactory;
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
	protected static final SimpleNameMatcher SIMPLE_NAME_MATCHER = new SimpleNameMatcher();
	protected Collection<? extends Variable> context;
	protected Collection<Variable> filtered;
	protected Collection<Variable> tempStepList;
	protected boolean matchAllDescendants;
	protected Variable predicateContext;
	protected Boolean exprResult = Boolean.FALSE;
	protected OPathParser pathParser;
	protected OPathASTFactory astFactory;
	private IProgressListener progressListener;
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
		this.context = Arrays.asList(new Variable[] { context });
		filtered.add(context);
		interprete(expr);
		return this;
	}

	protected void interprete(String expr) {
		ASTStep parse = getParser().parse(expr, this.getASTFactory());
		getProgressListener().beginTask("OPath Interpretation", count(parse));
		int progress = 0;
		while (true) {
			if(getProgressListener().isCanceled())
				break;
			parse = parse.getNext();
			if (parse == null)
				break;
			matchAllDescendants = parse.isMultilevel();
			if (parse.getType() == StepType.SlashStep || parse.getType() == StepType.Predicate)
				CycleDetector.getInstance().clear();
			if (parse.getType() == StepType.SlashStep && matchAllDescendants) {
				parse = parse.getNext();
			}
			getProgressListener().setTaskName("Executing " + parse);
			parse.accept(this);
			getProgressListener().worked(progress++);
			filtered.clear();
			for (Variable variable : tempStepList) {
				if(getProgressListener().isCanceled())
					break;
				filtered.add(variable);
			}
		}
		getProgressListener().done();
	}

	private int count(ASTStep parse) {
		parse = parse.getNext();
		int cnt = 1;
		while((parse = parse.getNext()) != null)
			cnt++;
		return cnt;
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
			if(getProgressListener().isCanceled())
				return;
			List<Variable> children = var.getChildren();
			for (Variable variable : children) {
				if(getProgressListener().isCanceled())
					return;
				this.predicateContext = variable;
				exprResult = Boolean.FALSE;
				pExpr.accept(this);
				if (exprResult.booleanValue()) {
					CycleDetector.getInstance().acyclicAdd(var);
					if (!CycleDetector.getInstance().wasCycleDetected())
						tempStepList.add(var);
				}
			}
		}
		CycleDetector.getInstance().clear();
		CycleDetector.getInstance().clearCycleFlag();
	}

	protected void evaluateExpression(Expression pExpr) {
		Object evaluation = pExpr.evaluate(this.predicateContext);
		this.exprResult = evaluation instanceof Boolean ? (Boolean) evaluation : Boolean.TRUE;
	}

	public Boolean evaluateArrayIndexer(NumberExpression expr) {
		Integer value = (Integer) expr.getValue();
		return this.predicateContext.getName().equals("[" + value + "]") ? Boolean.TRUE
				: Boolean.FALSE;
	}

	public Boolean evaluateQName(QNameExpression expr) {
		return this.predicateContext.getName().equals(expr.getValue()) ? Boolean.TRUE
				: Boolean.FALSE;

	}

	protected void matchNodeSetIterative(ASTStep step, Variable var,
			IMatchingStrategy matchingStrategy) {
		Variable variable = var;
		List<Variable> children = new LinkedList<Variable>();
		children.addAll(variable.getChildren());
		while (children != null && children.size() != 0) {
			List<Variable> variables = new LinkedList<Variable>();
			if(getProgressListener().isCanceled())
				return;
			for (Variable var1 : children) {
				if(getProgressListener().isCanceled())
					return;
				CycleDetector.getInstance().acyclicAdd(var1);
				if (CycleDetector.getInstance().wasCycleDetected()) {
					System.out.println("Pruning from : " + buildList(var1));
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
				name = var1.getName();
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
	}

	public OPathParser getParser() {
		if (pathParser == null)
			pathParser = new OPathParser();
		return pathParser;
	}

	public void setPathParser(OPathParser pathParser) {
		this.pathParser = pathParser;
	}

	public void setASTFactory(OPathASTFactory astFactory) {
		this.astFactory = astFactory;
	}

	public OPathASTFactory getASTFactory() {
		return astFactory;
	}

	public void visit(EqualsExpression expr) {
		evaluateExpression(expr);
	}

	public void visit(NotEqualsExpression expr) {
		evaluateExpression(expr);
	}

	public void visit(GreaterThanExpression expr) {
		evaluateExpression(expr);
	}

	public void visit(LessThanExpression expr) {
		evaluateExpression(expr);
	}

	public void visit(LiteralExpression expr) {
		Value value = predicateContext.getValue();
		if (value != null) {
			if (value.getValue() != null) {
				if (value.getComparableValue() instanceof String) {
					String val = (String) value.getComparableValue();
					if (expr.isRegEx())
						this.exprResult = val.matches((String) expr.getValue());
					else
						this.exprResult = val.equals(expr.getValue());
				}
			}
		}
	}

	public void visit(MethodInvocationExpression expr) {
		evaluateExpression(expr);
	}

	public void visit(NumberExpression expr) {
		exprResult = evaluateArrayIndexer(expr);
	}

	public void visit(QNameExpression expr) {
		exprResult = evaluateQName(expr);
	}

	public void setProgressListener(IProgressListener listener) {
		this.progressListener = listener;
	}

	public IProgressListener getProgressListener() {
		if (progressListener == null) {
			progressListener = new DefaultProgressListener();
		}
		return progressListener;
	}

}
