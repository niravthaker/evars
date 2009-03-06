/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package name.nirav.evariablesview.ui;


import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class AttachSearchFeature implements IStartup {

	private static final String ID_SEARCH_VARIABLES = "search.variables";

	private void attach(IWorkbenchPart part) {
		if (part instanceof IViewPart) {
			IViewPart view = (IViewPart) part;
			if (!view.getViewSite().getId().equals(
					IDebugUIConstants.ID_VARIABLE_VIEW))
				return;
			IToolBarManager manager = view.getViewSite().getActionBars()
					.getToolBarManager();
			if (manager.find(ID_SEARCH_VARIABLES) != null)
				return;
			manager.appendToGroup("variableGroup",
					new SearchVariableCustomContributionItem(
							ID_SEARCH_VARIABLES, (VariablesView) view));
			manager.update(true);
			view.getViewSite().getActionBars().updateActionBars();
		}
	}

	public class CustomPartListener implements IPartListener2 {

		public void partActivated(IWorkbenchPartReference partRef) {
			attach(partRef.getPart(false));
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			attach(partRef.getPart(false));
		}

		public void partVisible(IWorkbenchPartReference partRef) {
		}

	}

	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				window.getActivePage()
						.addPartListener(new CustomPartListener());
				IViewReference[] references = window.getActivePage()
						.getViewReferences();
				for (IViewReference viewReference : references) {
					IViewPart view = viewReference.getView(false);
					attach(view);
				}
			}
		});
	}
}
