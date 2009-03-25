/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

import java.util.List;

public class Variable {

	private final String name;
	private Value value;
	private Variable parent;

	public void setParent(Variable parent) {
		this.parent = parent;
	}

	public Variable getParent() {
		return parent;
	}

	public List<Variable> getChildren() {
		return value == null ? null : value.getVariables();
	}

	public Variable(String string, Variable parent) {
		this.name = string;
		this.parent = parent;
	}

	public Variable(String string) {
		this.name = string;
	}

	public String getName() {
		return name;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		if (getValue() != null)
			value.accept(visitor);
	}

	@Override
	public String toString() {
		return name;
	}

}