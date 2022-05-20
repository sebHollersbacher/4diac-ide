/*******************************************************************************
 * Copyright (c) 2022 Martin Erich Jobst
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Martin Jobst - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.fbtypeeditor.editors;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;

public class FBTypeMultiPageEditorSite extends MultiPageEditorSite {
	public FBTypeMultiPageEditorSite(final MultiPageEditorPart multiPageEditor, final IEditorPart editor) {
		super(multiPageEditor, editor);
	}

	@Override
	public String getId() {
		final IEditorPart editorPart = getEditor();
		if (editorPart instanceof IFBTEditorPart) {
			return ((IFBTEditorPart) editorPart).getEditorId();
		}
		return super.getId();
	}
}
