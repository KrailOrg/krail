package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.MapBinder
import com.vaadin.data.HasValue
import com.vaadin.shared.ui.colorpicker.Color
import com.vaadin.ui.CheckBox
import com.vaadin.ui.ColorPicker
import com.vaadin.ui.Component
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.TextField
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass


/**
 * Created by David Sowerby on 10 Jun 2018
 */

interface Form : KrailView {
    fun translate()

    /**
     * Change route to the item specified by [id].  [id] is usually from [Entity.id]
     */
    fun changeRoute(id: String)

}

class DefaultForm @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        val navigator: Navigator,
        val uriFragmentHandler: StrictURIFragmentHandler,
        val currentLocale: CurrentLocale,
        @field:Transient val formBuilderSelectorProvider: Provider<FormBuilderSelector>)

    : ViewBase(translate, serializationSupport), Form {


    override fun changeRoute(id: String) {
        val newState = NavigationState()
        newState.fragment(navigationStateExt.to.fragment)
        newState.update(uriFragmentHandler)
        newState.parameter("id", id)
        newState.update(uriFragmentHandler)
        navigator.navigateTo(newState)
    }


    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private lateinit var section: FormSection


    override fun doBuild() {
        val viewConfigurationClass = navigationStateExt.node.masterNode.viewConfiguration
        val formConfiguration =
                if (FormConfiguration::class.java.isAssignableFrom(viewConfigurationClass)) {
                    try {
                        viewConfigurationClass.newInstance() as FormConfiguration
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
        section.mode = EditMode.READ_ONLY
        translate()
        rootComponent = section.rootComponent

    }


    override fun translate() {
        section.translate(translate, currentLocale)
    }

    /**
     * Loads data but only for the detail section.  Data for table is 'loaded' (with the Dao as a CallbackProvider) during construction
     * of the [FormTableSection]
     */
    override fun loadData() {
        if (section is FormDetailSection<*>) {
            (section as FormDetailSection<*>).loadData(navigationStateExt.to.parameters)
        }
    }
}


interface FormSection : Serializable {
    var mode: EditMode
    val rootComponent: Component
    fun translate(translate: Translate, currentLocale: CurrentLocale)
}


class MissingParameterException(val parameter: String) : RuntimeException("There is no parameter '$parameter'")


interface BaseDao<BEAN : Any> : Serializable {

    /**
     * Inserts new [beans]
     */
    fun insert(vararg beans: BEAN)

    /**
     * Returns all the entities of type BEAN
     */
    fun get(): List<BEAN>

    /**
     * Gets the first item which has an 'id' property matching [key]
     *
     * @throws NoSuchElementException if not found
     */
    fun get(key: String): BEAN


    /**
     * Updates an existing element or inserts a new one if one does not exist
     */
    fun update(element: BEAN)

    /**
     * Identical to [update]
     */
    fun put(element: BEAN)

    /**
     * Close the underlying database connection
     */
    fun close()


    fun isClosed(): Boolean
}


interface FormDaoFactory : Serializable {
    fun <T : Entity> getDao(entityClass: KClass<T>): FormDao<T>
}


class FormDao<T : Any>(baseDao: BaseDao<T>) : BaseDao<T> by baseDao, Serializable {
    fun applyFilter() {
        TODO()
    }
}


interface Entity {
    val id: String
}

open class FormModule : AbstractModule() {

    override fun configure() {
        val fieldLiteral = object : TypeLiteral<HasValue<*>>() {}
        val modelClassLiteral = object : TypeLiteral<Class<*>>() {}
        val modelClassToUIFieldMap: MapBinder<Class<*>, HasValue<*>> = MapBinder.newMapBinder(binder(), modelClassLiteral, fieldLiteral)
        val stringLiteral = object : TypeLiteral<String>() {}
        val formTypeBuilderClassLiteral = object : TypeLiteral<FormBuilder>() {}
        val formTypeBuilderLookup: MapBinder<String, FormBuilder> = MapBinder.newMapBinder(binder(), stringLiteral, formTypeBuilderClassLiteral)
        bindFormBuilders(formTypeBuilderLookup)
        bindDefaultDataClassMappings(modelClassToUIFieldMap)
        bindBeanValidatorFactory()
        bindFormSupport()
        bindErrorMessageProvider()
        bindForm()
        bindFormBuilderSelector()
        bindPropertySpecCreator()
        bindEditSaveCancel()
        bindEditSaveCancelBuilder()
    }


    protected open fun bindEditSaveCancel() {
        bind(EditSaveCancel::class.java).to(DefaultEditSaveCancel::class.java)
    }

    protected open fun bindEditSaveCancelBuilder() {
        bind(EditSaveCancelBuilder::class.java).to(DefaultEditSaveCancelBuilder::class.java)
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
        dataClassToFieldMap.addBinding(Int::class.java).to(TextField::class.java)
        dataClassToFieldMap.addBinding(Integer::class.java).to(TextField::class.java)
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
