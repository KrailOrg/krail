package uk.q3c.krail.core.vaadin

import com.google.inject.Inject
import com.vaadin.data.Binder
import com.vaadin.data.HasValue
import com.vaadin.ui.AbstractField
import net.jodah.typetools.TypeResolver
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.form.ConverterFactory
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.OptionKey
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 14 Oct 2017
 */
class DefaultOptionBinder @Inject
constructor(private val option: Option, private val converterFactory: ConverterFactory) : OptionBinder {


    override fun <P : Any, M : Any> bindOption(optionKey: OptionKey<M>, field: AbstractField<P>) {
        val optionGetter = OptionValueProvider<M, M>(option)
        val optionSetter = OptionSetter<M, M>(optionKey, option)
        optionGetter.optionKey = optionKey
        @Suppress("UNCHECKED_CAST")
        val presentationValueClass = TypeResolver.resolveRawArgument(HasValue::class.java, field.javaClass).kotlin as KClass<P>
        val defaultValue: M = optionKey.defaultValue
        val modelClass = defaultValue.javaClass.kotlin
        val binder = Binder<M>()
        val converter = converterFactory.get(presentationValueClass, modelClass)
        binder.forField(field).withConverter<M>(converter).bind(optionGetter, optionSetter)
        val optionValue = option.get(optionKey)
        log.debug("option value is: ", optionValue)
        binder.bean = optionValue // goes to field directly

    }


    companion object {

        private val log = LoggerFactory.getLogger(DefaultOptionBinder::class.java)
    }


}


