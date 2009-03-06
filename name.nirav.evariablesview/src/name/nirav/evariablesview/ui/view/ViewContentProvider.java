/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.ui.view;

import java.util.Collection;

import name.nirav.evariablesview.core.serializable.java.DebugVariable;
import name.nirav.opath.Variable;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	private Collection<Variable> result;

	/**
	 * @param pathFilteredView
	 */
	ViewContentProvider(OPathFilteredView pathFilteredView) {
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object parent) {
		if (parent instanceof Collection) {
			this.result = (Collection<Variable>) parent;
			return this.result.toArray();
		}

		return getChildren(parent);
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof DebugVariable) {
			DebugVariable parentVar = (DebugVariable) parent;
			if (parentVar.hasChildren()) {
				return parentVar.getChildren().toArray();

			}
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof DebugVariable) {
			DebugVariable var = (DebugVariable) parent;
			return var.hasChildren();
		}
		return false;
	}

	public Object getParent(Object child) {
		return null;
	}

}