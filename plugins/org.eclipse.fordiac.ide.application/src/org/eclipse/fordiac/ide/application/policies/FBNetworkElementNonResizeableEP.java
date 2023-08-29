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

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedNonResizeableEditPolicy;
import org.eclipse.fordiac.ide.gef.widgets.ContextButton;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer.Pos;
import org.eclipse.fordiac.ide.gef.widgets.IContextButtonProvider;
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
		final IContextButtonProvider provider = getHost().getAdapter(IContextButtonProvider.class);
		if (provider != null) {
			createContextButtonMenu(provider);
		}

		final RoundedRectangle figure = (RoundedRectangle) super.createSelectionFeedbackFigure();
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
				maxButtonSize);
		container.setFill(false);
		container.setOutline(true);

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
		if (hoverFigure != null) {
			getLayer(LayerConstants.FEEDBACK_LAYER).add(hoverFigure);
		}

		if (topContainer != null) {
			getLayer(LayerConstants.HANDLE_LAYER).add(topContainer);
		}
		if (rightContainer != null) {
			getLayer(LayerConstants.HANDLE_LAYER).add(rightContainer);
		}
		if (bottomContainer != null) {
			getLayer(LayerConstants.HANDLE_LAYER).add(bottomContainer);
		}
		if (leftContainer != null) {
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