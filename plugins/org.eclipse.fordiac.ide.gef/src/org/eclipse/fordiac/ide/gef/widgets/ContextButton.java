/*******************************************************************************
 * Copyright (c) 2023 Johannes Kepler University
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Sebastian Hollersbacher - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.fordiac.ide.gef.widgets;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Label;
import org.eclipse.fordiac.ide.ui.imageprovider.FordiacImage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class ContextButton extends Clickable implements ActionListener {

	private final String command = "org.eclipse.fordiac.ide.gef.zoom100";

	public ContextButton() {
		super(new Label(FordiacImage.ICON_DELETE_RESOURCE.getImage()), STYLE_BUTTON);
		addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
//		final ICommandImageService imageService = PlatformUI.getWorkbench().getService(ICommandImageService.class);

		try {
			handlerService.executeCommand(command, null);
		} catch (final Exception ex) {
			throw new RuntimeException("Command not found");
		}
	}
}
