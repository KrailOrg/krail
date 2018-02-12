package uk.q3c.krail.core.form

import com.google.inject.Inject
import org.vaadin.easybinder.data.AutoBinder
import org.vaadin.easybinder.data.BasicBinder
import org.vaadin.easybinder.data.ReflectionBinder
import uk.q3c.krail.core.validation.KrailValidationModule
import javax.validation.Validator

/**
 * Krail needs to use a [validator] that is aware of its I18N mechanism.  The [validator] itself is constructed in the [KrailValidationModule],
 * and then injected into one of the 3 binder types provided by [EasyBinder](https://github.com/ljessendk/easybinder)
 */
class EasyBinder @Inject constructor(val validator: Validator) {

    fun <BEAN> auto(clazz: Class<BEAN>): AutoBinder<BEAN> {
        return KrailAutoBinder<BEAN>(validator, clazz)
    }

    fun <BEAN> reflection(clazz: Class<BEAN>): ReflectionBinder<BEAN> {
        return KrailReflectionBinder<BEAN>(validator, clazz)
    }

    fun <BEAN> basic(clazz: Class<BEAN>): BasicBinder<BEAN> {
        return KrailBasicBinder<BEAN>(validator, clazz)
    }
}


class KrailAutoBinder<BEAN> internal constructor(validator: Validator, clazz: Class<BEAN>) : AutoBinder<BEAN>(clazz) {

    init {
        super.validator = validator
    }

}

class KrailReflectionBinder<BEAN> internal constructor(validator: Validator, clazz: Class<BEAN>) : ReflectionBinder<BEAN>(clazz) {

    init {
        super.validator = validator
    }

}

class KrailBasicBinder<BEAN> internal constructor(validator: Validator, clazz: Class<BEAN>) : AutoBinder<BEAN>(clazz) {

    init {
        super.validator = validator
    }

}





