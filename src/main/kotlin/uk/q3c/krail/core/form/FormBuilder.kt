package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
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
interface FormBuilder


open class FormModule : AbstractModule() {

    override fun configure() {
        val fieldLiteral = object : TypeLiteral<AbstractField<*>>() {}
        val dataClassLiteral = object : TypeLiteral<Class<*>>() {}
        val dataClassToFieldMap: MapBinder<Class<*>, AbstractField<*>> = MapBinder.newMapBinder(binder(), dataClassLiteral, fieldLiteral)
        bindDefaultDataClassMappings(dataClassToFieldMap)
        bindBeanValidatorFactory()
        bindFormSupport()
        bindErrorMessageProvider()
        bindForm()
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
