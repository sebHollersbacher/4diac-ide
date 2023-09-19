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
		Top, Bottom, Left, Right
	}

	private static final int BUTTON_SIZE = 21;
	private static final int CONTAINER_BUTTON_MARGIN = 5;
	private final Pos position;
	private final Rectangle drawRectangle = new Rectangle();

	private final List<ContextButton> buttons = new ArrayList<>();

	public ContextButtonContainer(final Rectangle editPartBounds, final Pos position) {
		this.position = position;
		initContainer(editPartBounds);
	}

	private void initContainer(final Rectangle editPartBounds) {
		switch (position) {
		case Top -> {
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.width() + 2 * CONTAINER_BUTTON_MARGIN, BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN));

			this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.width() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN,
					BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN));
		}
		case Right -> {
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width() + CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - CONTAINER_BUTTON_MARGIN, BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.height() + 2 * CONTAINER_BUTTON_MARGIN));
			this.setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width(),
					editPartBounds.y() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
					BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.height() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN));
		}
		case Bottom -> {
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() + editPartBounds.height() + CONTAINER_BUTTON_MARGIN,
					editPartBounds.width() + 2 * CONTAINER_BUTTON_MARGIN, BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN));
			this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() + editPartBounds.height(),
					editPartBounds.width() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN,
					BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN));
		}
		case Left -> {
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - CONTAINER_BUTTON_MARGIN, BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.height() + 2 * CONTAINER_BUTTON_MARGIN));
			this.setBounds(new Rectangle(editPartBounds.x() - BUTTON_SIZE - 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - BUTTON_SIZE - 2 * CONTAINER_BUTTON_MARGIN,
					BUTTON_SIZE + 3 * CONTAINER_BUTTON_MARGIN,
					editPartBounds.height() + 2 * BUTTON_SIZE + 4 * CONTAINER_BUTTON_MARGIN));
		}
		default -> throw new IllegalArgumentException();
		}
	}

	public void updateBounds(final Rectangle editPartBounds) {
		final int buttonSize = BUTTON_SIZE + 2 * CONTAINER_BUTTON_MARGIN;
		switch (position) {
		case Top -> drawRectangle.setBounds(new Rectangle(
				editPartBounds.x() + 2 * CONTAINER_BUTTON_MARGIN + editPartBounds.width() - buttons.size() * buttonSize,
				editPartBounds.y() - buttonSize - CONTAINER_BUTTON_MARGIN, buttons.size() * buttonSize, buttonSize));
		case Right ->
			drawRectangle.setBounds(new Rectangle(editPartBounds.x() + editPartBounds.width() + CONTAINER_BUTTON_MARGIN,
					editPartBounds.y() - CONTAINER_BUTTON_MARGIN, buttonSize, buttons.size() * buttonSize));
		case Bottom -> drawRectangle.setBounds(new Rectangle(editPartBounds.x() - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height() + CONTAINER_BUTTON_MARGIN, buttons.size() * buttonSize,
				buttonSize));
		case Left -> drawRectangle.setBounds(new Rectangle(editPartBounds.x() - buttonSize - CONTAINER_BUTTON_MARGIN,
				editPartBounds.y() + editPartBounds.height + CONTAINER_BUTTON_MARGIN - buttons.size() * buttonSize,
				buttonSize, buttons.size() * buttonSize));
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
		setAlpha(ModifiedMoveHandle.SELECTION_FILL_ALPHA);
		setForegroundColor(ModifiedMoveHandle.getSelectionColor());
		graphics.setLineWidth(ModifiedMoveHandle.SELECTION_BORDER_WIDTH);
		graphics.drawRoundRectangle(drawRectangle.getCopy().shrink(2, 2), 4, 4);
	}

	public void addButton(final ContextButton button) {
		this.add(button);

		switch (position) {
		case Top -> {
			button.setBounds(new Rectangle(
					getBounds().x() + drawRectangle.width() + CONTAINER_BUTTON_MARGIN
							- buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
					getBounds().y() + CONTAINER_BUTTON_MARGIN, BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case Right -> {
			button.setBounds(new Rectangle(
					getBounds().x() + 2 * CONTAINER_BUTTON_MARGIN, getBounds().y() + 2 * CONTAINER_BUTTON_MARGIN
							+ BUTTON_SIZE + buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
					BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case Bottom -> {
			button.setBounds(
					new Rectangle(getBounds().x() + (1 + buttons.size()) * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
							getBounds().y() + 2 * CONTAINER_BUTTON_MARGIN, BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		case Left -> {
			button.setBounds(
					new Rectangle(getBounds().x() + CONTAINER_BUTTON_MARGIN,
							getBounds().y() + drawRectangle.height()
									- buttons.size() * (2 * CONTAINER_BUTTON_MARGIN + BUTTON_SIZE),
							BUTTON_SIZE, BUTTON_SIZE));
			this.buttons.add(button);
		}
		default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void figureMoved(final IFigure source) {
		updateBounds(source.getBounds());
	}
}