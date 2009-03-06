/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;


public abstract class ModelVisitor {

	public void visit(Variable variable) {

	}

	public void visit(Value value) {

	}

	public void exit(Value value) {
		
	}

	public void enter(Value value) {
		
	}

}