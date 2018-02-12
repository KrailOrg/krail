package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.apache.bval.jsr303.ApacheValidatorFactory
import uk.q3c.krail.core.validation.KrailInterpolator
import javax.validation.Validation
import javax.validation.Validator

/**
 * Created by David Sowerby on 11 Feb 2018
 */
class FormsModule : AbstractModule() {

    override fun configure() {
    }

    @Provides
    fun validatorProvider(interpolator: KrailInterpolator): Validator {
        val validatorFactory = Validation.buildDefaultValidatorFactory() as ApacheValidatorFactory
        validatorFactory.messageInterpolator = interpolator
        return validatorFactory.validator
    }
}

