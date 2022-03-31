/*
 * generated by Xtext 2.26.0
 */
package org.eclipse.fordiac.ide.structuredtextalgorithm.ui;

import com.google.inject.Injector;
import org.eclipse.fordiac.ide.structuredtextalgorithm.ui.internal.StructuredtextalgorithmActivator;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class STAlgorithmExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return FrameworkUtil.getBundle(StructuredtextalgorithmActivator.class);
	}
	
	@Override
	protected Injector getInjector() {
		StructuredtextalgorithmActivator activator = StructuredtextalgorithmActivator.getInstance();
		return activator != null ? activator.getInjector(StructuredtextalgorithmActivator.ORG_ECLIPSE_FORDIAC_IDE_STRUCTUREDTEXTALGORITHM_STALGORITHM) : null;
	}

}
