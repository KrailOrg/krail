package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify which Services the annotated class is dependent on. This enables the {@link ServicesManager}
 * to start and stop services in the right order
 * 
 * @author David Sowerby
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DependsOnServices {
	Class<? extends Service>[] services() default {};
}
