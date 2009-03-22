/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

/**
 * @author Nirav Thaker
 * 
 */
public class DefaultProgressListener implements IProgressListener {
	public void beginTask(String name, int totalWork) {
		System.out.println("Starting " + name + " " + totalWork);
	}

	public void done() {
		System.out.println(".");
	}

	public boolean isCanceled() {
		return false;
	}

	public void worked(int work) {
		System.out.print("-");
	}

	public void setTaskName(String name) {
		System.out.print(name);
	}

}
