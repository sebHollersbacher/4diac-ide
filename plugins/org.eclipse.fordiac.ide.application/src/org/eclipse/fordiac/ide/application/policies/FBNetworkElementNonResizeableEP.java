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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedNonResizeableEditPolicy;
import org.eclipse.fordiac.ide.gef.widgets.ContextButton;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer.Pos;
import org.eclipse.fordiac.ide.gef.widgets.IContextButtonProvider;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;

public class FBNetworkElementNonResizeableEP extends ModifiedNonResizeableEditPolicy {

	private ContextButtonContainer topContainer;
	private ContextButtonContainer rightContainer;
	private ContextButtonContainer bottomContainer;
	private ContextButtonContainer leftContainer;

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
		createContainer(provider.topCommandIDs(), Pos.Top);
		createContainer(provider.rightCommandIDs(), Pos.Right);
		createContainer(provider.bottomCommandIDs(), Pos.Bottom);
		createContainer(provider.leftCommandIDs(), Pos.Left);
	}

	private void createContainer(final List<String> commands, final Pos position) {
		if (!commands.isEmpty()) {
			final ContextButtonContainer container = new ContextButtonContainer(getHostFigure().getBounds(), position,
					MAX_BUTTON_SIZE);
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
	protected IFigure createDragSourceFeedbackFigure() {
		topContainer.setVisible(false);
		rightContainer.setVisible(false);
		bottomContainer.setVisible(false);
		leftContainer.setVisible(false);

		return super.createDragSourceFeedbackFigure();
	}

	@Override
	protected void eraseChangeBoundsFeedback(final ChangeBoundsRequest request) {
		final Rectangle dragFigureBounds = getDragSourceFeedbackFigure().getBounds();

		topContainer.setVisible(true);
		topContainer.updateBounds(dragFigureBounds);
		rightContainer.setVisible(true);
		rightContainer.updateBounds(dragFigureBounds);
		bottomContainer.setVisible(true);
		bottomContainer.updateBounds(dragFigureBounds);
		leftContainer.setVisible(true);
		leftContainer.updateBounds(dragFigureBounds);

		super.eraseChangeBoundsFeedback(request);
	}
}