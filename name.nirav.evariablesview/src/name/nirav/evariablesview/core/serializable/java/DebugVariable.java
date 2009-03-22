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

import name.nirav.evariablesview.Activator;
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
			Activator.log(e);
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
			Activator.log(e);
		}
		return false;
	}

	public IJavaModifiers getModifiers() {
		return (IJavaModifiers) var.getAdapter(IJavaModifiers.class);
	}

	public Object getType() {
		try {
			return var.getJavaType() == null ? null : var.getJavaType().getName();
		} catch (DebugException e) {
			Activator.log(e);
		}
		return var;
	}

	@Override
	public int hashCode() {
		return ((var == null) ? 0 : var.hashCode());
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
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (var.hashCode() != other.var.hashCode())
			return false;
		if (!var.equals(other.var))
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
			Activator.log(e);
		}
		return super.toString();
	}
	
	public JDIVariable getJDIVar() {
		return var;
	}

}
