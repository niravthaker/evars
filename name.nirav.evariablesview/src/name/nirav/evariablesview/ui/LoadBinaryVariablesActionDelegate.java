/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package name.nirav.evariablesview.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.nirav.evariablesview.Activator;
import name.nirav.evariablesview.core.serializable.java.DebugVariable;
import name.nirav.evariablesview.core.util.SerializerUtils;
import name.nirav.evariablesview.core.util.ValueFactory;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

@SuppressWarnings("restriction")
public class LoadBinaryVariablesActionDelegate implements IViewActionDelegate {

	private VariablesView view;
	private List<DebugVariable> vlist = new ArrayList<DebugVariable>();

	public void init(IViewPart view) {
		this.view = (VariablesView) view;
	}

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		FileDialog dialog = new FileDialog(this.view.getViewSite().getShell(),
				SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.bvars" });
		String fileToOpen = dialog.open();
		if (fileToOpen != null && !fileToOpen.trim().isEmpty()) {
			File file = new File(fileToOpen);
			if (file.exists()) {
				try {
					this.vlist = (List<DebugVariable>) SerializerUtils
							.deserialize(file);
				} catch (IOException e) {
					ErrorDialog.openError(this.view.getSite().getShell(),
							"Load Variables Failed",
							"Can't load variables from file", Activator
									.getErrorStatus(e));
				} catch (ClassNotFoundException e) {
					ErrorDialog.openError(this.view.getSite().getShell(),
							"Load Variables Failed",
							"One of more classes were not found in classpath",
							Activator.getErrorStatus(e));
				}
				loadVariablesInDebugger(this.vlist);
			}

		}
	}

	private void loadVariablesInDebugger(List<DebugVariable> vlist2) {
		for (DebugVariable variable : vlist2) {
			try {
				IVariable variableInDebugger = getMatchingVariableInDebugger(variable);
				if(variableInDebugger != null && variableInDebugger.supportsValueModification()) {
					setValueInDebugger(variableInDebugger, variable);
				}
			} catch (DebugException e) {
				ErrorDialog.openError(this.view.getSite().getShell(),
						"Variable not found", "Can't find variable "
								+ variable.getName()
								+ " in current stack-frame.", Activator
								.getErrorStatus(e));
			}
		}
	}

	private void setValueInDebugger(IVariable variableInDebugger,
			DebugVariable variable) {
		if(ValueFactory.isPrimitive(variable.getType()))
			try {
				variableInDebugger.setValue(variable.getValue().getValue().toString());
			} catch (DebugException e) {
				Activator.log(e);
			}
	}

	@SuppressWarnings("unchecked")
	private IVariable getMatchingVariableInDebugger(DebugVariable variable)
			throws DebugException {
		ISelection selection = this.view.getViewer().getSelection();
		if (selection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) selection;
			Iterator iterator = treeSelection.iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				if (next instanceof IDebugElement) {
					IDebugElement debugEle = (IDebugElement) next;
					IVariable var = (IVariable) debugEle
							.getAdapter(IVariable.class);
					if (var.getName().equals(variable.getName())
							&& var.getReferenceTypeName().equals(
									variable.getType()))
						return var;
				}
			}
		}
		return null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
