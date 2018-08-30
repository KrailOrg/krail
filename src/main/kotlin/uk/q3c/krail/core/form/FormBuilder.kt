package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate


/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormBuilderSelector {
    fun selectFormBuilder(configuration: FormConfiguration): FormBuilder
}

interface FormBuilder {
    var configuration: FormConfiguration
    fun build(form: Form, navigationStateExt: NavigationStateExt): FormSection
}


class DefaultFormBuilderSelector @Inject constructor(private val formTypeBuilders: MutableMap<String, Provider<FormBuilder>>) : FormBuilderSelector {
    override fun selectFormBuilder(configuration: FormConfiguration): FormBuilder {
        val provider = formTypeBuilders[configuration.formType]
        if (provider != null) {
            val builder = provider.get()
            builder.configuration = configuration
            return builder
        } else {
            throw FormConfigurationException("unrecognised FormBuilder '${configuration.formType}'")
        }
    }
}


/**
 * Created by David Sowerby on 12 Jul 2018
 */
class StandardFormBuilder @Inject constructor(
        @field:Transient private val binderFactory: KrailBeanValidationBinderFactory,
        private val propertySpecCreator: PropertyConfigurationCreator,
        private val formSupport: FormSupport,
        private val formDaoFactory: FormDaoFactory,
        private val currentLocale: CurrentLocale,
        private val editSaveCancelBuilder: EditSaveCancelBuilder,
        val translate: Translate,
        val userNotifier: UserNotifier) : FormBuilder {

    override lateinit var configuration: FormConfiguration

    override fun build(form: Form, navigationStateExt: NavigationStateExt): FormSection {

        val sectionConfiguration = configuration.section("standard")
        if (sectionConfiguration.entityClass == Any::class) {
            throw FormConfigurationException("entityClass must be specified")
        }
        val sectionBuilder = StandardFormSectionBuilder(entityClass = sectionConfiguration.entityClass.kotlin, binderFactory = binderFactory, propertySpecCreator = propertySpecCreator, formSupport = formSupport, configuration = sectionConfiguration, currentLocale = currentLocale, userNotifier = userNotifier)
        val pageParams = navigationStateExt.to.parameters
        if (pageParams.containsKey("id")) {
            return sectionBuilder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
        } else {
            return sectionBuilder.buildTable(form, formDaoFactory, translate)
        }

    }
}


