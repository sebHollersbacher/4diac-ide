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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.ui.imageprovider.FordiacImage;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.handlers.IHandlerService;

public class ContextButton extends Clickable implements ActionListener {

	private final String command;
	final IHandlerService handlerService;
	final ICommandImageService imageService;
	private boolean hover = false;
	private ImageDescriptor imgDescriptor;
	private final EditPart editPart;

	public ContextButton(final String command, final EditPart editpart) {
		imageService = PlatformUI.getWorkbench().getService(ICommandImageService.class);
		handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);

		this.command = command;
		this.imgDescriptor = imageService.getImageDescriptor(command);
		if (this.imgDescriptor == null) {
			this.imgDescriptor = FordiacImage.MISSING.getImageDescriptor();
		}
		setToolTip(new Label(command));
		this.editPart = editpart;

		addActionListener(this);
		addMouseMotionListener(new MouseMotionListener.Stub() {
			@Override
			public void mouseEntered(final MouseEvent me) {
				hover = true;
				repaint();
			}

			@Override
			public void mouseExited(final MouseEvent me) {
				hover = false;
				repaint();
			}
		});
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		try {
//			editPart.getViewer().select(editPart);
			handlerService.executeCommand(command, null);
//			editPart.getViewer().deselect(editPart);
		} catch (final Exception ex) {
			// invalid command
		}
	}

	@Override
	protected void paintFigure(final Graphics graphics) {
		if (hover) {
			graphics.setAlpha(2 * ModifiedMoveHandle.SELECTION_FILL_ALPHA);
		} else {
			graphics.setAlpha(ModifiedMoveHandle.SELECTION_FILL_ALPHA);
		}
		graphics.setBackgroundColor(ModifiedMoveHandle.getSelectionColor());
		graphics.fillRoundRectangle(getBounds(), 4, 4);
		if (imgDescriptor != null) {
			final Image image = imgDescriptor.createImage();
			graphics.setAlpha(255);
			final org.eclipse.swt.graphics.Rectangle rect = image.getBounds();
			final Rectangle newRect = new Rectangle(0, 0, rect.width, rect.height);
			newRect.x = getBounds().x + ((getBounds().width - newRect.width) / 2);
			newRect.y = getBounds().y + ((getBounds().height - newRect.height) / 2);
			graphics.drawImage(image, rect.x, rect.y, rect.width, rect.height, newRect.x, newRect.y, newRect.width,
					newRect.height);
			image.dispose();
		}
	}
}
