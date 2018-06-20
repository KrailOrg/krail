package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 20 Jun 2018
 */
/**
 * If [SectionConfiguration] is not being scanned automatically, [PropertyConfiguration] must be fully populated manually.
 *
 * When [SectionConfiguration] is being scanned automatically, any manually specified values take precedence, (thus overriding the defaults) but otherwise:
 *
 * - [propertyType] is taken from the property declaration in the entity class
 * - [componentClass] is selected using [FormSupport.componentFor]
 * - [converterClass] is selected using [FormSupport.converterFor]
 * - [validations] are additive - that is, any manually defined [ValidatorSpec]s are combined with those read from JSR 303 annotations from the entity class.
 *
 * When setting validation, http://piotrnowicki.com/2011/02/float-and-double-in-java-inaccurate-result/
 *
 * [caption] and [description] must be set manually
 */
@FormDsl
class PropertyConfiguration(val name: String) : Serializable {
    var propertyType: KClass<out Any> = Any::class
    var componentClass: Class<out AbstractField<*>> = AbstractField::class.java
    var converterClass: Class<out Converter<*, *>> = Converter::class.java
    var caption: I18NKey = LabelKey.Unnamed
    var description: I18NKey = DescriptionKey.No_description_provided
    var validations: MutableList<KrailValidator<*>> = mutableListOf()


    fun max(max: Short) {
        validations.add(MaxShort(max))
    }

    fun max(max: Byte) {
        validations.add(MaxByte(max))
    }

    fun min(min: Short) {
        validations.add(MinShort(min))
    }

    fun min(min: Byte) {
        validations.add(MinByte(min))
    }

    fun max(max: Int) {
        validations.add(MaxInt(max))
    }

    fun min(min: Int) {
        validations.add(MinInt(min))
    }

    fun max(max: Long) {
        validations.add(MaxLong(max))
    }

    fun min(min: Long) {
        validations.add(MinLong(min))
    }


    fun assertTrue() {
        validations.add(MustBeTrue())
    }

    fun assertFalse() {
        validations.add(MustBeFalse())
    }


    fun null_() {
        validations.add(MustBeNull())
    }

    fun notNull() {
        validations.add(MustNotBeNull())
    }

    fun pattern(pattern: String, vararg flags: javax.validation.constraints.Pattern.Flag) {
        validations.add(MustMatch(pattern, *flags))
    }

    fun past(dateTime: LocalDateTime) {
        validations.add(PastLocalDateTime(dateTime))
    }

    fun past(dateTime: OffsetDateTime) {
        validations.add(PastOffsetDateTime(dateTime))
    }

    fun past(date: LocalDate) {
        validations.add(PastLocalDate(date))
    }

    fun future(date: Date) {
        validations.add(FutureDate(date))
    }

    fun future(localDate: LocalDate) {
        validations.add(FutureLocalDate(localDate))
    }

    fun future(offsetDateTime: OffsetDateTime) {
        validations.add(FutureOffsetDateTime(offsetDateTime))
    }

    fun future(localDateTime: LocalDateTime) {
        validations.add(FutureLocalDateTime(localDateTime))
    }

    /**
     * BigDecimal
    BigInteger
    String
    byte, short, int, long, and their respective wrappers
     */
    fun decimalMax(max: BigDecimal) {
        validations.add(MaxBigDecimal(max))
    }

    fun decimalMax(max: BigInteger) {
        validations.add(MaxBigInteger(max))
    }

    fun decimalMax(max: String) {
        val bd: BigDecimal = max.toBigDecimal()
        validations.add(MaxBigDecimal(bd))
    }

    fun decimalMax(max: Int) {
        max(max)
    }

    fun decimalMax(max: Long) {
        max(max)
    }

    fun decimalMax(max: Short) {
        max(max)
    }

    fun decimalMax(max: Byte) {
        max(max)
    }

    ////////////////////////////////////

    fun decimalMin(min: BigDecimal) {
        validations.add(MinBigDecimal(min))
    }

    fun decimalMin(min: BigInteger) {
        validations.add(MinBigInteger(min))
    }

    fun decimalMin(min: String) {
        val bd: BigDecimal = min.toBigDecimal()
        validations.add(MinBigDecimal(bd))
    }

    fun decimalMin(min: Int) {
        min(min)
    }

    fun decimalMin(min: Long) {
        min(min)
    }

    fun decimalMin(min: Short) {
        min(min)
    }

    fun decimalMin(min: Byte) {
        min(min)
    }


    fun size(min: Int = 0, max: Int = Int.MAX_VALUE) {
        if (propertyType == String::class) {
            validations.add(StringSize(min = min, max = max))
        } else {
            if (Collection::class.java.isAssignableFrom(propertyType.java)) {
                validations.add(CollectionSize(min = min, max = max))
            }
        }
        throw InvalidTypeForValidator(propertyType, "size")
    }


    fun mustBeNull() {
        null_()
    }

    fun mustNotBeNull() {
        notNull()
    }


    fun mustBeTrue() {
        assertTrue()
    }

    fun mustBeFalse() {
        assertFalse()
    }

    fun mustMatch(pattern: String, vararg flags: javax.validation.constraints.Pattern.Flag) {
        pattern(pattern, *flags)
    }
}

class InvalidTypeForValidator(targetClass: KClass<*>, validatorType: String) : RuntimeException("$targetClass is an invalid type for a $validatorType")