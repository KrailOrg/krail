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
import com.vaadin.ui.Component
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.TextField
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport
import java.time.LocalDate
import java.time.LocalDateTime


/**
 * Created by David Sowerby on 10 Jun 2018
 */

interface Form : KrailView

class DefaultForm @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        @field:Transient val formBuilderProvider: Provider<FormBuilder>)

    : ViewBase(translate, serializationSupport), Form {


    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private var componentMap: Map<String, FormProperty> = mutableMapOf()

    override fun doBuild(busMessage: ViewChangeBusMessage) {
        doBuild()
    }

    override fun doBuild() {
        val viewConfigurationClass = navigationStateExt.node?.masterNode?.viewConfiguration
        val formConfiguration =
                if (FormConfiguration::class.java.isAssignableFrom(viewConfigurationClass)) {
                    try {
                        viewConfigurationClass?.newInstance() as FormConfiguration
                    } catch (e: Exception) {
                        log.error("Failed to set form configuration", e)
                        EmptyFormConfiguration()
                    }

                } else {
                    throw FormConfigurationException("Configuration for a Form must be of type FormConfiguration")
                }
        val formTypeBuilder = formBuilderProvider.get().selectFormTypeBuilder(formConfiguration)
        val componentSet = formTypeBuilder.build()
        rootComponent = componentSet.rootComponent
        componentMap = componentSet.componentMap
    }

}

data class FormComponentSet(val componentMap: Map<String, FormProperty>, val rootComponent: Component)

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
        bindPropertySpecCreator()
    }

    private fun bindPropertySpecCreator() {
        bind(PropertySpecCreator::class.java).to(DefaultPropertySpecCreator::class.java)
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
