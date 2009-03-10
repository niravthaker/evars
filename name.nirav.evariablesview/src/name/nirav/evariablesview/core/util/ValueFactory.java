/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.core.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import name.nirav.evariablesview.Activator;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.internal.debug.core.model.JDIArrayValue;
import org.eclipse.jdt.internal.debug.core.model.JDIClassObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIPrimitiveValue;

@SuppressWarnings({ "serial", "restriction" })
public class ValueFactory {
	private static Map<String, Class<?>> primitiveTypeMap = new HashMap<String, Class<?>>() {
		{
			put("int", int.class);
			put("char", char.class);
			put("short", short.class);
			put("double", double.class);
			put("byte", byte.class);
			put("boolean", boolean.class);
			put("long", long.class);
			put("float", float.class);
		}
	};
	public class ObjectValueTypeResolver implements ITypeResolver {

		public Object resolve(Object obj) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class PrimitiveValueTypeResolver implements ITypeResolver {
		enum JDIPrimitiveTypes {
			Int("int"), Long("long"), Boolean("boolean"), Double("double"), Float(
					"float"), Short("short"), Byte("byte"), Char("char");
			String str;

			JDIPrimitiveTypes(String str) {
				this.str = str;
			}

			public static JDIPrimitiveTypes fromDescription(String desc) {
				JDIPrimitiveTypes[] values = values();
				for (JDIPrimitiveTypes primitiveType : values) {
					if (primitiveType.str.equals(desc))
						return primitiveType;
				}
				return null;
			}
		};

		public Object resolve(Object obj) {
			JDIPrimitiveValue value = (JDIPrimitiveValue) obj;
			try {
				String name = value.getJavaType().getName();
				JDIPrimitiveTypes types = JDIPrimitiveTypes
						.fromDescription(name);
				switch (types) {
				case Int:
					return value.getIntValue();
				case Char:
					return value.getCharValue();
				case Short:
					return value.getShortValue();
				case Long:
					return value.getLongValue();
				case Boolean:
					return value.getBooleanValue();
				case Byte:
					return value.getByteValue();
				case Double:
					return value.getDoubleValue();
				case Float:
					return value.getFloatValue();
				}
			} catch (DebugException e) {
				Activator.log(e);
			}
			return null;
		}

	}

	public class ClassObjectValueTypeResolver implements ITypeResolver {

		public Object resolve(Object obj) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public class NullValueTypeResolver implements ITypeResolver {

		public Object resolve(Object obj) {
			return null;
		}

	}

	public class ArrayValueTypeResolver implements ITypeResolver {

		public Object resolve(Object obj) {
			JDIArrayValue value = (JDIArrayValue) obj;
			try {
				String className = value.getJavaType().getName().replace('[',
						' ').replace(']', ' ').trim();
				Class<?> clazz;
				if (isPrimitiveClass(className)) {
					clazz = getPrimitiveClass(className);
				} else
					clazz = Class.forName(className);
				return Array.newInstance(clazz, value.getArrayLength());
			} catch (DebugException e) {
				Activator.log(e);
			} catch (Exception e) {
				Activator.log(e);
			}
			return null;
		}

		

		private Class<?> getPrimitiveClass(String className) {
			return primitiveTypeMap.get(className);
		}

		private boolean isPrimitiveClass(String className) {
			return primitiveTypeMap.keySet().contains(className);
		}

	}

	public interface ITypeResolver {
		public Object resolve(Object obj);
	}

	private Map<Class<?>, ITypeResolver> valueResolvers = new HashMap<Class<?>, ITypeResolver>() {
		{
			put(JDIObjectValue.class, new ObjectValueTypeResolver());
			put(JDIArrayValue.class, new ArrayValueTypeResolver());
			put(JDINullValue.class, new NullValueTypeResolver());
			put(JDIClassObjectValue.class, new ClassObjectValueTypeResolver());
			put(JDIPrimitiveValue.class, new PrimitiveValueTypeResolver());
		}
	};
	private static ValueFactory factory = null;

	private static ValueFactory getFactory() {
		if (factory == null) {
			factory = new ValueFactory();
		}
		return factory;
	}

	public static Object resolve(IValue value) {
		return getFactory().valueResolvers.get(value.getClass()).resolve(value);
	}

	public static boolean isPrimitive(Object type) {
		return primitiveTypeMap.containsKey(type);
	}

}
