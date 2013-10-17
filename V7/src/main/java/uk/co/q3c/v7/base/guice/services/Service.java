package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
	/**
	 * Specify if the service should be started immediatly or only when the user
	 * create an instance.
	 */
	boolean startAsSoonAsPossible() default false;
	
	
}