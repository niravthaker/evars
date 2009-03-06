/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;

public abstract class ASTVisitor {
	
	public void visit(ASTPredicateStep step) {}
	public void visit(ASTAllTestStep step) {}
	public void visit(ASTStep step) {}
	public void visit(ASTAttributeStep step) {}
	public void visit(ASTRootContextStep step) {}
	public void visit(ASTQNameStep step) {}
	public void visit(ASTCurrentStep step) {}
	public void visit(ASTParentStep step) {}
}