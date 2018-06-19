package uk.q3c.krail.core.form

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import uk.q3c.krail.core.validation.ValidationKey
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate

/**
 * Created by David Sowerby on 19 Jun 2018
 */
class AssertTrueValidator : BaseValidator<Boolean>(ValidationKey.AssertTrue) {
    override fun apply(value: Boolean, context: ValueContext): ValidationResult {
        return toResult(value, value)
    }
}

class AssertFalseValidator : BaseValidator<Boolean>(ValidationKey.AssertFalse) {
    override fun apply(value: Boolean, context: ValueContext): ValidationResult {
        return toResult(value, !value)
    }
}


interface KrailValidator<T> : Validator<T> {
    var translate: Translate
}

abstract class BaseValidator<T>(val messageKey: I18NKey) : KrailValidator<T> {
    override lateinit var translate: Translate

    protected fun getMessage(value: T): String {
        return translate.from(messageKey, value)
    }

    protected fun toResult(value: T, isValid: Boolean): ValidationResult {
        return if (isValid)
            ValidationResult.ok()
        else
            ValidationResult.error(getMessage(value))
    }
}