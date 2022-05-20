/**
 * generated by Xtext 2.25.0
 */
package org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fordiac.ide.model.structuredtext.structuredText.NumericLiteral;
import org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage;
import org.eclipse.fordiac.ide.model.structuredtext.structuredText.Type;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Numeric Literal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.impl.NumericLiteralImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NumericLiteralImpl extends ConstantImpl implements NumericLiteral
{
  /**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
  protected static final Type TYPE_EDEFAULT = Type.DINT;

  /**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
  protected Type type = TYPE_EDEFAULT;

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected NumericLiteralImpl()
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
		return StructuredTextPackage.Literals.NUMERIC_LITERAL;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Type getType()
  {
		return type;
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void setType(Type newType)
  {
		Type oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StructuredTextPackage.NUMERIC_LITERAL__TYPE, oldType, type));
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
			case StructuredTextPackage.NUMERIC_LITERAL__TYPE:
				return getType();
			default:
				return super.eGet(featureID, resolve, coreType);
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void eSet(int featureID, Object newValue)
  {
		switch (featureID) {
			case StructuredTextPackage.NUMERIC_LITERAL__TYPE:
				setType((Type)newValue);
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
			case StructuredTextPackage.NUMERIC_LITERAL__TYPE:
				setType(TYPE_EDEFAULT);
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
			case StructuredTextPackage.NUMERIC_LITERAL__TYPE:
				return type != TYPE_EDEFAULT;
			default:
				return super.eIsSet(featureID);
		}
	}

  /**
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public String toString()
  {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (type: ");
		result.append(type);
		result.append(')');
		return result.toString();
	}

} //NumericLiteralImpl
