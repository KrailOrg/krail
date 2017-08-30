package uk.q3c.krail.core.option;

import com.vaadin.v7.data.Container;
import uk.q3c.krail.option.persist.OptionSource;

import java.lang.annotation.Annotation;

/**
 * Extends {@link OptionSource} to enable retrieval of a container.  This will be deprecated when Krail moves to Vaadin 8
 * <p>
 * Created by David Sowerby on 17 Aug 2017
 */
public interface VaadinOptionSource extends OptionSource {

    Container getContainer(Class<? extends Annotation> annotationClass);
}
