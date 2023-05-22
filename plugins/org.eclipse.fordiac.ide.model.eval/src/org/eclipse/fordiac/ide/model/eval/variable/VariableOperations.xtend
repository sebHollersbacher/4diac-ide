/*******************************************************************************
 * Copyright (c) 2022 - 2023 Martin Erich Jobst
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
package org.eclipse.fordiac.ide.model.eval.variable

import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.fordiac.ide.model.data.AnyType
import org.eclipse.fordiac.ide.model.data.ArrayType
import org.eclipse.fordiac.ide.model.data.DataType
import org.eclipse.fordiac.ide.model.data.StructuredType
import org.eclipse.fordiac.ide.model.eval.value.Value
import org.eclipse.fordiac.ide.model.libraryElement.FB
import org.eclipse.fordiac.ide.model.libraryElement.FBType
import org.eclipse.fordiac.ide.model.libraryElement.INamedElement
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElementFactory
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration

import static extension org.eclipse.fordiac.ide.model.eval.EvaluatorFactory.*

final class VariableOperations {
	private new() {
	}

	def static Variable<?> newVariable(String name, INamedElement type) {
		switch (type) {
			ArrayType: new ArrayVariable(name, type)
			StructuredType: new StructVariable(name, type)
			AnyType: new ElementaryVariable(name, type)
			FBType: new FBVariable(name, type)
			default: throw new UnsupportedOperationException('''Cannot instanciate variable «name» of type «type.name»''')
		}
	}

	def static Variable<?> newVariable(String name, INamedElement type, String value) {
		switch (type) {
			ArrayType: new ArrayVariable(name, type, value)
			StructuredType: new StructVariable(name, type, value)
			AnyType: new ElementaryVariable(name, type, value)
			FBType: new FBVariable(name, type, value)
			default: throw new UnsupportedOperationException('''Cannot instanciate variable «name» of type «type.name»''')
		}
	}

	def static Variable<?> newVariable(String name, INamedElement type, Value value) {
		switch (type) {
			ArrayType: new ArrayVariable(name, type, value)
			StructuredType: new StructVariable(name, type, value)
			AnyType: new ElementaryVariable(name, type, value)
			FBType: new FBVariable(name, type, value)
			default: throw new UnsupportedOperationException('''Cannot instanciate variable «name» of type «type.name»''')
		}
	}

	def static Variable<?> newVariable(String name, Value value) {
		newVariable(name, value.type, value)
	}

	def static Variable<?> newVariable(VarDeclaration decl) {
		if (decl.array || !decl.initialValue.nullOrEmpty) {
			val evaluator = decl.createEvaluator(VarDeclaration, null, emptySet, null)
			if (evaluator instanceof VariableEvaluator) {
				evaluator.evaluateVariable
			} else
				throw new UnsupportedOperationException("No suitable evaluator for VarDeclaration found")
		} else
			newVariable(decl.name, decl.type)
	}

	def static Variable<?> newVariable(VarDeclaration decl, String initialValue) {
		decl.withValue(initialValue).newVariable
	}

	def static Variable<?> newVariable(VarDeclaration decl, Value value) {
		newVariable(decl.name, decl.evaluateResultType, value)
	}

	def static Variable<?> newVariable(FB fb) {
		newVariable(fb.name, fb.type)
	}

	def static INamedElement evaluateResultType(VarDeclaration decl) {
		if (decl.array) {
			val evaluator = decl.createEvaluator(VarDeclaration, null, emptySet, null)
			if (evaluator instanceof VariableEvaluator) {
				evaluator.evaluateResultType
			} else
				throw new UnsupportedOperationException("No suitable evaluator for VarDeclaration found")
		} else
			decl.type
	}

	def static String validateValue(VarDeclaration decl) {
		val evaluator = decl.createEvaluator(VarDeclaration, null, emptySet, null)
		try {
			evaluator?.prepare
			""
		} catch (Exception e) {
			e.message;
		}
	}

	def static String validateValue(VarDeclaration decl, String initialValue) {
		decl.withValue(initialValue).validateValue
	}

	def static String validateValue(DataType dataType, String initialValue) {
		(LibraryElementFactory.eINSTANCE.createVarDeclaration => [
			name = "dummy"
			type = dataType
			value = LibraryElementFactory.eINSTANCE.createValue => [value = initialValue]
		]).validateValue
	}

	def static Value evaluateValue(DataType dataType, String initialValue) {
		(LibraryElementFactory.eINSTANCE.createVarDeclaration => [
			name = "dummy"
			type = dataType
			value = LibraryElementFactory.eINSTANCE.createValue => [value = initialValue]
		]).newVariable.value
	}

	def static String getInitialValue(VarDeclaration decl) {
		decl.declaredInitialValue ?: decl.inheritedInitialValue
	}

	def static String getDeclaredInitialValue(VarDeclaration decl) {
		val result = decl.value?.value
		if(result.nullOrEmpty) null else result
	}

	def static String getInheritedInitialValue(VarDeclaration decl) {
		val result = decl.FBNetworkElement?.type?.interfaceList?.getVariable(decl.name)?.value?.value
		if(result.nullOrEmpty) null else result
	}

	def private static withValue(VarDeclaration decl, String valueString) {
		val copy = LibraryElementFactory.eINSTANCE.createVarDeclaration => [
			name = decl.name
			type = decl.type
			arraySize = decl.arraySize
			value = LibraryElementFactory.eINSTANCE.createValue => [value = valueString]
		]
		if (decl.eResource !== null) {
			new ResourceImpl(decl.eResource.URI).contents.add(copy)
		}
		copy
	}
}
