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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.gef.policies.ModifiedMoveHandle;

public class ContextButtonContainer extends Shape {

	public enum Pos {
		Top, Bottom, Left, Right
	}

	private static final int CONTAINER_BUTTON_MARGIN = 5;
	private static final int PADDING = 10;
	private final Pos position;
	private final int buttonSize;

	private final List<ContextButton> buttons = new ArrayList<>();

	/**
	 * Constructs a round cornered rectangle.
	 */
	public ContextButtonContainer(final Rectangle editPartBounds, final Pos position, final int buttonSize) {
		this.position = position;
		this.buttonSize = buttonSize;
		initContainer(editPartBounds);
	}

	private void initContainer(final Rectangle editPartBounds) {
		switch (position) {
		case Top -> setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() - buttonSize - 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.width() + 2 * CONTAINER_BUTTON_MARGIN, buttonSize + 2 * CONTAINER_BUTTON_MARGIN));
		case Right -> setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width(),
				editPartBounds.y() - CONTAINER_BUTTON_MARGIN, buttonSize + 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.height() + 2 * CONTAINER_BUTTON_MARGIN));
		case Bottom -> setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height(), editPartBounds.width() + 2 * CONTAINER_BUTTON_MARGIN,
				buttonSize + 2 * CONTAINER_BUTTON_MARGIN));
		case Left -> setBounds(new Rectangle(editPartBounds.x() - buttonSize - 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() - CONTAINER_BUTTON_MARGIN, buttonSize + 2 * CONTAINER_BUTTON_MARGIN,
				editPartBounds.height() + 2 * CONTAINER_BUTTON_MARGIN));
		default -> throw new IllegalArgumentException();
		}
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
		setLineWidth(2 * ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		setAlpha(ModifiedMoveHandle.SELECTION_FILL_ALPHA);
		setForegroundColor(ModifiedMoveHandle.getSelectionColor());
		graphics.drawRectangle(getBounds().getCopy().shrink(2, 2));
	}

	public void addButton(final ContextButton button) {
		this.add(button);

		switch (position) {
		case Top -> addTopButton(button);
		case Right -> addRightButton(button);
		case Bottom -> addBottomButton(button);
		case Left -> addLeftButton(button);
		default -> throw new IllegalArgumentException();
		}
	}

	private void addTopButton(final ContextButton button) {
		final Rectangle containerBounds = getBounds();
		button.setBounds(new Rectangle(
				containerBounds.x() + containerBounds.width() - buttonSize - CONTAINER_BUTTON_MARGIN
						- buttons.size() * (PADDING + buttonSize),
				containerBounds.y() + CONTAINER_BUTTON_MARGIN, buttonSize, buttonSize));
		this.buttons.add(button);
	}

	private void addRightButton(final ContextButton button) {
		final Rectangle containerBounds = getBounds();
		button.setBounds(
				new Rectangle(containerBounds.x() + containerBounds.width() - buttonSize - CONTAINER_BUTTON_MARGIN,
						containerBounds.y() + CONTAINER_BUTTON_MARGIN + buttons.size() * (PADDING + buttonSize),
						buttonSize, buttonSize));
		this.buttons.add(button);
	}

	private void addBottomButton(final ContextButton button) {
		final Rectangle containerBounds = getBounds();
		button.setBounds(
				new Rectangle(containerBounds.x() + CONTAINER_BUTTON_MARGIN + buttons.size() * (PADDING + buttonSize),
						containerBounds.y() + containerBounds.height() - buttonSize - CONTAINER_BUTTON_MARGIN,
						buttonSize, buttonSize));
		this.buttons.add(button);
	}

	private void addLeftButton(final ContextButton button) {
		final Rectangle containerBounds = getBounds();
		button.setBounds(new Rectangle(
				containerBounds.x() + CONTAINER_BUTTON_MARGIN, containerBounds.y() + containerBounds.height()
						- buttonSize - CONTAINER_BUTTON_MARGIN - buttons.size() * (PADDING + buttonSize),
				buttonSize, buttonSize));
		this.buttons.add(button);
	}
}