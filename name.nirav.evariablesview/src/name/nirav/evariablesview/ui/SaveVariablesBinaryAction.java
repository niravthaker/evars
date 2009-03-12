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
import java.util.List;

import name.nirav.evariablesview.Activator;
import name.nirav.evariablesview.core.util.ObjectGraphBuilder;
import name.nirav.evariablesview.core.util.SerializerUtils;
import name.nirav.opath.Variable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

@SuppressWarnings("restriction")
public class SaveVariablesBinaryAction implements IViewActionDelegate {

	VariablesView vview;
	private List<? extends Variable> varList = null;

	public void init(IViewPart view) {
		vview = (VariablesView) view;
	}

	public void run(IAction action) {
		TreeViewer viewer = (TreeViewer) vview.getViewer();
		this.varList = new ObjectGraphBuilder().buildFromSelection((ITreeSelection) viewer
				.getSelection(), (JDIStackFrame) viewer.getInput());
		FileDialog dialog = new FileDialog(vview.getSite().getShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.bvars" });
		String open = dialog.open();
		if (open != null && open.trim().length() != 0) {
			File file = new File(open);
			if (file.exists()) {
				String message = "The file you selected already exists, overwrite?";
				boolean overwrite = MessageDialogWithToggle.openQuestion(
						vview.getSite().getShell(), "Variable file already exists", message);
				if (overwrite)
					write(file, this.varList);
				return;
			}
			write(file, this.varList);
		}

	}

	private void write(File file, List<? extends Variable> varList2) {
		try {
			SerializerUtils.serialize(file, varList2);
		} catch (IOException e) {
			Activator.log(e);
			IStatus status = Activator.getErrorStatus(e);
			ErrorDialog.openError(vview.getSite().getShell(), "Variables save failed",
					"Failed to save variables to file", status);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
