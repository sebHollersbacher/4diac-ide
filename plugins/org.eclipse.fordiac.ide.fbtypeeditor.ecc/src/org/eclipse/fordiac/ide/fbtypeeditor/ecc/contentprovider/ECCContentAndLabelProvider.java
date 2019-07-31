/*******************************************************************************
 * Copyright (c) 2016, 2013, 2017 fortiss GmbH
 *               2019 Johannes Kepler University Linz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *   Bianca Wiesmayr
 *     - expanded for input events
 *******************************************************************************/
package org.eclipse.fordiac.ide.fbtypeeditor.ecc.contentprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.fordiac.ide.model.NamedElementComparator;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterDeclaration;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterEvent;
import org.eclipse.fordiac.ide.model.libraryElement.Algorithm;
import org.eclipse.fordiac.ide.model.libraryElement.BasicFBType;
import org.eclipse.fordiac.ide.model.libraryElement.ECAction;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElementFactory;

/**
 * Helper functions need by the action and transition edit parts.
 * 
 *
 */
public final class ECCContentAndLabelProvider {

	public static final String EMPTY_FIELD = "[none]"; // drop-down menu entry for selecting nothing

	public static List<Event> getOutputEvents(BasicFBType type) {
		List<Event> events = new ArrayList<>();
		if (null != type) {
			events.addAll(type.getInterfaceList().getEventOutputs());
			type.getInterfaceList().getSockets().forEach(socket -> events
					.addAll(createAdapterEventList(socket.getType().getInterfaceList().getEventInputs(), socket)));
			type.getInterfaceList().getPlugs().forEach(plug -> events
					.addAll(createAdapterEventList(plug.getType().getInterfaceList().getEventOutputs(), plug)));
			Collections.sort(events, NamedElementComparator.INSTANCE);
		}

		return events;
	}

	public static List<String> getOutputEventNames(BasicFBType type) {
		List<String> eventNames = getOutputEvents(type).stream().map(ev -> ev.getName()).collect(Collectors.toList());
		eventNames.add(EMPTY_FIELD);
		return eventNames;
	}

	public static List<Event> getInputEvents(BasicFBType type) {
		List<Event> transitionConditions = new ArrayList<>();
		if (null != type) {
			transitionConditions.addAll(type.getInterfaceList().getEventInputs());
			type.getInterfaceList().getSockets().forEach(socket -> transitionConditions
					.addAll(createAdapterEventList(socket.getType().getInterfaceList().getEventOutputs(), socket)));
			type.getInterfaceList().getPlugs().forEach(plug -> transitionConditions
					.addAll(createAdapterEventList(plug.getType().getInterfaceList().getEventInputs(), plug)));
			Collections.sort(transitionConditions, NamedElementComparator.INSTANCE);
		}
		return transitionConditions;
	}

	public static List<String> getInputEventNames(BasicFBType type) {
		List<String> transitionConditionNames = getInputEvents(type).stream().map(tc -> tc.getName())
				.collect(Collectors.toList());
		transitionConditionNames.add(EMPTY_FIELD);
		return transitionConditionNames;
	}

	// TODO move to a utility class as same function is used in
	// ECTransitionEditPart
	public static List<Event> createAdapterEventList(EList<Event> events, AdapterDeclaration adapter) {
		List<Event> adapterEvents = new ArrayList<>();

		for (Event event : events) {
			AdapterEvent ae = LibraryElementFactory.eINSTANCE.createAdapterEvent();
			ae.setName(event.getName());
			ae.setComment(event.getComment());
			ae.setAdapterDeclaration(adapter);
			adapterEvents.add(ae);
		}
		return adapterEvents;
	}

	public static List<Algorithm> getAlgorithms(BasicFBType type) {
		List<Algorithm> algorithms = new ArrayList<>();
		algorithms.addAll(type.getAlgorithm());

		Collections.sort(algorithms, NamedElementComparator.INSTANCE);
		return algorithms;
	}

	public static List<String> getAlgorithmNames(BasicFBType type) {
		List<String> algNames = getAlgorithms(type).stream().map(alg -> alg.getName()).collect(Collectors.toList());
		algNames.add(EMPTY_FIELD);
		return algNames;
	}

	public static BasicFBType getFBType(ECAction action) {
		if (null != action.eContainer() && null != action.eContainer().eContainer()
				&& null != action.eContainer().eContainer().eContainer()) {
			return (BasicFBType) action.eContainer().eContainer().eContainer();
		}
		return null;
	}

	private ECCContentAndLabelProvider() {
		throw new UnsupportedOperationException("ECActionHelpers should not be instantiated!");
	}
}
