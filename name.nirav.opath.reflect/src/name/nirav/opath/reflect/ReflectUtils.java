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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Nirav Thaker
 * 
 */
public class ReflectUtils {
	public static Collection<Field> getAllFields(Class<?> clazz) {
		if (clazz == null || clazz.equals(Object.class))
			return Collections.emptyList();
		Field[] declaredFields = clazz.getDeclaredFields();
		Field[] fields = clazz.getFields();
		List<Field> list = new ArrayList<Field>(declaredFields.length + fields.length);
		list.addAll(Arrays.asList(declaredFields));
		list.addAll(Arrays.asList(fields));
		list.addAll(getAllFields(clazz.getSuperclass()));
		return list;
	}

	public static boolean isConstant(Field field) {
		return Modifier.isFinal(field.getModifiers())
				&& Modifier.isStatic(field.getModifiers());
	}

	public static boolean isBoxedPrimitive(Class<? extends Object> clazz) {
		return Number.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
				|| Boolean.class.isAssignableFrom(clazz);
	}

	public static Object getFieldValue(Field field, Object o) {
		field.setAccessible(true);
		Object object = null;
		try {
			object = field.get(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
