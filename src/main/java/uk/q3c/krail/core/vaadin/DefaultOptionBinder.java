package uk.q3c.krail.core.vaadin;

import com.google.inject.Inject;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractField;
import net.jodah.typetools.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.form.ConverterFactory;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;

/**
 * Created by David Sowerby on 14 Oct 2017
 */
public class DefaultOptionBinder implements OptionBinder {

    private static Logger log = LoggerFactory.getLogger(DefaultOptionBinder.class);

    private Option option;
    private ConverterFactory converterFactory;

    @Inject
    public DefaultOptionBinder(Option option, ConverterFactory converterFactory) {
        this.option = option;
        this.converterFactory = converterFactory;
    }

    @Override
    public <P, M> void bindOption(OptionKey<M> optionKey, AbstractField<P> field) {
        OptionValueProvider<M, M> optionGetter = new OptionValueProvider<>(option);
        OptionSetter<M, M> optionSetter = new OptionSetter<>(optionKey, option);
        optionGetter.setOptionKey(optionKey);
        Class<P> presentationClass = presentationClass(field);
        Class<M> modelClass = (Class<M>) optionKey.getDefaultValue().getClass();
        Binder<M> binder = new Binder<>();
//        Converter<P, M> converter = converterFactory.get(presentationClass, modelClass);
//        binder.forField(field).withConverter(converter).bind(optionGetter, optionSetter);
        M optionValue = option.get(optionKey);
        log.debug("option value is: ", optionValue);
        binder.setBean(optionValue); // goes to field directly

    }


    private <P> Class<P> presentationClass(AbstractField<P> field) {
        return (Class<P>) TypeResolver.resolveRawArgument(HasValue.class, field.getClass());
    }


}


