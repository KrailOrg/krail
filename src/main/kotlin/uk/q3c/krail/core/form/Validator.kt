package uk.q3c.krail.core.form

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import uk.q3c.krail.core.validation.ValidationKey
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

/**
 * Created by David Sowerby on 19 Jun 2018
 */
class MustBeTrue : BaseValidator<Boolean>(ValidationKey.AssertTrue) {
    override fun doApply(value: Boolean, context: ValueContext): ValidationResult {
        return toResult(value, value)
    }
}

class MustBeFalse : BaseValidator<Boolean>(ValidationKey.AssertFalse) {
    override fun doApply(value: Boolean, context: ValueContext): ValidationResult {
        return toResult(value, !value)
    }
}

class MaxInt(val max: Int) : BaseValidator<Int>(ValidationKey.Max) {
    override fun doApply(value: Int, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinLong(val min: Long) : BaseValidator<Long>(ValidationKey.Min) {
    override fun doApply(value: Long, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class MaxLong(val max: Long) : BaseValidator<Long>(ValidationKey.Max) {
    override fun doApply(value: Long, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinInt(val min: Int) : BaseValidator<Int>(ValidationKey.Min) {
    override fun doApply(value: Int, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class MaxShort(val max: Short) : BaseValidator<Short>(ValidationKey.Max) {
    override fun doApply(value: Short, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinShort(val min: Short) : BaseValidator<Short>(ValidationKey.Min) {
    override fun doApply(value: Short, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class MaxByte(val max: Byte) : BaseValidator<Byte>(ValidationKey.Max) {
    override fun doApply(value: Byte, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinByte(val min: Byte) : BaseValidator<Byte>(ValidationKey.Min) {
    override fun doApply(value: Byte, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class MaxBigInteger(val max: BigInteger) : BaseValidator<BigInteger>(ValidationKey.Max) {
    override fun doApply(value: BigInteger, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinBigInteger(val min: BigInteger) : BaseValidator<BigInteger>(ValidationKey.Min) {
    override fun doApply(value: BigInteger, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class MaxBigDecimal(val max: BigDecimal) : BaseValidator<BigDecimal>(ValidationKey.DecimalMax) {
    override fun doApply(value: BigDecimal, context: ValueContext): ValidationResult {
        return toResult(value, value <= max)
    }
}

class MinBigDecimal(val min: BigDecimal) : BaseValidator<BigDecimal>(ValidationKey.DecimalMin) {
    override fun doApply(value: BigDecimal, context: ValueContext): ValidationResult {
        return toResult(value, value >= min)
    }
}

class StringSize(val min: Int = 0, val max: Int = Int.MAX_VALUE) : BaseValidator<String>(ValidationKey.Size) {
    override fun doApply(value: String, context: ValueContext): ValidationResult {
        val r = toResult(value, value.length >= min)
        if (r.errorLevel == null) {
            return toResult(value, value.length <= max)
        }
        return r
    }
}


class CollectionSize(val min: Int = 0, val max: Int = Int.MAX_VALUE) : BaseValidator<Collection<*>>(ValidationKey.Size) {
    override fun doApply(value: Collection<*>, context: ValueContext): ValidationResult {
        val r = toResult(value, value.size >= min)
        if (r.errorLevel == null) {
            return toResult(value, value.size <= max)
        }
        return r
    }
}

class MustNotBeNull : BaseValidator<Any>(ValidationKey.NotNull) {
    override fun doApply(value: Any, context: ValueContext): ValidationResult {
        throw InvalidValueForValidator("Should only be called with nullable value")
    }

    override fun apply(value: Any?, context: ValueContext): ValidationResult {
        return toResult(value, value != null)
    }
}


class MustBeNull : BaseValidator<Any>(ValidationKey.Null) {
    override fun doApply(value: Any, context: ValueContext): ValidationResult {
        throw InvalidValueForValidator("Should only be called with nullable value")
    }
    override fun apply(value: Any?, context: ValueContext): ValidationResult {
        return toResult(value, value == null)
    }
}

class MustMatch(val pattern: String, vararg flags: javax.validation.constraints.Pattern.Flag) : BaseValidator<Any>(ValidationKey.Pattern) {
    val p: java.util.regex.Pattern

    init {
        var intFlag = 0
        for (flag in flags) {
            intFlag = intFlag or flag.value
        }
        p = java.util.regex.Pattern.compile(pattern, intFlag)
    }

    override fun doApply(value: Any, context: ValueContext): ValidationResult {
        return toResult(value, value == null)
    }
}

class PastLocalDateTime(val dateTime: LocalDateTime) : BaseValidator<LocalDateTime>(ValidationKey.Past) {
    override fun doApply(value: LocalDateTime, context: ValueContext): ValidationResult {
        return toResult(value, value.isBefore(dateTime))
    }
}

class PastLocalDate(val Date: LocalDate) : BaseValidator<LocalDate>(ValidationKey.Past) {
    override fun doApply(value: LocalDate, context: ValueContext): ValidationResult {
        return toResult(value, value.isBefore(Date))
    }
}

class PastOffsetDateTime(val dateTime: OffsetDateTime) : BaseValidator<OffsetDateTime>(ValidationKey.Past) {
    override fun doApply(value: OffsetDateTime, context: ValueContext): ValidationResult {
        return toResult(value, value.isBefore(dateTime))
    }
}

class PastDate(val dateTime: Date) : BaseValidator<Date>(ValidationKey.Past) {
    override fun doApply(value: Date, context: ValueContext): ValidationResult {
        return toResult(value, value.time < dateTime.time)
    }
}


class FutureLocalDateTime(val dateTime: LocalDateTime) : BaseValidator<LocalDateTime>(ValidationKey.Future) {
    override fun doApply(value: LocalDateTime, context: ValueContext): ValidationResult {
        return toResult(value, dateTime.isBefore(value))
    }
}

class FutureOffsetDateTime(val dateTime: OffsetDateTime) : BaseValidator<OffsetDateTime>(ValidationKey.Future) {
    override fun doApply(value: OffsetDateTime, context: ValueContext): ValidationResult {
        return toResult(value, dateTime.isBefore(value))
    }
}

class FutureLocalDate(val date: LocalDate) : BaseValidator<LocalDate>(ValidationKey.Future) {
    override fun doApply(value: LocalDate, context: ValueContext): ValidationResult {
        return toResult(value, date.isBefore(value))
    }
}

class FutureDate(val dateTime: Date) : BaseValidator<Date>(ValidationKey.Future) {
    override fun doApply(value: Date, context: ValueContext): ValidationResult {
        return toResult(value, value.time > dateTime.time)
    }
}







interface KrailValidator<T> : Validator<T> {
    var translate: Translate
}

abstract class BaseValidator<T>(val messageKey: I18NKey) : KrailValidator<T> {
    override lateinit var translate: Translate


    override fun apply(value: T?, context: ValueContext): ValidationResult {
        if (value == null) {
            return toResult(null, true)
        } else {
            return doApply(value, context)
        }
    }

    abstract fun doApply(value: T, context: ValueContext): ValidationResult


    protected fun getMessage(value: T): String {
        return translate.from(messageKey, value)
    }

    protected fun getMessage(value: String): String {
        return translate.from(messageKey, value)
    }

    protected fun toResult(value: T?, isValid: Boolean): ValidationResult {
        return if (isValid)
            ValidationResult.ok()
        else
            if (value == null) {
                ValidationResult.error(getMessage("null"))
            } else {
                ValidationResult.error(getMessage(value))
            }

    }
}