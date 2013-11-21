package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Normally, implementations of {@link Service} are registered and started automatically, using the {@link Service}
 * interface to identify candidates. However, there may be occasions when this is not desirable - use this annotation on
 * the implementation class to prevent automatic registration. The service can subsequently be registered and started
 * manually by a call to {@link ServicesManager#registerService(Service)}
 * 
 * @author David Sowerby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NoAutoRegister {

}
