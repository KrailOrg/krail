package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.data.ErrorMessageProvider
import com.vaadin.data.Result
import com.vaadin.data.ValueContext
import com.vaadin.data.converter.StringToIntegerConverter
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * A factory to provide data converters which implement [Converter] - a Vaadin defined interface, so this code cannot become
 * an external utility
 *
 * Created by David Sowerby on 05 Oct 2017.
 */

interface ConverterSet : ConverterProvider {
    val errorMessageProviderProvider: Provider<KrailConverterErrorMessageProvider>
}

interface ConverterProvider : Serializable {
    /**
     * Returns a [Converter] to match [converterPair], or a [NoConversionConverter] if the pair is not supported
     */
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

class BaseConverterSet @Inject constructor(@field:Transient override val errorMessageProviderProvider: Provider<KrailConverterErrorMessageProvider>, val serializationSupport: SerializationSupport) : ConverterSet {

    override fun supports(converterPair: ConverterPair): Boolean {
        return true
    }

    override fun get(converterPair: ConverterPair): Converter<Any, Any> {

        val emp = errorMessageProviderProvider.get()
        val converter: Any = when (converterPair) {
            ConverterPair(String::class, Int::class) -> StringToIntegerConverter(emp.setMessage(ConverterKey.Must_be_a_number))
            else -> {
                throw UnsupportedOperationException("Conversion between $converterPair is not supported")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return converter as Converter<Any, Any>
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }
}

interface ConverterFactory : ConverterProvider {

    /**
     * Returns a [Converter] instance for the combination of [presentationClass] and [modelClass]
     *
     * @throws UnsupportedOperationException if no [Converter] has been defined
     */
    fun <P : Any, M : Any> get(presentationClass: KClass<out P>, modelClass: KClass<out M>): Converter<P, M>

}

/**
 * Provides a translated error message for failures during conversion using [Converter.apply]
 */
class KrailConverterErrorMessageProvider @Inject constructor(private val translate: Translate) : ErrorMessageProvider {
    private lateinit var messageKey: I18NKey

    fun setMessage(messageKey: I18NKey): KrailConverterErrorMessageProvider {
        this.messageKey = messageKey
        return this
    }

    override fun apply(context: ValueContext): String {
        if (context.component.isPresent && context.component.get().locale != null) {
            return translate.from(messageKey, context.locale)
        } else {
            return translate.from(messageKey)
        }
    }
}


data class ConverterPair(val presentation: KClass<out Any>, val model: KClass<out Any>) : Serializable

/**
 * Uses all configured instances of [ConverterSet] to find a suitable [Converter].  Additional [ConverterSet]s can be defined
 * by your own Guice module - see [ConverterModule] for an example of the SetBinder to use.  Once bound via Guice, this factory will use
 * all available [ConverterSet]s
 */
class DefaultConverterFactory @Inject constructor(private val converters: MutableSet<ConverterSet>) : ConverterFactory {

    override fun <P : Any, M : Any> get(presentationClass: KClass<out P>, modelClass: KClass<out M>): Converter<P, M> {
        @Suppress("UNCHECKED_CAST")
        return get(ConverterPair(presentationClass, modelClass)) as Converter<P, M>
    }

    override fun supports(converterPair: ConverterPair): Boolean {
        for (converterSetProvider in converters) {
//            if (converterSetProvider.get().supports(converterPair)) {
//                return true
//            }
            if (converterSetProvider.supports(converterPair)) {
                return true
            }
        }
        return false
    }

    override fun get(converterPair: ConverterPair): Converter<Any, Any> {
        if (converterPair.model == converterPair.presentation) {
            return NoConversionConverter()
        }
        for (converterSetProvider in converters) {
            val converterSet = converterSetProvider
//            val converterSet = converterSetProvider.get()
            if (converterSet.supports(converterPair)) {
                return converterSet.get(converterPair)
            }
        }
        throw IllegalArgumentException("Converter Pair not supported $converterPair")
    }


}

enum class ConverterKey : I18NKey {
    Must_be_a_number
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

