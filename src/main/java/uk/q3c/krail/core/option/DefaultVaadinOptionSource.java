package uk.q3c.krail.core.option;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.vaadin.data.Container;
import uk.q3c.krail.option.persist.source.DefaultOptionSource;
import uk.q3c.krail.persist.PersistenceInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by David Sowerby on 17 Aug 2017
 */
public class DefaultVaadinOptionSource extends DefaultOptionSource implements VaadinOptionSource {

    public DefaultVaadinOptionSource(Injector injector, Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders, Class<? extends Annotation> activeSource) {
        super(injector, optionDaoProviders, activeSource);
    }

    @Override
    public Container getContainer(Class<? extends Annotation> annotationClass) {
        checkAnnotationKey(annotationClass);
        Key<OptionContainerProvider> containerProviderKey = Key.get(OptionContainerProvider.class, annotationClass);
        OptionContainerProvider provider = injector.getInstance(containerProviderKey);
        return provider.get();
    }
}
