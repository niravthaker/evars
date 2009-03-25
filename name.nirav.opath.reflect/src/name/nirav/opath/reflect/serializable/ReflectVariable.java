/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.reflect.serializable;

import java.lang.reflect.Field;

import name.nirav.opath.Variable;

/**
 * @author Nirav Thaker
 * 
 */
public class ReflectVariable extends Variable {

	private Object obj;
	private Field field;

	public ReflectVariable(Field field, Object o) {
		super(field.getName());
		this.field = field;
		obj = o;
		field.setAccessible(true);
		try {
			Object object = field.get(o);
			setValue(new ReflectValue(object, this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReflectVariable(String string) {
		super(string);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReflectVariable other = (ReflectVariable) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		return true;
	}

	public static ReflectVariable create(Field field, Object a) {
		return new ReflectVariable(field, a);
	}
}
