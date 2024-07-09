/*******************************************************************************
 * Copyright (c) 2021 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Fabio Gandolfi - created this class to handle follow connections,
 *   				  jump to pin or parent, via right side
 *******************************************************************************/

package org.eclipse.fordiac.ide.application.handlers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.model.data.StructuredType;
import org.eclipse.fordiac.ide.model.libraryElement.Connection;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.fordiac.ide.model.libraryElement.MemberVarDeclaration;
import org.eclipse.fordiac.ide.model.libraryElement.StructManipulator;
import org.eclipse.fordiac.ide.model.libraryElement.SubApp;
import org.eclipse.fordiac.ide.model.ui.editors.HandlerHelper;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class FollowRightConnectionHandler extends FollowConnectionHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		final GraphicalViewer viewer = editor.getAdapter(GraphicalViewer.class);
		final StructuredSelection selection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);

		final IInterfaceElement originPin = ((InterfaceEditPart) ((IStructuredSelection) selection).getFirstElement())
				.getModel();

		if (originPin instanceof MemberVarDeclaration && originPin.isIsInput()) {
			final IInterfaceElement oppositePin = getInternalOppositePin(selection);
			if (oppositePin.getType() instanceof final StructuredType struct) {
				final List<FBNetworkElement> list = getStructOpposites(struct, oppositePin);

				final List<IInterfaceElement> interf = list.stream()
						.map(mux -> mux.getInterfaceElement(originPin.getName())).toList();

				if (interf.size() == 1) {
					// TODO use correct viewer
					HandlerHelper.selectElement(interf.getFirst(), viewer);
					return Status.OK_STATUS;
				}
				if (interf.size() > 1) {
					showOppositeSelectionDialog(interf, event, viewer, originPin, editor);
					return Status.OK_STATUS;
				}
			}
		}

		final InterfaceEditPart interfaceEditPart = (InterfaceEditPart) ((IStructuredSelection) selection)
				.getFirstElement();
		if (isEditorBorderPin(interfaceEditPart.getModel(), getFBNetwork(editor))
				&& !interfaceEditPart.getModel().isIsInput()) {
			gotoParent(event);
			return Status.OK_STATUS;
		}

		if (interfaceEditPart.isInput() && !isExpandedSubappPin(interfaceEditPart.getModel())) {
			HandlerHelper.selectElement(getInternalOppositePin(selection), viewer);
			return Status.OK_STATUS;
		}

		final List<IInterfaceElement> opposites = getConnectionOposites(interfaceEditPart);
		final List<IInterfaceElement> resolvedOpposites = resolveTargetPins(opposites, viewer);
		selectOpposites(event, viewer, originPin, resolvedOpposites, editor);
		return Status.OK_STATUS;
	}

	private static List<FBNetworkElement> getStructOpposites(final StructuredType struct,
			final IInterfaceElement oppositePin) {

		final List<FBNetworkElement> list = new ArrayList<>();
		final Deque<IInterfaceElement> queue = new ArrayDeque<>();
		queue.add(oppositePin);

		while (!queue.isEmpty()) {
			final IInterfaceElement currentPin = queue.pop();
			for (final Connection conn : currentPin.getOutputConnections()) {
//				while(!conn.getDestination().isIsInput())
				if (conn.getDestination().getFBNetworkElement() instanceof final StructManipulator structManipulator) {
					if (structManipulator.getDataType() == struct) {
						list.add(structManipulator);
					}

					structManipulator.getInterface().getOutputs()
							.filter(Predicate.not(StructuredType.class::isInstance)).forEach(queue::add);
				} else if (conn.getDestination().getFBNetworkElement() instanceof SubApp) {
					conn.getDestination().getOutputConnections().stream()
							.map((Function<? super Connection, ? extends IInterfaceElement>) Connection::getDestination)
							.filter(Predicate.not(StructuredType.class::isInstance)).forEach(queue::add);
				}
//				else if (conn.getDestination().getFBNetworkElement() instanceof final SubApp subApp) {
//					subApp.getInterface().getOutputs().filter(Predicate.not(StructuredType.class::isInstance))
//							.forEach(queue::add);
//				}
			}
		}

		return list;
	}

	@Override
	protected IInterfaceElement getInternalOppositeEventPin(final InterfaceEditPart pin) {
		final var eventOutputs = pin.getModel().getFBNetworkElement().getInterface().getEventOutputs();
		final var eventInputs = pin.getModel().getFBNetworkElement().getInterface().getEventInputs();

		if (eventOutputs.isEmpty()) {
			return getInternalOppositeVarPin(pin);
		}
		return calcInternalOppositePin(eventInputs, eventOutputs, pin);
	}

	@Override
	protected IInterfaceElement getInternalOppositeVarPin(final InterfaceEditPart pin) {
		final var varInputs = pin.getModel().getFBNetworkElement().getInterface().getInputVars();
		final var varOutputs = pin.getModel().getFBNetworkElement().getInterface().getOutputVars();

		if (varOutputs.isEmpty()) {
			return getInternalOppositeVarInOutPin(pin);
		}
		return calcInternalOppositePin(varInputs, varOutputs, pin);
	}

	@Override
	protected IInterfaceElement getInternalOppositeVarInOutPin(final InterfaceEditPart pin) {
		final var varInputs = pin.getModel().getFBNetworkElement().getInterface().getInOutVars();
		final var varOutputs = pin.getModel().getFBNetworkElement().getInterface().getOutMappedInOutVars();

		if (varInputs.isEmpty()) {
			return getInternalOppositePlugOrSocketPin(pin);
		}
		return calcInternalOppositePin(varInputs, varOutputs, pin);
	}

	@Override
	protected IInterfaceElement getInternalOppositePlugOrSocketPin(final InterfaceEditPart pin) {
		final var sockets = pin.getModel().getFBNetworkElement().getInterface().getSockets();
		final var plugs = pin.getModel().getFBNetworkElement().getInterface().getPlugs();

		if (plugs.isEmpty()) {
			return getInternalOppositeEventPin(pin);
		}
		return calcInternalOppositePin(sockets, plugs, pin);
	}

	@Override
	protected boolean hasOpposites(final InterfaceEditPart pin) {
		return !(pin.getModel().getFBNetworkElement().getInterface().getEventOutputs().isEmpty()
				&& pin.getModel().getFBNetworkElement().getInterface().getOutputVars().isEmpty()
				&& pin.getModel().getFBNetworkElement().getInterface().getPlugs().isEmpty());
	}

	@Override
	protected boolean isLeft() {
		return false;
	}

	@Override
	protected EList<Connection> getConnectionList(final IInterfaceElement ie) {
		return ie.getOutputConnections();
	}
}