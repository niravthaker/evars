/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nirav Thaker
 * 
 */
public class CycleDetector {
	private final static CycleDetector instance = new CycleDetector();
	private Set<Object> identitySet;
	private boolean cycleDetected;

	private CycleDetector() {
		identitySet = new HashSet<Object>();
	}

	public static CycleDetector getInstance() {
		return instance;
	}

	public boolean wasCycleDetected() {
		return cycleDetected;
	}

	public void clear() {
		identitySet.clear();
	}

	public boolean acyclicAdd(Object obj) {
		if (identitySet.contains(obj)) {
			this.cycleDetected = true;
			return false;
		}
		identitySet.add(obj);
		return true;
	}

	public void clearCycleFlag() {
		this.cycleDetected = false;
	}
}
