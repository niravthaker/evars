/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse.ast;



/**
 * @author nrulezz
 * 
 */
public class OPathAST {
	public static ASTStep createStartStep() {
		ASTStep step = new ASTStep();
		step.setType(StepType.Start);
		return step;
	}
	
	public static ASTStep createCurrentStep(ASTStep prevStep) {
		ASTStep step = new ASTCurrentStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}
	public static ASTStep createParentStep(ASTStep prevStep) {
		ASTStep step = new ASTParentStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}
	public static ASTStep createRootContext(ASTStep prevStep) {
		ASTStep step = new ASTRootContextStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}

	public static ASTStep createAllTestStep(ASTStep prevStep) {
		ASTStep step = new ASTAllTestStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		return step;
	}
	public static ASTStep createQNameStep(ASTStep prevStep, String qname) {
		ASTStep step = new ASTQNameStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		step.setQname(qname);
		return step;
	}
	public static ASTStep createAttributeStep(ASTStep prevStep, String qname) {
		ASTAttributeStep step = new ASTAttributeStep();
		prevStep.setNext(step);
		step.setPrevious(prevStep);
		step.setQname(qname);
		return step;
	}

//	public static ASTStep createStep(StepType type, ASTStep prevStep, String qname) {
//		ASTStep step = createStep(type, prevStep);
//		step.setQname(qname);
//		return step;
//	}
	public static ASTPredicateStep createPredicateStep(ASTStep step) {
		ASTPredicateStep predicateStep = new ASTPredicateStep();
		step.setNext(predicateStep);
		predicateStep.setPrevious(step);
		predicateStep.setType(StepType.Predicate);
		return predicateStep;
	}

}
