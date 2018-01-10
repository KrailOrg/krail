package uk.q3c.krail.core.vaadin

import com.google.inject.Inject
import com.vaadin.data.Converter
import com.vaadin.data.Result
import com.vaadin.data.ValueContext
import com.vaadin.data.converter.StringToIntegerConverter
import java.io.Serializable

/**
 * A factory to provide data converters which implement [Converter] - a Vaadin defined interface, so this code cannot become
 * an external utility
 *
 * Created by David Sowerby on 05 Oct 2017.
 */

interface ConverterSet {

    fun get(converterPair: ConverterPair): Converter<Any, Any>
    fun supports(converterPair: ConverterPair): Boolean
}

class NoConversionConverter : Converter<Any, Any> {
    override fun convertToModel(value: Any?, context: ValueContext?): Result<Any> {
        if (value != null)
            return Result.ok(value)

        throw NullPointerException("value for model cannot be null")
    }

    override fun convertToPresentation(value: Any?, context: ValueContext?): Any {
        if (value != null)
            return value

        throw NullPointerException("value for presentation cannot be null")
    }

}

class BaseConverterSet : ConverterSet {
    override fun supports(converterPair: ConverterPair): Boolean {
        return true
    }

    override fun get(converterPair: ConverterPair): Converter<Any, Any> {
        if (converterPair.model == converterPair.presentation) {
            return NoConversionConverter()
        }
        val converter: Any = when (converterPair) {
            ConverterPair(String::class.java, Integer::class.java) -> StringToIntegerConverter("")
            else -> {
                throw UnsupportedOperationException("Conversion between $converterPair is not supported")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return converter as Converter<Any, Any>
    }
}

interface ConverterFactory : ConverterSet {
    fun <P : Any, M : Any> get(presentationClass: Class<out P>, modelClass: Class<out M>): Converter<P, M>

}


data class ConverterPair(val presentation: Class<out Any>, val model: Class<out Any>) : Serializable


class DefaultConverterFactory @Inject constructor(val converters: MutableSet<ConverterSet>) : ConverterFactory {
    override fun <P : Any, M : Any> get(presentationClass: Class<out P>, modelClass: Class<out M>): Converter<P, M> {
        @Suppress("UNCHECKED_CAST")
        return get(ConverterPair(presentationClass, modelClass)) as Converter<P, M>
    }

    override fun supports(converterPair: ConverterPair): Boolean {
        for (converterSet in converters) {
            if (converterSet.supports(converterPair)) {
                return true
            }
        }
        return false
    }

    override fun get(converterPair: ConverterPair): Converter<Any, Any> {
        for (converterSet in converters) {
            if (converterSet.supports(converterPair)) {
                return converterSet.get(converterPair)
            }
        }
        throw UnsupportedOperationException("Pair not supported $converterPair")
    }


}

inline fun <reified L : Any, reified R : Any> isSubClassOf(): Boolean {
    return R::class.java.isAssignableFrom(L::class.java)
}

inline fun <reified L : Any, reified R : Any> isSuperClassOf(): Boolean {
    return L::class.java.isAssignableFrom(R::class.java)
}

//fun <PRESENTATION : Any, MODEL : Any> createStringConverter(modelType: Class<MODEL>): Converter<PRESENTATION, MODEL> {
//    return if (Double::class.java.isAssignableFrom(modelType)) {
//        StringToDoubleConverter("FAILED")
//    } else if (Float::class.java.isAssignableFrom(modelType)) {
//        StringToFloatConverter("FAILED")
//    } else if (Int::class.java.isAssignableFrom(modelType)) {
//        StringToIntegerConverter("FAILED")
//    } else if (Int::class.javaPrimitiveType!!.isAssignableFrom(modelType)) {
//        StringToIntegerConverter("FAILED")
//    } else if (Long::class.java.isAssignableFrom(modelType)) {
//        StringToLongConverter("FAILED")
//    } else if (BigDecimal::class.java.isAssignableFrom(modelType)) {
//        StringToBigDecimalConverter("FAILED")
//    } else if (Boolean::class.java.isAssignableFrom(modelType)) {
//        StringToBooleanConverter("FAILED")
//    } else if (Date::class.java.isAssignableFrom(modelType)) {
//        StringToDateConverter()
//    } else if (BigInteger::class.java.isAssignableFrom(modelType)) {
//        StringToBigIntegerConverter("FAILED")
//
//    } else {
//        throw UnsupportedOperationException("No Converter available to convert from $modelType to String")
//    }
//}

