package uk.co.q3c.v7.base.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to assist in the management of {@link Service} dependencies, and is valid only when applied to a field of a
 * {@link Service}, when that field is also a {@link Service}.
 * <p>
 * System behaviour is determined by the options selected, and the code for that is provided by {@link AbstractService}
 * - please see the Javadoc of {@link AbstractService} for details.
 * <p>
 * <b>WARNING:<b> It is entirely possible to create a loop using this annotation, where Service A depends on Service B
 * which depends on Service A. There is currently no detection for this situation. See
 * https://github.com/davidsowerby/v7/issues/240
 * 
 * @author David Sowerby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dependency {

	boolean requiredAtStart() default true;

	boolean stopOnStop() default true;

	boolean startOnRestart() default true;

}
