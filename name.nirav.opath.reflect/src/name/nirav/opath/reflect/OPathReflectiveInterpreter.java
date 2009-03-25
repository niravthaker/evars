/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import name.nirav.opath.OPathInterpreter;
import name.nirav.opath.Variable;
import name.nirav.opath.reflect.serializable.ReflectValue;
import name.nirav.opath.reflect.serializable.ReflectVariable;

/**
 * @author Nirav Thaker
 * 
 */
public class OPathReflectiveInterpreter {
	public static Collection<Variable> interpret(final Object contextObject, String expr) {
		ReflectVariable context = new ReflectVariable(contextObject.getClass().getSimpleName()) {
			@Override
			public List<Variable> getChildren() {
				Field[] fields = contextObject.getClass().getDeclaredFields();
				List<Variable> list = new ArrayList<Variable>();
				for (Field field : fields) {
					list.add(ReflectVariable.create(field, contextObject));
				}
				return list;
			}
		};
		context.setValue(new ReflectValue(contextObject, context));
		return new OPathInterpreter().evaluate(expr, context).getResult();
	}
}
