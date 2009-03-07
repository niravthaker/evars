/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.core.serializable.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.nirav.opath.Value;
import name.nirav.opath.Variable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaModifiers;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;

/**
 * @author Nirav Thaker
 * 
 */
@SuppressWarnings("restriction")
public class DebugValue extends Value {

	private final IValue value;
	private final DebugVariable holder;

	public DebugValue(IValue value, DebugVariable holder) {
		this.value = value;
		this.holder = holder;
	}

	@Override
	public Object getValue() {
		try {
			return value.getValueString();
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Variable> getVariables() {
		IVariable[] vars;
		List<Variable> lst = Collections.emptyList();
		try {
			vars = value.getVariables();
			if (vars == null)
				return lst;
			lst = new ArrayList<Variable>(vars.length);
			for (IVariable variable : vars) {
				IJavaModifiers adapter = (IJavaModifiers) variable.getAdapter(IJavaModifiers.class);
				if (adapter.isFinal() && adapter.isStatic())
					continue;
				DebugVariable var = DebugVariable.create((JDIVariable) variable);
				if (var != null) {
					var.setParent(holder);
					lst.add(var);
				}
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return lst;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DebugValue other = (DebugValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (value.hashCode() != other.value.hashCode())
			return false;
		if(!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (String) getValue();
	}

}
