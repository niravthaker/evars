/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.core.serializable.java;

import java.util.Collections;
import java.util.List;

import name.nirav.opath.Value;
import name.nirav.opath.Variable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaModifiers;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;

/**
 * @author Nirav Thaker
 * 
 */
@SuppressWarnings("restriction")
public class DebugVariable extends Variable {
	private final JDIVariable var;
	private IValue jdiValue;

	public DebugVariable(String string, JDIVariable var) {
		super(string);
		this.var = var;
		try {
			if (var != null) {
				jdiValue = var.getValue();
				setValue(new DebugValue(jdiValue, this));
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	public static DebugVariable create(JDIVariable var) throws DebugException {
		return new DebugVariable(var.getName(), var);
	}

	@Override
	public List<Variable> getChildren() {
		Value value = getValue();
		return value == null ? Collections.<Variable> emptyList() : value.getVariables();
	}

	public boolean hasChildren() {
		try {
			return this.jdiValue != null && this.jdiValue.getVariables() != null
					&& this.jdiValue.getVariables().length != 0;
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return false;
	}

	public IJavaModifiers getModifiers() {
		return (IJavaModifiers) var.getAdapter(IJavaModifiers.class);
	}

	public Object getType() {
		try {
			return var.getJavaType().getName();
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return var;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jdiValue == null) ? 0 : jdiValue.hashCode());
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		DebugVariable other = (DebugVariable) obj;
		if (jdiValue == null) {
			if (other.jdiValue != null)
				return false;
		} else if (!jdiValue.equals(other.jdiValue))
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}

	@Override
	public String toString() {
		try {
			if (var != null) {
				return var.getName();
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return super.toString();
	}

}
