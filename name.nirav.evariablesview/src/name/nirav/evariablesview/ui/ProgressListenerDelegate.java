/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.ui;

import org.eclipse.core.runtime.IProgressMonitor;

import name.nirav.opath.IProgressListener;

/**
 * @author Nirav Thaker
 *
 */
public class ProgressListenerDelegate implements IProgressListener {

	private final IProgressMonitor monitor;

	public ProgressListenerDelegate(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/* (non-Javadoc)
	 * @see name.nirav.opath.IProgressListener#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/* (non-Javadoc)
	 * @see name.nirav.opath.IProgressListener#done()
	 */
	public void done() {
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see name.nirav.opath.IProgressListener#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	/* (non-Javadoc)
	 * @see name.nirav.opath.IProgressListener#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/* (non-Javadoc)
	 * @see name.nirav.opath.IProgressListener#worked(int)
	 */
	public void worked(int work) {
		monitor.worked(work);
	}

}
