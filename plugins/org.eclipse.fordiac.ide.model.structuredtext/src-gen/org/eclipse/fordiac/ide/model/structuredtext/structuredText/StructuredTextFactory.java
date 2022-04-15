/**
 * generated by Xtext 2.25.0
 */
package org.eclipse.fordiac.ide.model.structuredtext.structuredText;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage
 * @generated
 */
public interface StructuredTextFactory extends EFactory
{
  /**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  StructuredTextFactory eINSTANCE = org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl.StructuredTextFactoryImpl.init();

  /**
	 * Returns a new object of class '<em>Algorithm</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Algorithm</em>'.
	 * @generated
	 */
  StructuredTextAlgorithm createStructuredTextAlgorithm();

  /**
	 * Returns a new object of class '<em>Statement List</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Statement List</em>'.
	 * @generated
	 */
  StatementList createStatementList();

  /**
	 * Returns a new object of class '<em>Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Statement</em>'.
	 * @generated
	 */
  Statement createStatement();

  /**
	 * Returns a new object of class '<em>Assignment Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Assignment Statement</em>'.
	 * @generated
	 */
  AssignmentStatement createAssignmentStatement();

  /**
	 * Returns a new object of class '<em>FB Call</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>FB Call</em>'.
	 * @generated
	 */
  FBCall createFBCall();

  /**
	 * Returns a new object of class '<em>If Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>If Statement</em>'.
	 * @generated
	 */
  IfStatement createIfStatement();

  /**
	 * Returns a new object of class '<em>Else If Clause</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Else If Clause</em>'.
	 * @generated
	 */
  ElseIfClause createElseIfClause();

  /**
	 * Returns a new object of class '<em>Else Clause</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Else Clause</em>'.
	 * @generated
	 */
  ElseClause createElseClause();

  /**
	 * Returns a new object of class '<em>Case Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Case Statement</em>'.
	 * @generated
	 */
  CaseStatement createCaseStatement();

  /**
	 * Returns a new object of class '<em>Case Clause</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Case Clause</em>'.
	 * @generated
	 */
  CaseClause createCaseClause();

  /**
	 * Returns a new object of class '<em>For Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>For Statement</em>'.
	 * @generated
	 */
  ForStatement createForStatement();

  /**
	 * Returns a new object of class '<em>While Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>While Statement</em>'.
	 * @generated
	 */
  WhileStatement createWhileStatement();

  /**
	 * Returns a new object of class '<em>Repeat Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Repeat Statement</em>'.
	 * @generated
	 */
  RepeatStatement createRepeatStatement();

  /**
	 * Returns a new object of class '<em>Expression</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Expression</em>'.
	 * @generated
	 */
  Expression createExpression();

  /**
	 * Returns a new object of class '<em>Call</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Call</em>'.
	 * @generated
	 */
  Call createCall();

  /**
	 * Returns a new object of class '<em>Argument</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Argument</em>'.
	 * @generated
	 */
  Argument createArgument();

  /**
	 * Returns a new object of class '<em>In Argument</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>In Argument</em>'.
	 * @generated
	 */
  InArgument createInArgument();

  /**
	 * Returns a new object of class '<em>Out Argument</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Out Argument</em>'.
	 * @generated
	 */
  OutArgument createOutArgument();

  /**
	 * Returns a new object of class '<em>Variable</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Variable</em>'.
	 * @generated
	 */
  Variable createVariable();

  /**
	 * Returns a new object of class '<em>Adapter Variable</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Adapter Variable</em>'.
	 * @generated
	 */
  AdapterVariable createAdapterVariable();

  /**
	 * Returns a new object of class '<em>Partial Access</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Partial Access</em>'.
	 * @generated
	 */
  PartialAccess createPartialAccess();

  /**
	 * Returns a new object of class '<em>Primary Variable</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Primary Variable</em>'.
	 * @generated
	 */
  PrimaryVariable createPrimaryVariable();

  /**
	 * Returns a new object of class '<em>Constant</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Constant</em>'.
	 * @generated
	 */
  Constant createConstant();

  /**
	 * Returns a new object of class '<em>Numeric Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Numeric Literal</em>'.
	 * @generated
	 */
  NumericLiteral createNumericLiteral();

  /**
	 * Returns a new object of class '<em>Int Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Int Literal</em>'.
	 * @generated
	 */
  IntLiteral createIntLiteral();

  /**
	 * Returns a new object of class '<em>Real Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Real Literal</em>'.
	 * @generated
	 */
  RealLiteral createRealLiteral();

  /**
	 * Returns a new object of class '<em>Bool Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bool Literal</em>'.
	 * @generated
	 */
  BoolLiteral createBoolLiteral();

  /**
	 * Returns a new object of class '<em>String Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>String Literal</em>'.
	 * @generated
	 */
  StringLiteral createStringLiteral();

  /**
	 * Returns a new object of class '<em>Time Literal</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Time Literal</em>'.
	 * @generated
	 */
  TimeLiteral createTimeLiteral();

  /**
	 * Returns a new object of class '<em>Local Variable</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Variable</em>'.
	 * @generated
	 */
  LocalVariable createLocalVariable();

  /**
	 * Returns a new object of class '<em>Super Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Super Statement</em>'.
	 * @generated
	 */
  SuperStatement createSuperStatement();

  /**
	 * Returns a new object of class '<em>Return Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Return Statement</em>'.
	 * @generated
	 */
  ReturnStatement createReturnStatement();

  /**
	 * Returns a new object of class '<em>Exit Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Exit Statement</em>'.
	 * @generated
	 */
  ExitStatement createExitStatement();

  /**
	 * Returns a new object of class '<em>Continue Statement</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Continue Statement</em>'.
	 * @generated
	 */
  ContinueStatement createContinueStatement();

  /**
	 * Returns a new object of class '<em>Binary Expression</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Binary Expression</em>'.
	 * @generated
	 */
  BinaryExpression createBinaryExpression();

  /**
	 * Returns a new object of class '<em>Unary Expression</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unary Expression</em>'.
	 * @generated
	 */
  UnaryExpression createUnaryExpression();

  /**
	 * Returns a new object of class '<em>Array Variable</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Array Variable</em>'.
	 * @generated
	 */
  ArrayVariable createArrayVariable();

  /**
	 * Returns a new object of class '<em>Adapter Root</em>'.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return a new object of class '<em>Adapter Root</em>'.
	 * @generated
	 */
  AdapterRoot createAdapterRoot();

  /**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
  StructuredTextPackage getStructuredTextPackage();

} //StructuredTextFactory
