package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.MapBinder
import com.vaadin.shared.ui.colorpicker.Color
import com.vaadin.ui.AbstractField
import com.vaadin.ui.CheckBox
import com.vaadin.ui.ColorPicker
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.TextField
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormBuilder {
    fun selectFormTypeBuilder(configuration: FormConfiguration): FormTypeBuilder
}

interface FormTypeBuilder {
    var configuration: FormConfiguration
    fun build(): FormComponentSet
}

class SimpleFormTypeBuilder @Inject constructor() : FormTypeBuilder {
    override lateinit var configuration: FormConfiguration
    override fun build(): FormComponentSet {
        TODO()
    }
}


class DefaultFormBuilder @Inject constructor(private val formTypeBuilders: MutableMap<String, Provider<FormTypeBuilder>>) : FormBuilder {
    override fun selectFormTypeBuilder(configuration: FormConfiguration): FormTypeBuilder {
        val typeName = configuration.formType
        val adjustedTypeName =
                when (typeName) {
                    "unnamed", "simple", "" -> "simple"
                    else -> {
                        typeName
                    }
                }

        val provider = formTypeBuilders[adjustedTypeName]
        if (provider != null) {
            val builder = provider.get()
            builder.configuration = configuration
            return builder
        } else {
            throw FormConfigurationException("unrecognised FormTypeBuilder '$typeName'")
        }
    }
}


open class FormModule : AbstractModule() {

    override fun configure() {
        val fieldLiteral = object : TypeLiteral<AbstractField<*>>() {}
        val dataClassLiteral = object : TypeLiteral<Class<*>>() {}
        val dataClassToFieldMap: MapBinder<Class<*>, AbstractField<*>> = MapBinder.newMapBinder(binder(), dataClassLiteral, fieldLiteral)
        val stringLiteral = object : TypeLiteral<String>() {}
        val formTypeBuilderClassLiteral = object : TypeLiteral<FormTypeBuilder>() {}
        val formTypeBuilderLookup: MapBinder<String, FormTypeBuilder> = MapBinder.newMapBinder(binder(), stringLiteral, formTypeBuilderClassLiteral)
        bindFormTypeBuilders(formTypeBuilderLookup)
        bindDefaultDataClassMappings(dataClassToFieldMap)
        bindBeanValidatorFactory()
        bindFormSupport()
        bindErrorMessageProvider()
        bindForm()
        bindFormBuilder()
    }

    protected fun bindFormTypeBuilders(formTypeBuilderLookup: MapBinder<String, FormTypeBuilder>) {
        formTypeBuilderLookup.addBinding("simple").to(SimpleFormTypeBuilder::class.java)
    }

    protected fun bindFormBuilder() {
        bind(FormBuilder::class.java).to(DefaultFormBuilder::class.java)
    }

    protected fun bindDefaultDataClassMappings(dataClassToFieldMap: MapBinder<Class<*>, AbstractField<*>>) {
        dataClassToFieldMap.addBinding(String::class.java).to(TextField::class.java)
        dataClassToFieldMap.addBinding(Integer::class.java).to(TextField::class.java)
        dataClassToFieldMap.addBinding(LocalDateTime::class.java).to(DateTimeField::class.java)
        dataClassToFieldMap.addBinding(LocalDate::class.java).to(DateField::class.java)
        dataClassToFieldMap.addBinding(Color::class.java).to(ColorPicker::class.java)
        dataClassToFieldMap.addBinding(Boolean::class.java).to(CheckBox::class.java)
    }


    protected fun bindBeanValidatorFactory() {
        bind(KrailBeanValidatorFactory::class.java).to(DefaultKrailBeanValidatorFactory::class.java)
    }

    protected fun bindFormSupport() {
        bind(FormSupport::class.java).to(DefaultFormSupport::class.java)
    }

    protected fun bindErrorMessageProvider() {
        bind(KrailConverterErrorMessageProvider::class.java)
    }

    protected fun bindForm() {
        bind(Form::class.java).to(DefaultForm::class.java)
    }

}
