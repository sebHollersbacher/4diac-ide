/*******************************************************************************
 * Copyright (c) 2022 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl - initial API and implementation and/or initial documentation
 *   Sebastian Hollersbacher - changes to create ContextButtons
 *******************************************************************************/
package org.eclipse.fordiac.ide.application.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedNonResizeableEditPolicy;
import org.eclipse.fordiac.ide.gef.widgets.ContextButton;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer.Pos;
import org.eclipse.fordiac.ide.gef.widgets.IContextButtonProvider;
import org.eclipse.gef.GraphicalEditPart;

public class FBNetworkElementNonResizeableEP extends ModifiedNonResizeableEditPolicy {

	private ContextButtonContainer topContainer;
	private ContextButtonContainer rightContainer;
	private ContextButtonContainer bottomContainer;
	private ContextButtonContainer leftContainer;

	private IContextButtonProvider provider;

	private static final int MAX_BUTTON_SIZE = 21;

	@Override
	protected List<? extends IFigure> createSelectionHandles() {
		final List<IFigure> list = new ArrayList<>();
		list.add(new ModifiedMoveHandle((GraphicalEditPart) getHost(), insets, arc));

		if (topContainer != null) {
			list.add(topContainer);
		}
		if (rightContainer != null) {
			list.add(rightContainer);
		}
		if (bottomContainer != null) {
			list.add(bottomContainer);
		}
		if (leftContainer != null) {
			list.add(leftContainer);
		}

		removeSelectionFeedbackFigure();
		return list;
	}

	@Override
	protected RoundedRectangle createSelectionFeedbackFigure() {
		final IContextButtonProvider provider = getHost().getAdapter(IContextButtonProvider.class);
		if (provider != null) {
			createContextButtonMenu(provider);
		}

		final RoundedRectangle figure = super.createSelectionFeedbackFigure();
		figure.setFill(false);
		figure.setOutline(true);
		figure.setLineWidth(2 * ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		return figure;
	}

	private void createContextButtonMenu(final IContextButtonProvider provider) {
		// top
		List<String> commands = provider.topCommandIDs();
		if (!commands.isEmpty()) {
			topContainer = createContainer(Pos.Top);
			for (final String cmd : commands) {
				topContainer.addButton(new ContextButton(cmd));
			}
		}

		// right
		commands = provider.rightCommandIDs();
		if (!commands.isEmpty()) {
			rightContainer = createContainer(Pos.Right);
			for (final String cmd : commands) {
				rightContainer.addButton(new ContextButton(cmd));
			}
		}

		// bottom
		commands = provider.bottomCommandIDs();
		if (!commands.isEmpty()) {
			bottomContainer = createContainer(Pos.Bottom);
			for (final String cmd : commands) {
				bottomContainer.addButton(new ContextButton(cmd));
			}
		}

		// left
		commands = provider.leftCommandIDs();
		if (!commands.isEmpty()) {
			leftContainer = createContainer(Pos.Left);
			for (final String cmd : commands) {
				leftContainer.addButton(new ContextButton(cmd));
			}
		}
	}

	private ContextButtonContainer createContainer(final Pos position) {
		final ContextButtonContainer container = new ContextButtonContainer(getHostFigure().getBounds(), position,
				MAX_BUTTON_SIZE);
		container.setFill(false);
		container.setOutline(true);

		return container;
	}
}