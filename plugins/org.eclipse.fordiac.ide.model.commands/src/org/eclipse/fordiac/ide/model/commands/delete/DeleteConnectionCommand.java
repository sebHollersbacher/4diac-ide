/*******************************************************************************
 * Copyright (c) 2008, 2021 Profactor GmbH, fortiss GmbH, AIT,
 *                          Johannes Keppler University Linz
 *               2022 Primetals Technologies Austria GmbH
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl, Filip Pröstl Andren, Monika Wenger
 *   	- initial API and implementation and/or initial documentation
 *   Alois Zoitl - removed editor check from canUndo
 *   Michael Oberlehner - added support for error marker handling
 *                      - added check for error marker connection
 *******************************************************************************/
package org.eclipse.fordiac.ide.model.commands.delete;

import org.eclipse.fordiac.ide.model.commands.Messages;
import org.eclipse.fordiac.ide.model.errormarker.ErrorMarkerBuilder;
import org.eclipse.fordiac.ide.model.helpers.ConnectionsHelper;
import org.eclipse.fordiac.ide.model.libraryElement.Connection;
import org.eclipse.fordiac.ide.model.libraryElement.ErrorMarkerInterface;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class DeleteConnectionCommand extends Command {
	private IInterfaceElement source;
	private IInterfaceElement destination;
	private final Connection connection;
	private final FBNetwork connectionParent;
	private boolean performMappingCheck;
	private DeleteConnectionCommand deleteMapped = null;
	private final CompoundCommand deleteInterfaceErrorMarkers = new CompoundCommand();
	private FBNetworkElement errorFb;
	ErrorMarkerBuilder deleteConnectionErrorMarker = null;

	private boolean keepMarker; // we want to keep the marker

	public DeleteConnectionCommand(final Connection connection) {
		this(connection, null);
		this.errorFb = null;
		keepMarker = false;
	}

	public DeleteConnectionCommand(final Connection connection, final boolean keepMarker) {
		this(connection, null);
		this.errorFb = null;
		this.keepMarker = keepMarker;
	}

	public DeleteConnectionCommand(final Connection connection, final FBNetworkElement errorFb) {
		super(Messages.DeleteConnectionCommand_DeleteConnection);
		this.connection = connection;
		if (this.connection.eContainer() instanceof FBNetwork) {
			connectionParent = (FBNetwork) this.connection.eContainer();
		} else {
			connectionParent = null;
		}
		performMappingCheck = true;
		this.errorFb = errorFb;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void execute() {
		source = connection.getSource();
		destination = connection.getDestination();

		if (performMappingCheck) {
			deleteMapped = checkAndDeleteMirroredConnection();
			if (null != deleteMapped) {
				deleteMapped.execute();
			}
		}
		checkErrorMarker();
		deleteConnection();
		deleteInterfaceErrorMarkers.execute();
	}

	@Override
	public void redo() {
		if (deleteConnectionErrorMarker != null) {
			deleteConnectionErrorMarker.deleteErrorMarker();
		}
		deleteInterfaceErrorMarkers.redo();
		deleteConnection();
	}

	private void deleteConnection() {
		connection.setSource(null);
		connection.setDestination(null);
		if (null != deleteMapped) {
			deleteMapped.redo();
		}
		if (connectionParent != null) {
			connectionParent.removeConnection(connection);
		}
	}

	@Override
	public void undo() {
		deleteInterfaceErrorMarkers.undo();
		connection.setSource(source);
		connection.setDestination(destination);
		if (connectionParent != null) {
			connectionParent.addConnection(connection);
		}
		if (null != deleteMapped) {
			deleteMapped.undo();
		}

		if (deleteConnectionErrorMarker != null) {
			deleteConnectionErrorMarker.createMarkerInFile();
		}

	}

	private DeleteConnectionCommand checkAndDeleteMirroredConnection() {
		final Connection opposite = ConnectionsHelper.getOppositeConnection(connection);
		if (null != opposite) {
			final DeleteConnectionCommand cmd = new DeleteConnectionCommand(opposite);
			cmd.setPerformMappingCheck(false); // as this is the command for the mirrored connection we don't
			// want again to check
			return (cmd.canExecute()) ? cmd : null;
		}
		return null;
	}

	private void setPerformMappingCheck(final boolean performMappingCheck) {
		this.performMappingCheck = performMappingCheck;
	}

	private void checkErrorMarker() {
		if (isErrorMarkerToDelete(source) && !keepMarker) {
			deleteInterfaceErrorMarkers.add(new DeleteErrorMarkerCommand((ErrorMarkerInterface) source, errorFb));
		}
		if (isErrorMarkerToDelete(destination) && !keepMarker) {
			deleteInterfaceErrorMarkers.add(new DeleteErrorMarkerCommand((ErrorMarkerInterface) destination, errorFb));
		}

		if (connection.hasError() && !keepMarker) {
			deleteConnectionErrorMarker = ErrorMarkerBuilder.deleteErrorMarker(connection);
			deleteInterfaceErrorMarkers.add(new DeleteErrorMarkerCommand((ErrorMarkerInterface) source, errorFb));
			deleteInterfaceErrorMarkers.add(new DeleteErrorMarkerCommand((ErrorMarkerInterface) destination, errorFb));
		}
	}

	private static boolean isErrorMarkerToDelete(final IInterfaceElement ie) {
		// we only have to remove the error marker interface if it is one, if it is still in the model (i.e., has not
		// already been deleted by someone else) and it has no further connections
		return ie instanceof ErrorMarkerInterface && ie.eContainer() != null && hasNoOtherConnections(ie);
	}

	private static boolean hasNoOtherConnections(final IInterfaceElement ie) {
		return ie.isIsInput() ? ie.getInputConnections().size() <= 1 : ie.getOutputConnections().size() <= 1;
	}

	public IInterfaceElement getSource() {
		return source;
	}

	public IInterfaceElement getDestination() {
		return destination;
	}

	public FBNetwork getConnectionParent() {
		return connectionParent;
	}

}
