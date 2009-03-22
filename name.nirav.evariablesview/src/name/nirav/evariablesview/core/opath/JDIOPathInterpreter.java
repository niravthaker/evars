/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.core.opath;

import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.parse.ast.expr.MethodInvocationExpression;

/**
 * @author Nirav Thaker
 * 
 */
public class JDIOPathInterpreter extends OPathInterpreter {
	@Override
	public void visit(MethodInvocationExpression expr) {
		super.visit(expr);
	}
}
