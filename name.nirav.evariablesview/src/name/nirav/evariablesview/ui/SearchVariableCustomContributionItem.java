/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package name.nirav.evariablesview.ui;

import java.util.Collection;

import name.nirav.evariablesview.Activator;
import name.nirav.evariablesview.ui.view.OPathFilteredView;
import name.nirav.opath.Variable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class SearchVariableCustomContributionItem extends ControlContribution {

	private static final String TEXT_SEARCH = "Search...";

	public void setSearchText(Text text, String string) {
		text.setText(string);
		if (!TEXT_SEARCH.equals(text.getText()))
			searchText = string;
	}

	private Color oofColor;
	private Color regularColor;
	private Composite composite;
	private Text searchTextBox;
	private String searchText;
	private OPathSearchFilter opathFilter;
	private OPathFilteredView opathView;

	public SearchVariableCustomContributionItem(String id, VariablesView view) {
		super(id);
		opathFilter = new OPathSearchFilter(this, view);
	}

	@Override
	protected Control createControl(Composite parent) {
		oofColor = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		regularColor = parent.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		composite.setLayout(gridLayout);

		searchTextBox = new Text(composite, SWT.SINGLE | SWT.SEARCH | SWT.CANCEL);
		searchTextBox.setForeground(oofColor);
		setSearchText(searchTextBox, TEXT_SEARCH);

		searchTextBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				refreshInternal(0);
			}
		});
		searchTextBox.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
			}
		});
		searchTextBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchTextBox.setForeground(regularColor);
				if (searchTextBox.getText().equals(TEXT_SEARCH))
					setSearchText(searchTextBox, "");
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchTextBox.setForeground(oofColor);
				if (searchTextBox.getText() != null && searchTextBox.getText().trim().isEmpty()) {
					setSearchText(searchTextBox, TEXT_SEARCH);
				}
			}
		});
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = SWT.DEFAULT;
		gridData.heightHint = SWT.DEFAULT;
		searchTextBox.setLayoutData(gridData);
		return composite;
	}

	@Override
	protected int computeWidth(Control control) {
		return 250;
	}

	protected void refreshInternal(long delay) {
		if (searchTextBox.isDisposed()) {
			searchText = "";
		} else
			searchText = searchTextBox.getText();
		opathFilter.setExpressionString(searchText);

		Job job = new Job("Filter variables..") {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				show();
				return Status.OK_STATUS;
			}
		};
		job.schedule(delay);
	}

	private void show() {
		try {
			final Collection<Variable> filtered = opathFilter.getFiltered();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						if (opathView == null || opathView.getViewer() == null
								|| opathView.getViewer().getControl().isDisposed()
								|| !opathView.getViewer().getControl().isVisible())
							opathView = (OPathFilteredView) PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage().showView(
											"name.nirav.evariablesview.views.OPathFilteredView");
						opathView.setInput(filtered);
						searchTextBox.setFocus();
						if (opathView.isAutoExpand())
							opathView.getViewer().expandAll();
					} catch (PartInitException e) {
						Activator.log(e);
						Activator.log(e);
					}
				}
			});
		} catch (RuntimeException e) {
			Activator.log(e);
			opathView.getViewer().setInput(null);
		}
	}

	@Override
	public void dispose() {
		this.composite.dispose();
		this.searchTextBox.dispose();
		this.oofColor = null;
		this.regularColor = null;
		this.searchText = "";
		this.composite = null;
		this.searchTextBox = null;
		this.opathFilter = null;
		super.dispose();
	}

	public String getSearchText() {
		return this.searchText;
	}

}
