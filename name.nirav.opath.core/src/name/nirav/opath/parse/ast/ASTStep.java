/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;

public class ASTStep {
	private boolean isAbsolute;
	private ASTStep next;
	private StepType type;
	private String qname;
	private ASTStep previous;
	private boolean multilevel;

	public void setQname(String qname) {
		this.qname = qname;
	}

	public String getQname() {
		return qname;
	}

	public void setNext(ASTStep next) {
		this.next = next;
	}

	public ASTStep getNext() {
		return next;
	}

	public void setType(StepType type) {
		this.type = type;
	}

	public StepType getType() {
		return type;
	}

	public void setAbsolute(boolean isAbsolute) {
		this.isAbsolute = isAbsolute;
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	public void pp() {
		ASTStep step = this;
		print(step);
		while (step.getNext() != null) {
			step = step.getNext();
			print(step);
		}
		System.out.println(" EOF");
	}

	@Override
	public String toString() {
		return getType().name();
	}

	private void print(ASTStep step) {
		System.out.print("{" + step.toString() + "} => ");
	}

	public ASTStep getPreviousStep() {
		return previous;
	}

	public void setPrevious(ASTStep previous) {
		this.previous = previous;
	}

	public void setMultilevel(boolean b) {
		this.multilevel = b;
	}

	public boolean isMultilevel() {
		return multilevel;
	}
}
