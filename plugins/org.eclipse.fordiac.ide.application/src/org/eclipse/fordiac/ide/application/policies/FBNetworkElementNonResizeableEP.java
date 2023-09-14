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
import java.util.function.BiConsumer;

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

	@Override
	protected List<? extends IFigure> createSelectionHandles() {
		final List<IFigure> list = new ArrayList<>();
		list.add(new ModifiedMoveHandle((GraphicalEditPart) getHost(), insets, arc));

		final IContextButtonProvider provider = getHost().getAdapter(IContextButtonProvider.class);
		if (provider != null) {
			createContextButtonMenu(provider);
		}

		performContainerAction((container, l) -> l.add(container), list);
		performContainerAction((container, figure) -> figure.addFigureListener(container), getHostFigure());

		removeSelectionFeedbackFigure();
		return list;
	}

	@Override
	protected RoundedRectangle createSelectionFeedbackFigure() {
		final RoundedRectangle figure = super.createSelectionFeedbackFigure();
		figure.setFill(false);
		figure.setOutline(true);
		figure.setLineWidth(2 * ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		return figure;
	}

	private void createContextButtonMenu(final IContextButtonProvider provider) {
		createContainer(provider.topCommandIDs(), Pos.Top);
		createContainer(provider.rightCommandIDs(), Pos.Right);
		createContainer(provider.bottomCommandIDs(), Pos.Bottom);
		createContainer(provider.leftCommandIDs(), Pos.Left);
	}

	private void createContainer(final List<String> commands, final Pos position) {
		if (!commands.isEmpty()) {
			final ContextButtonContainer container = new ContextButtonContainer(getHostFigure().getBounds(), position);
			for (final String cmd : commands) {
				container.addButton(new ContextButton(cmd));
			}

			container.updateBounds(getHostFigure().getBounds());
			container.setFill(false);
			container.setOutline(true);

			switch (position) {
			case Top -> topContainer = container;
			case Right -> rightContainer = container;
			case Bottom -> bottomContainer = container;
			case Left -> leftContainer = container;
			default -> throw new IllegalArgumentException();
			}
		}
	}

	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		performContainerAction((container, figure) -> figure.removeFigureListener(container), getHostFigure());
		topContainer = null;
		rightContainer = null;
		bottomContainer = null;
		leftContainer = null;
	}

	private <T> void performContainerAction(final BiConsumer<ContextButtonContainer, T> function, final T value) {
		if (topContainer != null) {
			function.accept(topContainer, value);
		}
		if (rightContainer != null) {
			function.accept(rightContainer, value);
		}
		if (bottomContainer != null) {
			function.accept(bottomContainer, value);
		}
		if (leftContainer != null) {
			function.accept(leftContainer, value);
		}
	}
}