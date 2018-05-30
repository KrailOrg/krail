package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import com.vaadin.data.BeanPropertySet
import com.vaadin.data.BeanValidationBinder
import com.vaadin.data.Binder
import com.vaadin.data.HasValue
import com.vaadin.data.PropertyDefinition
import com.vaadin.data.RequiredFieldConfigurator
import com.vaadin.data.util.BeanUtil
import com.vaadin.data.validator.BeanValidator
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import javax.validation.Validator


/**
 * Created by David Sowerby on 24 May 2018
 */
class KrailBeanValidator constructor(
        val serializationSupport: SerializationSupport,
        @Transient private var javaxValidatorProvider: Provider<Validator>,
        type: Class<*>,
        propertyName: String)

    : BeanValidator(type, propertyName) {

    override fun getJavaxBeanValidator(): Validator {
        return javaxValidatorProvider.get()
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        val literal = object : TypeLiteral<Provider<Validator>>() {}
        val key = Key.get(literal)
        javaxValidatorProvider = serializationSupport.getInjector().getInstance(key)

    }
}


class FormModule : AbstractModule() {
    override fun configure() {
        bind(KrailBeanValidatorFactory::class.java).to(DefaultKrailBeanValidatorFactory::class.java)
    }

}


interface KrailBeanValidatorFactory : Serializable {
    fun create(type: Class<*>, propertyName: String): KrailBeanValidator
}

class DefaultKrailBeanValidatorFactory @Inject constructor(
        val serializationSupport: SerializationSupport,
        @field:Transient private val javaxValidatorProvider: Provider<Validator>)

    : KrailBeanValidatorFactory {

    override fun create(type: Class<*>, propertyName: String): KrailBeanValidator {
        return KrailBeanValidator(serializationSupport = serializationSupport, javaxValidatorProvider = javaxValidatorProvider, type = type, propertyName = propertyName)
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }
}

class KrailBeanValidationBinderFactory @Inject constructor(@field:Transient private val krailBeanValidatorFactory: KrailBeanValidatorFactory, val serializationSupport: SerializationSupport) : Serializable {

    @JvmOverloads
    fun <BEAN> create(beanClass: Class<BEAN>, requiredConfigurator: RequiredFieldConfigurator = RequiredFieldConfigurator.DEFAULT): KrailBeanValidationBinder<BEAN> {
        return KrailBeanValidationBinder(beanClass, krailBeanValidatorFactory, requiredConfigurator, serializationSupport)
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }
}

/**
 * Binds a Bean to existing Fields, using reflection of the provided bean type to resolve bean properties. It assumes that JSR-303 bean validation
 * implementation is present on the classpath.
 *
 * This is mostly a copy of the core Vaadin class [BeanValidationBinder], but that code could not be extended to integrate with Krail
 *
 * @param beanType the bean type to use
 * @param requiredConfigurator  Determines how the "required field" indicator is handled.  Used by [HasValue.setRequiredIndicatorVisible]
 *
 * @throws IllegalStateException if there is no JSR 303 implementation on the classpath
 */
class KrailBeanValidationBinder<BEAN>(
        private val beanType: Class<BEAN>,
        @field:Transient private var krailBeanValidatorFactory: KrailBeanValidatorFactory,
        private var requiredConfigurator: RequiredFieldConfigurator, val serializationSupport: SerializationSupport) : Binder<BEAN>(beanType) {

    init {
        if (!BeanUtil.checkBeanValidationAvailable()) {
            throw IllegalStateException("No JSR 303 implementation found on the classpath or could not be initialized. ")
        }
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        val injector = serializationSupport.getInjector()
        krailBeanValidatorFactory = injector.getInstance(KrailBeanValidatorFactory::class.java)
    }

    override fun configureBinding(binding: Binder.BindingBuilder<BEAN, *>, definition: PropertyDefinition<BEAN, *>): Binder.BindingBuilder<BEAN, *> {
        val actualBeanType = findBeanType(beanType, definition)
        val validator = krailBeanValidatorFactory.create(actualBeanType, definition.topLevelName)
        configureRequired(binding, definition, validator)
        return binding.withValidator(validator)
    }

    /**
     * Finds the bean type containing the property the given definition refers to.
     *
     * @param beanType
     * the root beanType
     *
     * @param definition
     * the definition for the property
     *
     * @return the bean type containing the given property
     */
    private fun findBeanType(beanType: Class<BEAN>, definition: PropertyDefinition<BEAN, *>): Class<*> {
        return if (definition is BeanPropertySet.NestedBeanPropertyDefinition<*, *>) {
            (definition as BeanPropertySet.NestedBeanPropertyDefinition<*, *>).parent.type
        } else {
            // Non nested properties must be defined in the main type
            beanType
        }
    }

    private fun configureRequired(binding: Binder.BindingBuilder<BEAN, *>, definition: PropertyDefinition<BEAN, *>, validator: KrailBeanValidator) {
        val propertyHolderType = definition.propertyHolderType
        val descriptor = validator.javaxBeanValidator.getConstraintsForClass(propertyHolderType)
        val propertyDescriptor = descriptor.getConstraintsForProperty(definition.topLevelName) ?: return
        if (propertyDescriptor.constraintDescriptors.stream().map { it.annotation }.anyMatch(requiredConfigurator)) {
            binding.field.isRequiredIndicatorVisible = true
        }
    }
}