/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import name.nirav.evariablesview.core.serializable.java.DebugVariable;
import name.nirav.evariablesview.core.util.ObjectGraphBuilder;
import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.Variable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * 
 */
@SuppressWarnings("restriction")
public class OPathSearchFilter {
	public OPathInterpreter interpreter;
	private String expressionString;
	private Collection<Variable> evaluate;
	private ObjectGraphBuilder objectGraphBuilder;
	private TreeViewer viewer;

	public OPathSearchFilter() {
		objectGraphBuilder = new ObjectGraphBuilder();
	}

	public OPathSearchFilter(
			SearchVariableCustomContributionItem searchVariableCustomContributionItem,
			VariablesView view) {
		this();
		viewer = ((TreeViewer) view.getViewer());
	}

	Collection<DebugVariable> context;

	private class InterpreterJob extends Job {
		private ITreeSelection treeSelection;
		private JDIStackFrame stackFrame;

		public InterpreterJob(String name, ITreeSelection treeSelection, JDIStackFrame stackFrame) {
			super(name);
			this.treeSelection = treeSelection;
			this.stackFrame = stackFrame;
		}

		public IStatus run(IProgressMonitor monitor) {
			context = objectGraphBuilder.buildFromSelection(treeSelection, stackFrame);
			interpreter = new OPathInterpreter();
			DebugVariable v = new DebugVariable("Context", null) {
				@Override
				public List<Variable> getChildren() {
					List<Variable> varList = new ArrayList<Variable>();
					for (DebugVariable var : context) {
						varList.add(var);
					}
					return varList;
				}

				@Override
				public String getName() {
					return super.getName();
				}
			};
			interpreter.evaluate(expressionString, v);
			evaluate = interpreter.getResult();
			return Status.OK_STATUS;
		}
	}

	private Job interpreterJob;

	public boolean filter(final ITreeSelection treeSelection, final JDIStackFrame stackFrame) {
		interpreterJob = new InterpreterJob("OPath Interpreter", treeSelection, stackFrame);
		return true;
	}

	public void setExpressionString(String inputText) {
		this.expressionString = inputText;
	}

	public Collection<Variable> getFiltered() {
		final Object[] obj = new Object[2];
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				synchronized (obj) {
					obj[0] = viewer.getSelection();
					obj[1] = viewer.getInput();
				}
			}
		});
		while (obj[0] == null || obj[1] == null)
			;

		filter((ITreeSelection) obj[0], (JDIStackFrame) obj[1]);
		try {
			interpreterJob.schedule();
			interpreterJob.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return evaluate;
	}

}
