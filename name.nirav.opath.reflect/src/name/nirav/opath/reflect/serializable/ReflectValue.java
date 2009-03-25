/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.reflect.serializable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import name.nirav.opath.Value;
import name.nirav.opath.Variable;
import name.nirav.opath.reflect.ReflectUtils;

/**
 * @author Nirav Thaker
 * 
 */
public class ReflectValue extends Value {

	private final Object value;
	private final ReflectVariable holder;

	public ReflectValue(Object obj, ReflectVariable holder) {
		this.value = obj;
		this.holder = holder;

	}

	@Override
	public Object getComparableValue() {
		if (value.getClass().equals(char[].class))
			return new String((char[]) value);
		return value;
	}

	@Override
	public Object getValue() {
		return getComparableValue();
	}

	@Override
	public List<Variable> getVariables() {
		if (value == null || ReflectUtils.isBoxedPrimitive(value.getClass())
				|| value.getClass().getName().indexOf("this$") != -1)
			return Collections.emptyList();

		Class<?> type = value.getClass().getComponentType();
		Collection<Field> fields;
		if (type == null)
			fields = ReflectUtils.getAllFields(value.getClass());
		else
			fields = ReflectUtils.getAllFields(type);

		List<Variable> lst = new ArrayList<Variable>();
		List<String> excludeList = Arrays.asList("entrySet", "keySet");
		for (Field field : fields) {
			try {
				if (ReflectUtils.isConstant(field) || excludeList.contains(field.getName())) {
					continue;
				}
				if (type != null) {
					int length = ((Object[]) value).length;
					for (int i = 0; i < length; i++) {
						Object object = Array.get(value, i);
						if (object != null) {
							ReflectVariable var = new ReflectVariable(field, object);
							var.setParent(holder);
							lst.add(var);
						}
					}
				} else {
					ReflectVariable var = new ReflectVariable(field, value);
					var.setParent(holder);
					lst.add(var);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lst;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((holder == null) ? 0 : holder.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ReflectValue other = (ReflectValue) obj;
		if (holder == null) {
			if (other.holder != null)
				return false;
		} else if (!holder.equals(other.holder))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return value == null ? super.toString() : value.getClass() + " : " + value.toString();
	}
}
