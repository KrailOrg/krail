package uk.q3c.krail.i18n;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A binding annotation to identify registered I18N annotations.  See {@link I18NModule#registeredAnnotations}
 * <p>
 * Created by David Sowerby on 28/01/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@BindingAnnotation
public @interface I18NAnnotation {
}
