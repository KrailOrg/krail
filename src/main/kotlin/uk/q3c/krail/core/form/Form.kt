package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.MapBinder
import com.google.inject.multibindings.Multibinder
import com.vaadin.data.HasValue
import com.vaadin.event.selection.SelectionEvent
import com.vaadin.event.selection.SelectionListener
import com.vaadin.shared.ui.colorpicker.Color
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.CheckBox
import com.vaadin.ui.ColorPicker
import com.vaadin.ui.Component
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.Grid
import com.vaadin.ui.Layout
import com.vaadin.ui.TextField
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import javax.cache.Cache
import kotlin.reflect.KClass


/**
 * Created by David Sowerby on 10 Jun 2018
 */

interface Form : KrailView {
    fun localeChanged()

    /**
     * Change route to the item specified by [id].  [id] is usually from [Entity.id]
     */
    fun changeRoute(id: String)
}

class DefaultForm @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        val currentLocale: CurrentLocale,
        @field:Transient val formBuilderSelectorProvider: Provider<FormBuilderSelector>)

    : ViewBase(translate, serializationSupport), Form {


    override fun changeRoute(newRoute: String) {
        TODO()
    }


    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private lateinit var section: FormSection


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
        if (formConfiguration is EmptyFormConfiguration) {
            throw FormConfigurationException("An EmptyFormConfiguration is not valid to construct a Form")
        }
        formConfiguration.config()
        val formBuilder = formBuilderSelectorProvider.get().selectFormBuilder(formConfiguration)
        section = formBuilder.build(this, navigationStateExt)
        if (section is FormDetailSection<*>) {
            (section as FormDetailSection<*>).translate(translate, currentLocale)
        }
        rootComponent = section.rootComponent
    }


    override fun localeChanged() {
        if (section is FormDetailSection<*>) {
            (section as FormDetailSection<*>).translate(translate, currentLocale)
        } else {
            if (section is FormTableSection<*>) {
                doBuild() // grid columns and renderers hide Locale away so we have to reconstruct
            }
        }

    }

    override fun loadData(busMessage: AfterViewChangeBusMessage) {
        section.loadData(navigationStateExt.to.parameters)
    }
}


interface FormSection : Serializable {
    val rootComponent: Component
    fun loadData(parameters: Map<String, String>)
}

class FormDetailSection<BEAN : Any>(val propertyMap: Map<String, DetailPropertyInfo>, override val rootComponent: Layout, val binder: KrailBeanValidationBinder<BEAN>, val dao: FormDao<BEAN>) : FormSection {

    fun translate(translate: Translate, currentLocale: CurrentLocale) {
        propertyMap.forEach { k, v ->
            v.component.caption = translate.from(v.captionKey)
            // setDescription is not part of Component interface!
            if (v.component is AbstractComponent) {
                v.component.locale = currentLocale.locale
                v.component.description = translate.from(v.descriptionKey)
            }
        }
    }

    override fun loadData(parameters: Map<String, String>) {
        val bean = dao.get("id")
        binder.readBean(bean)
    }
}

class FormTableSection<BEAN : Any>(val form: Form, override val rootComponent: Grid<BEAN>, val dao: FormDao<BEAN>) : FormSection, SelectionListener<BEAN> {
    override fun selectionChange(event: SelectionEvent<BEAN>) {
        val selectedItem = event.firstSelectedItem
        if (selectedItem.isPresent) {
            val bean = selectedItem.get()
            if (bean is Entity) {
                form.changeRoute(bean.id)
            }
        }
    }

    override fun loadData(parameters: Map<String, String>) {
        rootComponent.setItems(dao.get())
    }
}

open class FormModule : AbstractModule() {

    override fun configure() {
        val fieldLiteral = object : TypeLiteral<HasValue<*>>() {}
        val modelClassLiteral = object : TypeLiteral<Class<*>>() {}
        val modelClassToUIFieldMap: MapBinder<Class<*>, HasValue<*>> = MapBinder.newMapBinder(binder(), modelClassLiteral, fieldLiteral)
        val stringLiteral = object : TypeLiteral<String>() {}
        val formTypeBuilderClassLiteral = object : TypeLiteral<FormBuilder>() {}
        val formTypeBuilderLookup: MapBinder<String, FormBuilder> = MapBinder.newMapBinder(binder(), stringLiteral, formTypeBuilderClassLiteral)
        val rendererBinder: Multibinder<RendererSet> = Multibinder.newSetBinder(binder(), RendererSet::class.java)
        bindFormBuilders(formTypeBuilderLookup)
        bindDefaultDataClassMappings(modelClassToUIFieldMap)
        bindBeanValidatorFactory()
        bindFormSupport()
        bindErrorMessageProvider()
        bindForm()
        bindFormBuilderSelector()
        bindPropertySpecCreator()
        bindRendererFactory()
        bindRendererSets(rendererBinder)
    }

    protected open fun bindRendererSets(rendererBinder: Multibinder<RendererSet>) {
        rendererBinder.addBinding().to(BaseRendererSet::class.java)
    }

    protected open fun bindRendererFactory() {
        bind(RendererFactory::class.java).to(DefaultRendererFactory::class.java)
    }

    protected open fun bindPropertySpecCreator() {
        bind(PropertyConfigurationCreator::class.java).to(DefaultPropertyConfigurationCreator::class.java)
    }

    protected open fun bindFormBuilders(formBuilderLookup: MapBinder<String, FormBuilder>) {
        formBuilderLookup.addBinding("standard").to(StandardFormBuilder::class.java)
    }

    protected open fun bindFormBuilderSelector() {
        bind(FormBuilderSelector::class.java).to(DefaultFormBuilderSelector::class.java)
    }

    protected open fun bindDefaultDataClassMappings(dataClassToFieldMap: MapBinder<Class<*>, HasValue<*>>) {
        dataClassToFieldMap.addBinding(String::class.java).to(TextField::class.java)
        dataClassToFieldMap.addBinding(Int::class.javaPrimitiveType).to(TextField::class.java)
        dataClassToFieldMap.addBinding(LocalDateTime::class.java).to(DateTimeField::class.java)
        dataClassToFieldMap.addBinding(LocalDate::class.java).to(DateField::class.java)
        dataClassToFieldMap.addBinding(Color::class.java).to(ColorPicker::class.java)
        dataClassToFieldMap.addBinding(Boolean::class.javaPrimitiveType).to(CheckBox::class.java)
    }


    protected open fun bindBeanValidatorFactory() {
        bind(KrailBeanValidatorFactory::class.java).to(DefaultKrailBeanValidatorFactory::class.java)
    }

    protected open fun bindFormSupport() {
        bind(FormSupport::class.java).to(DefaultFormSupport::class.java)
    }

    protected open fun bindErrorMessageProvider() {
        bind(KrailConverterErrorMessageProvider::class.java)
    }

    protected open fun bindForm() {
        bind(Form::class.java).to(DefaultForm::class.java)
    }


}

interface BaseDao<BEAN : Any> : Cache<String, BEAN>, Serializable {

    fun put(vararg beans: BEAN)
    fun get(): List<BEAN>

}


interface FormDaoFactory {
    fun <T : Any> getDao(entityClass: KClass<T>): FormDao<T>
}


class FormDao<T : Any>(baseDao: BaseDao<T>) : BaseDao<T> by baseDao, Serializable {
    fun applyFilter() {
        TODO()
    }
}


interface Entity {
    val id: String
}