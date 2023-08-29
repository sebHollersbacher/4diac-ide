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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.handlers.IHandlerService;

public class ContextButton extends Clickable implements ActionListener {

	private final String command;
	final IHandlerService handlerService;
	final ICommandImageService imageService;

	public ContextButton(final String command) {
		super(new Label(FordiacImage.MISSING.getImage()), STYLE_BUTTON);

		imageService = PlatformUI.getWorkbench().getService(ICommandImageService.class);
		handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		this.command = command;
		final ImageDescriptor imgDescriptor = imageService.getImageDescriptor(command);
		if (imgDescriptor != null) {
			setContents(new Label(imgDescriptor.createImage()));
		}

		addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		try {
			handlerService.executeCommand(command, null);
		} catch (final Exception ex) {
			// invalid command
		}
	}
}
