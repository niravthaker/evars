/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package name.nirav.evariablesview.ui;

import java.io.File;
import java.io.FileOutputStream;

import name.nirav.evariablesview.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.viewers.model.VirtualCopyToClipboardActionDelegate;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

@SuppressWarnings("restriction")
public class SaveVariablesAction extends VirtualCopyToClipboardActionDelegate {
	private Shell shell;

	@Override
	public void init(IViewPart view) {
		shell = view.getSite().getShell();
		super.init(view);
	}

	@Override
	protected void doCopy(Clipboard clipboard, TextTransfer plainTextTransfer,
			StringBuffer buffer) {
		FileDialog d = new FileDialog(shell, SWT.SAVE);
		d.setFilterExtensions(new String[] { "*.txt", "*.vars" });
		String open = d.open();
		if (open != null && open.trim().length() != 0) {
			File file = new File(open);
			if (file.exists()) {
				String message = "The file you selected already exists, overwrite?";
				boolean overwrite = MessageDialogWithToggle.openQuestion(shell,
						"Variable file already exists", message);
				if (overwrite)
					write(file, buffer.toString());
				return;
			}
			write(file, buffer.toString());
		}
	}

	private void write(File file, String string) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(string.getBytes());
			fos.close();
		} catch (Exception e) {
			IStatus status =Activator.getErrorStatus(e);
			ErrorDialog.openError(shell, "Write Failed",
					"Failed to write variable data to file" + file, status);
		}
	}
}
