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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;

public class ContextButtonContainer extends Shape implements FigureListener {

	public enum Pos {
		TOP, BOTTOM, LEFT, RIGHT
	}

	private static final int BUTTON_SIZE = 21;
	private static final int CONTAINER_BUTTON_MARGIN = 5;
	private final Pos position;
	private Rectangle curEditPartBounds;

	private final Rectangle drawRectangle = new Rectangle();
	private final List<ContextButton> buttons = new ArrayList<>();

	public ContextButtonContainer(final Rectangle editPartBounds, final Pos position) {
		this.position = position;
		this.curEditPartBounds = editPartBounds.getCopy();
		updateContainer(editPartBounds);
	}

	private void updateContainer(final Rectangle editPartBounds) {
		switch (position) {
		case TOP -> this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.width() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN,
				BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN));

		case RIGHT -> this.setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width(),
				editPartBounds.y() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
				BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.height() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN));

		case BOTTOM -> this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height(),
				editPartBounds.width() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN,
				BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN));

		case LEFT -> this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
				BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.height() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN));

		default -> throw new IllegalArgumentException();
		}
	}

	private void updateButtons(final int widthChange, final int heightChange) {
		if (position == Pos.TOP) {
			for (final ContextButton contextButton : buttons) {
				contextButton.getBounds().setX(contextButton.getBounds().x() - widthChange);
			}
		}
		if (position == Pos.LEFT) {
			for (final ContextButton contextButton : buttons) {
				contextButton.getBounds().setY(contextButton.getBounds().y() - heightChange);
			}
		}
	}

	public void updateDrawRectangle(final Rectangle editPartBounds) {
		final int buttonSize = BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN;
		switch (position) {
		case TOP -> drawRectangle.setBounds(new Rectangle(
				editPartBounds.x() + 2 * CONTAINER_BUTTON_MARGIN + editPartBounds.width() - buttons.size() * buttonSize,
				editPartBounds.y() - buttonSize - CONTAINER_BUTTON_MARGIN, buttons.size() * buttonSize, buttonSize));
		case RIGHT ->
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width() + CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - CONTAINER_BUTTON_MARGIN, buttonSize, buttons.size() * buttonSize));
		case BOTTOM -> drawRectangle.setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height() + CONTAINER_BUTTON_MARGIN, buttons.size() * buttonSize,
				buttonSize));
		case LEFT -> drawRectangle.setBounds(new Rectangle(editPartBounds.x() - buttonSize - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height + CONTAINER_BUTTON_MARGIN - buttons.size() * buttonSize,
				buttonSize, buttons.size() * buttonSize));
		default -> throw new IllegalArgumentException();
		}
	}

	public void addButton(final ContextButton button) {
		this.add(button);

		final Rectangle containerBounds = getBounds();
		switch (position) {
		case TOP -> {
			button.setBounds(new Rectangle(
					containerBounds.x() + containerBounds.width() - 2 * BUTTON_SIZE - CONTAINER_BUTTON_MARGIN
							- buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
					containerBounds.y() + CONTAINER_BUTTON_MARGIN, BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case RIGHT -> {
			button.setBounds(new Rectangle(
					containerBounds.x() + 2 * CONTAINER_BUTTON_MARGIN, containerBounds.y() + 2 * CONTAINER_BUTTON_MARGIN
							+ BUTTON_SIZE + buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
					BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case BOTTOM -> {
			button.setBounds(new Rectangle(
					containerBounds.x() + (1 + buttons.size()) * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
					containerBounds.y() + 2 * CONTAINER_BUTTON_MARGIN, BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case LEFT -> {
			button.setBounds(
					new Rectangle(containerBounds.x() + CONTAINER_BUTTON_MARGIN,
							containerBounds.y() + containerBounds.height() - 2 * (BUTTON_SIZE + CONTAINER_BUTTON_MARGIN)
									- buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
							BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		default -> throw new IllegalArgumentException();
		}
		updateDrawRectangle(curEditPartBounds);
	}

	/**
	 * @see Shape#fillShape(Graphics)
	 */
	@Override
	protected void fillShape(final Graphics graphics) {
		// not yet used
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	@Override
	protected void outlineShape(final Graphics graphics) {
		setAlpha(ModifiedMoveHandle.SELECTION_FILL_ALPHA);
		setForegroundColor(ModifiedMoveHandle.getSelectionColor());
		graphics.setLineWidth(ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		graphics.drawRoundRectangle(drawRectangle.getCopy().shrink(2, 2), 4, 4);
	}

	@Override
	public void figureMoved(final IFigure source) {
		final Rectangle editPartBounds = source.getBounds();
		final int widthChanged = curEditPartBounds.width() - editPartBounds.width();
		final int heightChanged = curEditPartBounds.height() - editPartBounds.height();
		this.curEditPartBounds = editPartBounds.getCopy();

		updateContainer(editPartBounds);
		updateButtons(widthChanged, heightChanged);
		updateDrawRectangle(editPartBounds);
	}
}