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
import java.util.List;

public class Value {
	private List<Variable> variables;
	private Object value;

	public Value() {
	}

	public void addVariable(Variable var) {
		if (variables == null) {
			variables = new ArrayList<Variable>();
		}
		variables.add(var);
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		visitor.enter(this);
		if (variables != null) {
			for (Variable var : variables) {
				var.accept(visitor);
			}
		}
		visitor.exit(this);
	}
	public Object getComparableValue() {
		return this.value;
	}
	@Override
	public String toString() {
		return "val=" + variables;
	}

}