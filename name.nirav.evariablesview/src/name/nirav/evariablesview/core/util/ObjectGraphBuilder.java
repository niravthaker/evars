/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import name.nirav.evariablesview.core.serializable.java.DebugVariable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;
import org.eclipse.jface.viewers.ITreeSelection;

/**
 * @author nrulezz
 * 
 */
@SuppressWarnings("restriction")
public class ObjectGraphBuilder {
	private List<DebugVariable> varList;
	private Object lastSelection;

	public ObjectGraphBuilder() {
		varList = new ArrayList<DebugVariable>();
	}

	public List<DebugVariable> buildFromSelection(ITreeSelection selection, JDIStackFrame input) {
		Iterator<?> iterator = null;
		if (!(selection).isEmpty()) {
			iterator = selection.iterator();
			if (lastSelection != null && lastSelection.equals(iterator))
				return this.varList;
		} else {
			JDIStackFrame frame = input;
			try {
				IVariable[] variables = frame.getVariables();
				if (variables != null) {
					iterator = Arrays.asList(variables).iterator();
				}
			} catch (DebugException e) {
			}
		}
		this.varList = new ArrayList<DebugVariable>();
		iterate(iterator);
		lastSelection = ((ITreeSelection) selection);
		return varList;
	}

	private void iterate(Iterator<?> iterator) {
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next instanceof IDebugElement) {
				IDebugElement debugElement = (IDebugElement) next;
				JDIVariable var = (JDIVariable) debugElement.getAdapter(JDIVariable.class);
				processVariable(var);
			}
		}
	}

	private void processVariable(JDIVariable var) {
		if (var == null)
			return;
		DebugVariable root;
		try {
			root = DebugVariable.create(var);
			varList.add(root);
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

}
