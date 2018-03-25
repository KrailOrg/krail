package uk.q3c.krail.core.guice


import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.spi.InjectionPoint
import org.apache.commons.lang3.reflect.FieldUtils
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier


/**
 * Created by David Sowerby on 16 Mar 2018
 */
@FunctionalInterface
interface SerializationSupport : Serializable {
    var excludedFieldNames: List<String>
    fun injectTransientFields(target: Any)
    fun checkForNullTransients()
}


class DefaultSerializationSupport @Inject constructor(val injectorLocator: InjectorLocator) : SerializationSupport {
    private var log = LoggerFactory.getLogger(this.javaClass.name)
    private val candidateFieldKeys: MutableMap<Field, Key<*>> = mutableMapOf()
    private val unResolvedFieldKeys: MutableMap<Field, Key<*>> = mutableMapOf()
    private val constructorParameterKeys = mutableListOf<Key<*>>()
    override var excludedFieldNames: List<String> = listOf()

    private lateinit var target: Any

    override fun injectTransientFields(target: Any) {
        this.target = target
        val injector = injectorLocator.get()

        val candidateFields = collectCandidateFields()
        candidateFields.forEach({ f -> candidateFieldKeys[f] = createFieldKey(f) })
        val constructorInjectionPoint = InjectionPoint.forConstructorOf(target.javaClass)
        val constructorParams = (constructorInjectionPoint.member as Constructor<*>).parameterTypes
        for (i in 0 until constructorInjectionPoint.dependencies.size) {
            // ignore params which are Serializable - their associated fields will have been deserialised
            if (!Serializable::class.java.isAssignableFrom(constructorParams[i])) {
                constructorParameterKeys.add(constructorInjectionPoint.dependencies[i].key)
            }
        }


        for ((field, fieldKey) in candidateFieldKeys) {
            if (constructorParameterKeys.contains(fieldKey)) {
                field.isAccessible = true
                field.set(target, injector.getInstance(fieldKey))
                constructorParameterKeys.remove(fieldKey)
                log.debug("Injecting ${field.name} after deserialisation")
            } else {
                unResolvedFieldKeys[field] = fieldKey
                log.debug("${field.name} is not a Guice injected field, no injection after deserialisation")
            }
        }

    }

    private fun createFieldKey(field: Field): Key<*> {
        val genericType = field.genericType
        val fieldAnnotations = field.declaredAnnotations
        for (annotation in fieldAnnotations) {
            val annotationInterfaces = annotation.javaClass.annotatedInterfaces
            for (annInterface in annotationInterfaces) {
                val annotationType = annInterface.type
                if ((annotationType as Class<*>).isAnnotationPresent(BindingAnnotation::class.java)) {
                    log.debug("$annotationType is a binding annotation")
                    return Key.get(genericType, annotation)
                } else {
                    log.debug("$annotationType is not a binding annotation, and is therefore ignored")
                }
            }
        }
        log.debug("Field ${field.name} has no binding annotations")
        return Key.get(genericType)
    }


    /**
     * Execute this method at the end of readObject() to make sure there are no transient fields left with value still at null
     */
    override fun checkForNullTransients() {
        // we can't just use unresolvedFieldKeys, because the developer could set a transient to null after injectTransientFields was invoked
        val candidateFields = collectCandidateFields()
        if (candidateFields.isEmpty()) {
            // all transients have been populated
            log.debug("Deserialization complete, all fields resolved")
            if (unResolvedFieldKeys.isNotEmpty()) {
                // simply means that user code filled in a field
                log.debug("These fields were populated by user code: $unResolvedFieldKeys")
            }
            if (constructorParameterKeys.isNotEmpty()) {
                // somehow all the fields have been populated but not all the constructor params have been used. Warn the developer
                log.warn("All transient fields have been populated after deserialization, but these constructor parameters were not used: $constructorParameterKeys. This can occur if you have populated a Guice injected transient field without using SerializationSupport, or you have excluded a Guice injected transient field")
            }
        } else {
            // a field has been missed - throw exception, suggest exclusion
            val nullFieldNames = mutableListOf<String>()
            for (field in candidateFields) {
                nullFieldNames.add(field.name)
            }
            throw SerializationSupportException("One or more transient fields is still null after deserialization. If this is required state, add them to the 'exclusions' parameter to prevent this exception.  Null fields are: $nullFieldNames")
        }
    }

    /**
     * Selects fields that need checking for injection. These are transient fields, with a null value, which are not listed in exclusions
     */
    private fun collectCandidateFields(): List<Field> {
        val fields = FieldUtils.getAllFields(target.javaClass)
        // We only process transient fields that have null value - user code may have already set some values
        return fields.filter { f -> Modifier.isTransient(f.modifiers) }
                .filter { f ->
                    f.isAccessible = true
                    f.get(target) == null
                }.filter { f -> !excludedFieldNames.contains(f.name) }
    }


}


class SerializationSupportModule : AbstractModule() {
    override fun configure() {
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
    }

}

class SerializationSupportException(msg: String) : RuntimeException(msg)