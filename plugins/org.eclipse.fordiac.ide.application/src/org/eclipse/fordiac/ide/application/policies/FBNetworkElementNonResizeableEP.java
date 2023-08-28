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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedNonResizeableEditPolicy;
import org.eclipse.fordiac.ide.gef.widgets.ContextButton;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer.Pos;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;

public class FBNetworkElementNonResizeableEP extends ModifiedNonResizeableEditPolicy {

	private IFigure hoverFigure;
	private ContextButtonContainer topContainer;
	private ContextButtonContainer rightContainer;
	private ContextButtonContainer bottomContainer;
	private ContextButtonContainer leftContainer;
	private ContextButtonContainer.Pos hoverPos;

	private final int maxButtonSize = 21;

	@Override
	protected IFigure createSelectionFeedbackFigure() {

//		TODO Provider
//		IContextButtonProvider provider = getHost().getAdapter(IContextButtonProvider.class);
//		List<String> commands = provider.getCommandIds();
//		IContextButtonProvider.POS pos = provider.getPos(commandid);
//		int maxButtonSize = provider.getMaxButtonSize();

		topContainer = createContainer(Pos.Top);
		rightContainer = createContainer(Pos.Right);
		bottomContainer = createContainer(Pos.Bottom);
		leftContainer = createContainer(Pos.Left);

		final RoundedRectangle figure = (RoundedRectangle) super.createSelectionFeedbackFigure();
		figure.setFill(false);
		figure.setOutline(true);
		figure.setLineWidth(2 * ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		return figure;
	}

	private ContextButtonContainer createContainer(final Pos position) {
		final ContextButtonContainer container = new ContextButtonContainer(getHostFigure().getBounds(), position,
				maxButtonSize);
		container.setFill(false);
		container.setOutline(true);

		for (int i = 0; i < 3; i++) {
			final ContextButton button = new ContextButton();
			container.addButton(button);
			container.add(button);
		}
		container.addMouseMotionListener(new MouseMotionListener.Stub() {
			@Override
			public void mouseExited(final MouseEvent me) {
				if (!container.getBounds().contains(me.getLocation())) {
					getLayer(LayerConstants.HANDLE_LAYER).remove(container);
					switch (position) {
					case Top -> topContainer = null;
					case Right -> rightContainer = null;
					case Bottom -> bottomContainer = null;
					case Left -> leftContainer = null;
					default -> throw new IllegalArgumentException();
					}
					hoverPos = null;
				}
			}

			@Override
			public void mouseEntered(final MouseEvent me) {
				hoverPos = position;
			}
		});

		return container;
	}

	@Override
	public void showTargetFeedback(final Request request) {
		if (!isFeedbackRequest(request) || (null != hoverFigure) || (null != handles)) {
			return;
		}

		hoverFigure = createSelectionFeedbackFigure();
		if (null != hoverFigure) {
			getLayer(LayerConstants.FEEDBACK_LAYER).add(hoverFigure);
		}

		if (null != topContainer) {
			getLayer(LayerConstants.HANDLE_LAYER).add(topContainer);
		}
		if (null != rightContainer) {
			getLayer(LayerConstants.HANDLE_LAYER).add(rightContainer);
		}
		if (null != bottomContainer) {
			getLayer(LayerConstants.HANDLE_LAYER).add(bottomContainer);
		}
		if (null != leftContainer) {
			getLayer(LayerConstants.HANDLE_LAYER).add(leftContainer);
		}
	}

	@Override
	public void eraseTargetFeedback(final Request request) {
		if (hoverFigure != null) {
			getLayer(LayerConstants.FEEDBACK_LAYER).remove(hoverFigure);
			hoverFigure = null;
		}

		if (topContainer != null && hoverPos != Pos.Top) {
			getLayer(LayerConstants.HANDLE_LAYER).remove(topContainer);
			topContainer = null;
		}
		if (rightContainer != null && hoverPos != Pos.Right) {
			getLayer(LayerConstants.HANDLE_LAYER).remove(rightContainer);
			rightContainer = null;
		}
		if (bottomContainer != null && hoverPos != Pos.Bottom) {
			getLayer(LayerConstants.HANDLE_LAYER).remove(bottomContainer);
			bottomContainer = null;
		}
		if (leftContainer != null && hoverPos != Pos.Left) {
			getLayer(LayerConstants.HANDLE_LAYER).remove(leftContainer);
			leftContainer = null;
		}
	}
}