/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.evariablesview.ui.view;

import java.util.List;
import java.util.WeakHashMap;

import name.nirav.evariablesview.Activator;
import name.nirav.evariablesview.core.serializable.java.DebugVariable;
import name.nirav.opath.Variable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaModifiers;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class ViewLabelProvider extends LabelProvider {
	private WeakHashMap<String, Image> cache;

	public ViewLabelProvider() {
		cache = new WeakHashMap<String, Image>();
	}

	public String getText(Object obj) {
		if (obj instanceof DebugVariable) {
			DebugVariable var = (DebugVariable) obj;
			try {
				if ("char[]".equals(var.getType())) {
					List<Variable> variables = var.getValue().getVariables();
					StringBuilder builder = new StringBuilder();
					for (Variable variable : variables) {
						builder.append(variable.getValue().getValue());
					}
					return var.getName() + " = " + builder.toString() + " (char[])"  ;
				} 
			} catch (Exception e) {
			}
			return var.getName() + " = " + var.getValue().toString() + " (" + var.getType() +")";
		}
		return obj.toString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof DebugVariable) {
			String icoName = "methdef_obj.gif";
			DebugVariable var = (DebugVariable) element;
			ImageDescriptor descriptor;
			try {
				IJavaModifiers modifiers = var.getModifiers();
				if (modifiers != null) {
					if (modifiers.isFinal()) {
						if (modifiers.isStatic())
							icoName = "constant_co.gif";
						else
							icoName = "final_co.gif";
					} else if (modifiers.isPrivate()) {
						icoName = "private_co.gif";
					} else if (modifiers.isStatic()) {
						icoName = "static_co.gif";
					} else if (modifiers.isProtected()) {
						icoName = "protected_co.gif";
					} else if (modifiers.isPublic()) {
						icoName = "public_co.gif";
					}
				}

				Image img = cache.get(icoName);
				if (img != null)
					return img;

				descriptor = Activator.getImageDescriptor("icons/" + icoName);
				Image image = descriptor.createImage();
				cache.put(icoName, image);
				return image;
			} catch (DebugException e) {
				Activator.log(e);
			}
		}
		return null;
	}
}