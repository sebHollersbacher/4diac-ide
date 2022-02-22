/*******************************************************************************
 * Copyright (c) 2022 Primetals Technologies Austria GmbH
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Ulzii Jargalsaikhan - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.structuredtextfunctioneditor.tests

import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.formatter.*
import com.google.inject.Inject

@ExtendWith(InjectionExtension)
@InjectWith(STFunctionInjectorProvider)
class Formatter2Test {

	@Inject extension FormatterTestHelper

	@Test
	def void testVariableDeclarations() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert VAR internal1 : REAL; END_VAR VAR CONSTANT internalConstant1 : LREAL ; END_VAR VAR_TEMP temporary : USINT; END_VAR VAR_INPUT bol1:BOOL:=BOOL#TRUE;bol2:BOOL:= TRUE; bol3 : BOOL := BOOL#TRUE; bol4 :BOOL:=BOOL#FALSE; bol5 :BOOL := true ; bol6 : BOOL := false; bol7 : BOOL := BOOL#true; bol8 : BOOL := BOOL#false; bol9 : BOOL := BOOL#0; bol10 : BOOL := BOOL#1; bol11 : BOOL := 0; bol12 : BOOL := 1; structMaster : DRV_InputStruct; test1: TestStruct1; test : NewStruct; test2 : NewStruct; END_VAR VAR_OUTPUT real1: REAL; real1: REAL := REAL#+1.0; real1: REAL := REAL#-1; real1: REAL := REAL#1.4e3; real1: REAL := REAL#-1.4e+3; real1: REAL := REAL#+1.4e-3; int1 : INT := 2#10001; int1 : INT := 8#723; int3 : INT := 16#AFFE; END_VAR END_FUNCTION
				'''
			expectation = '''
				FUNCTION hubert
				VAR
					internal1 : REAL;
				END_VAR
				VAR CONSTANT
					internalConstant1 : LREAL;
				END_VAR
				VAR_TEMP
					temporary : USINT;
				END_VAR
				VAR_INPUT
					bol1 : BOOL := BOOL#TRUE;
					bol2 : BOOL := TRUE;
					bol3 : BOOL := BOOL#TRUE;
					bol4 : BOOL := BOOL#FALSE;
					bol5 : BOOL := true;
					bol6 : BOOL := false;
					bol7 : BOOL := BOOL#true;
					bol8 : BOOL := BOOL#false;
					bol9 : BOOL := BOOL#0;
					bol10 : BOOL := BOOL#1;
					bol11 : BOOL := 0;
					bol12 : BOOL := 1;
					structMaster : DRV_InputStruct;
					test1 : TestStruct1;
					test : NewStruct;
					test2 : NewStruct;
				END_VAR
				VAR_OUTPUT
					real1 : REAL;
					real1 : REAL := REAL#+1.0;
					real1 : REAL := REAL#-1;
					real1 : REAL := REAL#1.4e3;
					real1 : REAL := REAL#-1.4e+3;
					real1 : REAL := REAL#+1.4e-3;
					int1 : INT := 2#10001;
					int1 : INT := 8#723;
					int3 : INT := 16#AFFE;
				END_VAR
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testIfStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert IF bol1 THEN bol1 := TRUE; ELSIF bol1 THEN bol1 := TRUE; ELSE bol1 := FALSE; bol1 := FALSE; END_IF; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				IF bol1 THEN
					bol1 := TRUE;
				ELSIF bol1 THEN
					bol1 := TRUE;
				ELSE
					bol1 := FALSE;
					bol1 := FALSE;
				END_IF;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testForStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert FOR int1:=1 TO 4 DO bol7 := FALSE; END_FOR; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				FOR int1 := 1 TO 4 DO
					bol7 := FALSE;
				END_FOR;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testWhileStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert WHILE bol1 = TRUE DO bol1 := TRUE; bol7 := FALSE; END_WHILE; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				WHILE bol1 = TRUE DO
					bol1 := TRUE;
					bol7 := FALSE;
				END_WHILE;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testRepeatStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert REPEAT bol1 := TRUE; bol7 := FALSE; UNTIL bol8 END_REPEAT; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				REPEAT
					bol1 := TRUE;
					bol7 := FALSE;
				UNTIL bol8 END_REPEAT;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testCaseStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert CASE bol0 OF 0: bol1 := TRUE;  bol1 := TRUE;  bol1 := TRUE; 1: bol7 := FALSE; 2: bol2 := FALSE; ELSE bol7 := FALSE;bol7 := FALSE;bol7 := FALSE;bol7 := FALSE; END_CASE; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				CASE bol0 OF
					0 :
						bol1 := TRUE;
						bol1 := TRUE;
						bol1 := TRUE;
					1 :
						bol7 := FALSE;
					2 :
						bol2 := FALSE;
					ELSE
						bol7 := FALSE;
						bol7 := FALSE;
						bol7 := FALSE;
						bol7 := FALSE;
				END_CASE;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testOneLineStatements() {
		assertFormatted[
			toBeFormatted = '''
				FUNCTION hubert EXIT; RETURN; CONTINUE; NOP; bol1 := TRUE; END_FUNCTION
			'''

			expectation = '''
				FUNCTION hubert
				EXIT;
				RETURN;
				CONTINUE;
				NOP;
				bol1 := TRUE;
				END_FUNCTION
			'''
		]
	}

	@Test
	def void testFunctionWithReturnType() {
		assertFormatted[
			toBeFormatted = '''FUNCTION hubert:INT CONTINUE;END_FUNCTION'''
			expectation = '''
			FUNCTION hubert : INT
			CONTINUE;
			END_FUNCTION
			'''
		]
	}

	@Test
	def void testNestedIf() {
		assertFormatted[
			toBeFormatted = '''FUNCTION hubert:INT IF bol1 THEN IF bol1 THEN bol1 := TRUE;	END_IF;	END_IF;END_FUNCTION'''
			expectation = '''
			FUNCTION hubert : INT
			IF bol1 THEN
				IF bol1 THEN
					bol1 := TRUE;
				END_IF;
			END_IF;
			END_FUNCTION
			'''
		]
	}
}
