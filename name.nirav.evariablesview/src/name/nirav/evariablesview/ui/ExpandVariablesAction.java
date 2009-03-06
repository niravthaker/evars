/*******************************************************************************
 * Copyright (c) 2008 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package name.nirav.evariablesview.ui;

import java.util.Iterator;

import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * 
 * 
 */
@SuppressWarnings("restriction")
public class ExpandVariablesAction implements IViewActionDelegate {

	
	

	private VariablesView view;

	/**
	 * 
	 */
	public ExpandVariablesAction() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = (VariablesView) view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings( { "unchecked" })
	public void run(IAction action) {
		ISelection selection = view.getViewer().getSelection();
		if (selection instanceof TreeSelection) {
			TreeSelection treeSel = (TreeSelection) selection;
			Iterator iterator = treeSel.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				((TreeViewer) view.getViewer()).expandToLevel(object,
						TreeViewer.ALL_LEVELS);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
