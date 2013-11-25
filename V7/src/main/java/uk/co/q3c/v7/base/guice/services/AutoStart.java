package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Causes a 'predecessor' service to be automatically started when the class using it applies this annotation to the
 * field which holds it.
 * 
 * @author David Sowerby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoStart {
	boolean auto() default true;
}
