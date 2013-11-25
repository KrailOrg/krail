package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which causes a {@link ServiceStatusChangeListener} to be added to a field. Only applicable to a field of
 * type {@link Service}
 * 
 * @author David Sowerby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListenToStatus {
	Class<? extends Service>[] services() default {};
}
