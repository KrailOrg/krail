package uk.q3c.krail.core.option;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.vaadin.v7.data.Container;
import uk.q3c.krail.option.persist.ActiveOptionSourceDefault;
import uk.q3c.krail.option.persist.OptionContainerProvider;
import uk.q3c.krail.option.persist.OptionDaoProviders;
import uk.q3c.krail.option.persist.source.DefaultOptionSource;
import uk.q3c.krail.persist.PersistenceInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by David Sowerby on 17 Aug 2017
 */
public class DefaultVaadinOptionSource extends DefaultOptionSource implements VaadinOptionSource {

    @Inject
    public DefaultVaadinOptionSource(Injector injector, @OptionDaoProviders Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders, @ActiveOptionSourceDefault Class<? extends Annotation> activeSource) {
        super(injector, optionDaoProviders, activeSource);
    }

    @Override
    public Container getContainer(Class<? extends Annotation> annotationClass) {
        checkAnnotationKey(annotationClass);
        TypeLiteral<OptionContainerProvider<Container>> providerLiteral = new TypeLiteral<OptionContainerProvider<Container>>() {
        };
        Key<OptionContainerProvider<Container>> containerProviderKey = Key.get(providerLiteral, annotationClass);
        OptionContainerProvider<Container> provider = injector.getInstance(containerProviderKey);
        return provider.get();
    }
}
