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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

/**
 * 
 * 
 */
public class OPathInterpreter extends ASTVisitor {
	private static final SimpleNameMatcher SIMPLE_NAME_MATCHER = new SimpleNameMatcher();
	protected Collection<? extends Variable> context;
	protected Collection<Variable> filtered = new HashSet<Variable>();
	protected Collection<Variable> tempStepList = new HashSet<Variable>();
	private boolean matchAllDescendants;

	interface IMatchingStrategy {
		public boolean match(Variable var, ASTStep step);
	}

	public static final class SimpleNameMatcher implements IMatchingStrategy {
		@Override
		public boolean match(Variable var, ASTStep step) {
			return var.getName().equals(step.getQname());
		}
	}

	public Collection<Variable> getResult() {
		return filtered;
	}

	public OPathInterpreter evaluate(String expr, Variable context) {
		CycleDetector.getInstance().clear();
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
			if (parse.getType() == StepType.RootContext)
				CycleDetector.getInstance().clear();
			if (parse.getType() == StepType.RootContext && matchAllDescendants) {
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
				@Override
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
				@Override
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
		if (step.getQname().startsWith("[")) {
			tempStepList.clear();
			for (Variable var : this.filtered) {
				matchNodeSet(step, var, SIMPLE_NAME_MATCHER);
			}
		}

	}

	protected void matchNodeSetIterative(ASTStep step, Variable var,
			IMatchingStrategy matchingStrategy) {
		Variable variable = var;
		Set<Variable> children = new HashSet<Variable>();
		children.addAll(variable.getChildren());
		while (children != null && children.size() != 0) {
			Set<Variable> variables = new HashSet<Variable>();
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
				children = new HashSet<Variable>(variables);
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
				name = var1.getName() ;//+ ":" + var1.getValue().getValue();
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
	protected void matchNodeSetRecursive(ASTStep step, Variable var,
			IMatchingStrategy matchingStrategy) {
		Set<Variable> children = new HashSet<Variable>(var.getChildren());
		for (Variable variable : children) {
			if (matchingStrategy.match(variable, step)) {
				if (variable.getValue() != null)
					CycleDetector.getInstance().acyclicAdd(variable);
				if (!CycleDetector.getInstance().wasCycleDetected()) {
					tempStepList.add(variable);
				} else {
					CycleDetector.getInstance().clearCycleFlag();
					return;
				}
			}
			if (matchAllDescendants && !CycleDetector.getInstance().wasCycleDetected()) {
				matchNodeSet(step, variable, matchingStrategy);
			}
		}
	}
	 */
}
