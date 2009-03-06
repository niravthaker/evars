/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import name.nirav.opath.parse.ast.StepType;

/**
 * @author nrulezz
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

	public void evaluate(String expr, Variable context) {
		CycleDetector.getInstance().clear();
		this.context = Arrays.asList(new Variable[] { context });
		filtered.add(context);
		interprete(expr);
	}

	protected void interprete(String expr) {
		ASTStep parse = new OPathParser().parse(expr);
		while (true) {
			parse = parse.getNext();
			if (parse == null)
				break;
			matchAllDescendants = parse.isMultilevel();
			if (parse.getType() == StepType.RootContext && matchAllDescendants)
				parse = parse.getNext();
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

	protected void matchNodeSet(ASTStep step, Variable var, IMatchingStrategy matchingStrategy) {
		List<Variable> children = var.getChildren();
		if (children != null) {
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
	}

}
