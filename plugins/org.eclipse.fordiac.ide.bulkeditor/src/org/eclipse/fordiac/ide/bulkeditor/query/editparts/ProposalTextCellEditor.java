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

import org.eclipse.fordiac.ide.ui.widget.nattable.NatTableWidgetFactory;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

/** Text cell editor which proposes values while typing. */
public class ProposalTextCellEditor extends TextCellEditor {

	private final ContentProposalAdapter proposalAdapter;

	public ProposalTextCellEditor(final Composite parent, final IContentProposalProvider proposalProvider) {
		super(parent);
		proposalAdapter = new ContentProposalAdapter(text, new TextContentAdapter(), proposalProvider,
				KeyStroke.getInstance(SWT.CTRL, SWT.SPACE), NatTableWidgetFactory.getActivationChars());
		proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	@Override
	protected void keyReleaseOccured(final KeyEvent keyEvent) {
		// while the proposals are shown the keys belong to the proposal popup
		if (!proposalAdapter.isProposalPopupOpen()) {
			super.keyReleaseOccured(keyEvent);
		}
	}

	@Override
	protected void focusLost() {
		if (!proposalAdapter.isProposalPopupOpen()) {
			super.focusLost();
		}
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}
}
