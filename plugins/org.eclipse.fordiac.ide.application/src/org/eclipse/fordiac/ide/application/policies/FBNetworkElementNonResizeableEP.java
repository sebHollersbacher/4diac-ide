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
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedNonResizeableEditPolicy;
import org.eclipse.fordiac.ide.gef.widgets.ContextButton;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer;
import org.eclipse.fordiac.ide.gef.widgets.ContextButtonContainer.Pos;
import org.eclipse.fordiac.ide.gef.widgets.IContextButtonProvider;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;

public class FBNetworkElementNonResizeableEP extends ModifiedNonResizeableEditPolicy {

	private ContextButtonContainer topContainer;
	private ContextButtonContainer rightContainer;
	private ContextButtonContainer bottomContainer;
	private ContextButtonContainer leftContainer;

	private Rectangle realEditPartBounds = null;
	private boolean hasEnteredContainer = false;

	@Override
	protected List<? extends IFigure> createSelectionHandles() {
		final List<IFigure> list = new ArrayList<>();
		list.add(new ModifiedMoveHandle((GraphicalEditPart) getHost(), insets, arc));
		removeSelectionFeedbackFigure();

		createContextButtonMenu(getHost().getAdapter(IContextButtonProvider.class), false);
		performContainerAction((container, l) -> l.add(container), list);
		performContainerAction((container, figure) -> figure.addFigureListener(container), getHostFigure());

		return list;
	}

	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		performContainerAction((container, figure) -> figure.removeFigureListener(container), getHostFigure());
		performContainerAction((container, layer) -> {
			if (layer.getChildren().contains(container)) {
				layer.remove(container);
			}
		}, getLayer(LayerConstants.HANDLE_LAYER));
		topContainer = null;
		rightContainer = null;
		bottomContainer = null;
		leftContainer = null;
	}

	@Override
	protected RoundedRectangle createSelectionFeedbackFigure() {
		final RoundedRectangle figure = super.createSelectionFeedbackFigure();
		figure.setFill(false);
		figure.setOutline(true);
		figure.setLineWidth(2 * ModifiedMoveHandle.SELECTION_BORDER_WIDTH);

		realEditPartBounds = getHostFigure().getBounds().getCopy();
		getHostFigure().getBounds().expand(10, 10); // we have to expand the underlying figure for better behaviour when
													// moving the mouse over the context buttons
		createContextButtonMenu(getHost().getAdapter(IContextButtonProvider.class), true);
		setVisible(true);

		return figure;
	}

	@Override
	protected void removeSelectionFeedbackFigure() {
		if (selectionFeedback != null) {
			removeFeedback(selectionFeedback);
			selectionFeedback = null;
			getHostFigure().getBounds().shrink(10, 10);
			setVisible(false);
		}
	}

	private void createContextButtonMenu(final IContextButtonProvider provider, final boolean isHover) {
		if (topContainer == null) {
			createContainer(provider.topCommandIDs(), Pos.TOP, isHover);
		}
		if (rightContainer == null) {
			createContainer(provider.rightCommandIDs(), Pos.RIGHT, isHover);
		}
		if (bottomContainer == null) {
			createContainer(provider.bottomCommandIDs(), Pos.BOTTOM, isHover);
		}
		if (leftContainer == null) {
			createContainer(provider.leftCommandIDs(), Pos.LEFT, isHover);
		}
	}

	private void createContainer(final List<String> commands, final Pos position, final boolean isHover) {
		if (!commands.isEmpty()) {
			final ContextButtonContainer container = new ContextButtonContainer(realEditPartBounds, position);
			for (final String cmd : commands) {
				final ContextButton button = new ContextButton(cmd, getHost());
				if (isHover) {
					button.addMouseMotionListener(mouseEnterExitListener);
				}
				container.addButton(button);
			}

			container.setFill(false);
			container.setOutline(true);
			if (isHover) {
				container.addMouseMotionListener(mouseEnterExitListener);
			}

			switch (position) {
			case TOP -> topContainer = container;
			case RIGHT -> rightContainer = container;
			case BOTTOM -> bottomContainer = container;
			case LEFT -> leftContainer = container;
			default -> throw new IllegalArgumentException();
			}
		}
	}

	public void setVisible(final boolean value) {
		if (value) {
			performContainerAction((container, layer) -> {
				if (!layer.getChildren().contains(container)) {
					layer.add(container);
				}
			}, getLayer(LayerConstants.HANDLE_LAYER));
		} else if (!hasEnteredContainer) { // when entering the container showing called before hiding, we have to make
											// sure the hiding does not happen
			performContainerAction((container, layer) -> {
				if (layer.getChildren().contains(container)) {
					layer.remove(container);
				}
			}, getLayer(LayerConstants.HANDLE_LAYER));
		}
	}

	private final MouseMotionListener mouseEnterExitListener = new MouseMotionListener.Stub() {
		@Override
		public void mouseExited(final MouseEvent me) {
			hasEnteredContainer = false;
			setVisible(false);
		}

		@Override
		public void mouseEntered(final MouseEvent me) {
			setVisible(true);
			hasEnteredContainer = true;
		}
	};

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