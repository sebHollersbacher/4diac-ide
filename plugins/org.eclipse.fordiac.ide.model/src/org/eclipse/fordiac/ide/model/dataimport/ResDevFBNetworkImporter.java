/*******************************************************************************
 * Copyright (c) 2017 fortiss GmbH
 * 				 2020 Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl - initial API and implementation and/or initial documentation
 *   			 - Changed XML parsing to Staxx cursor interface for improved
 *  			   parsing performance
 *******************************************************************************/
package org.eclipse.fordiac.ide.model.dataimport;

import javax.xml.stream.XMLStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.fordiac.ide.model.Palette.Palette;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;

class ResDevFBNetworkImporter extends SubAppNetworkImporter {

	private final EList<VarDeclaration> varInputs;

	ResDevFBNetworkImporter(Palette palette, EList<VarDeclaration> varInputs, XMLStreamReader reader) {
		super(palette, reader);
		this.varInputs = varInputs;
	}

	ResDevFBNetworkImporter(Palette palette, FBNetwork fbNetwork, EList<VarDeclaration> varInputs,
			XMLStreamReader reader) {
		super(palette, fbNetwork, reader);
		this.varInputs = varInputs;
	}

	@Override
	protected IInterfaceElement getContainingInterfaceElement(String interfaceElement) {
		for (VarDeclaration var : varInputs) {
			if (var.getName().equals(interfaceElement)) {
				return var;
			}
		}
		return null;
	}

}
