/*******************************************************************************
 * Copyright (c) 2013, 2014, 2017 fortiss GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl, Monika Wenger
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.fbtypeeditor.actions;

import org.eclipse.fordiac.ide.model.commands.create.CreateInterfaceElementCommand;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.typelibrary.AdapterTypeEntry;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IWorkbenchPart;

public class CreateSocketAction extends WorkbenchPartAction {
	private static final String ID_PREFIX = "SOCKET_"; //$NON-NLS-1$
	private final FBType fbType;
	private final AdapterTypeEntry entry;

	public CreateSocketAction(final IWorkbenchPart part, final FBType fbType, final AdapterTypeEntry entry) {
		super(part);
		setId(getID(entry));
		setText(entry.getTypeName());
		this.fbType = fbType;
		this.entry = entry;
	}

	@Override
	protected boolean calculateEnabled() {
		return (null != fbType);
	}

	@Override
	public void run() {
		final CreateInterfaceElementCommand cmd = new CreateInterfaceElementCommand(entry.getType(),
				fbType.getInterfaceList(), true, -1);
		execute(cmd);
	}

	public static String getID(final AdapterTypeEntry entry) {
		return ID_PREFIX + entry.getFile().getFullPath().toString();
	}

}
