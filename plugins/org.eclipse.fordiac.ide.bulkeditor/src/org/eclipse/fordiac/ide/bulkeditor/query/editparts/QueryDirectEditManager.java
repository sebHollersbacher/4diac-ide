/*******************************************************************************
 * Copyright (c) 2026 Primetals Technologies Austria GmbH
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
package org.eclipse.fordiac.ide.bulkeditor.query.editparts;

import org.eclipse.fordiac.ide.bulkeditor.query.figures.QueryNodeFigure.EditableValue;
import org.eclipse.fordiac.ide.gef.editparts.LabelDirectEditManager;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/** Edits a value shown in a label of a query node. */
public class QueryDirectEditManager extends LabelDirectEditManager {

	private final EditableValue editableValue;
	private final IContentProposalProvider proposalProvider;

	public QueryDirectEditManager(final GraphicalEditPart source, final EditableValue editableValue) {
		this(source, editableValue, null);
	}

	public QueryDirectEditManager(final GraphicalEditPart source, final EditableValue editableValue,
			final IContentProposalProvider proposalProvider) {
		super(source, editableValue.label());
		this.editableValue = editableValue;
		this.proposalProvider = proposalProvider;
	}

	@Override
	protected Object getDirectEditFeature() {
		return editableValue;
	}

	@Override
	protected CellEditor createCellEditorOn(final Composite composite) {
		if (proposalProvider != null) {
			return new ProposalTextCellEditor(composite, proposalProvider);
		}
		return super.createCellEditorOn(composite);
	}
}
