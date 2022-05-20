/**
 * generated by Xtext 2.25.0
 */
package org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fordiac.ide.model.structuredtext.structuredText.ArrayVariable;
import org.eclipse.fordiac.ide.model.structuredtext.structuredText.Expression;
import org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage;
import org.eclipse.fordiac.ide.model.structuredtext.structuredText.Variable;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Array Variable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl.ArrayVariableImpl#getArray <em>Array</em>}</li>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl.ArrayVariableImpl#getIndex <em>Index</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArrayVariableImpl extends VariableImpl implements ArrayVariable
{
  /**
	 * The cached value of the '{@link #getArray() <em>Array</em>}' containment reference.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @see #getArray()
	 * @generated
	 * @ordered
	 */
  protected Variable array;

  /**
	 * The cached value of the '{@link #getIndex() <em>Index</em>}' containment reference list.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
  protected EList<Expression> index;

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected ArrayVariableImpl()
  {
		super();
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  protected EClass eStaticClass()
  {
		return StructuredTextPackage.Literals.ARRAY_VARIABLE;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Variable getArray()
  {
		return array;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  public NotificationChain basicSetArray(Variable newArray, NotificationChain msgs)
  {
		Variable oldArray = array;
		array = newArray;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, StructuredTextPackage.ARRAY_VARIABLE__ARRAY, oldArray, newArray);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void setArray(Variable newArray)
  {
		if (newArray != array) {
			NotificationChain msgs = null;
			if (array != null)
				msgs = ((InternalEObject)array).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - StructuredTextPackage.ARRAY_VARIABLE__ARRAY, null, msgs);
			if (newArray != null)
				msgs = ((InternalEObject)newArray).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - StructuredTextPackage.ARRAY_VARIABLE__ARRAY, null, msgs);
			msgs = basicSetArray(newArray, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StructuredTextPackage.ARRAY_VARIABLE__ARRAY, newArray, newArray));
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public EList<Expression> getIndex()
  {
		if (index == null) {
			index = new EObjectContainmentEList<Expression>(Expression.class, this, StructuredTextPackage.ARRAY_VARIABLE__INDEX);
		}
		return index;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
		switch (featureID) {
			case StructuredTextPackage.ARRAY_VARIABLE__ARRAY:
				return basicSetArray(null, msgs);
			case StructuredTextPackage.ARRAY_VARIABLE__INDEX:
				return ((InternalEList<?>)getIndex()).basicRemove(otherEnd, msgs);
			default:
				return super.eInverseRemove(otherEnd, featureID, msgs);
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
		switch (featureID) {
			case StructuredTextPackage.ARRAY_VARIABLE__ARRAY:
				return getArray();
			case StructuredTextPackage.ARRAY_VARIABLE__INDEX:
				return getIndex();
			default:
				return super.eGet(featureID, resolve, coreType);
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
		switch (featureID) {
			case StructuredTextPackage.ARRAY_VARIABLE__ARRAY:
				setArray((Variable)newValue);
				return;
			case StructuredTextPackage.ARRAY_VARIABLE__INDEX:
				getIndex().clear();
				getIndex().addAll((Collection<? extends Expression>)newValue);
				return;
			default:
				super.eSet(featureID, newValue);
				return;
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void eUnset(int featureID)
  {
		switch (featureID) {
			case StructuredTextPackage.ARRAY_VARIABLE__ARRAY:
				setArray((Variable)null);
				return;
			case StructuredTextPackage.ARRAY_VARIABLE__INDEX:
				getIndex().clear();
				return;
			default:
				super.eUnset(featureID);
				return;
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public boolean eIsSet(int featureID)
  {
		switch (featureID) {
			case StructuredTextPackage.ARRAY_VARIABLE__ARRAY:
				return array != null;
			case StructuredTextPackage.ARRAY_VARIABLE__INDEX:
				return index != null && !index.isEmpty();
			default:
				return super.eIsSet(featureID);
		}
	}

} //ArrayVariableImpl
