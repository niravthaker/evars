/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath;

public class InterpreterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InterpreterException(String string) {
		super(string);
	}
	public InterpreterException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

}