package uk.q3c.krail.core.option.beans

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.data.converter.StringToIntegerConverter
import com.vaadin.ui.ColorPicker
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.TextField
import net.jodah.typetools.TypeResolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.core.form.ConverterFactory
import uk.q3c.krail.core.ui.DataTypeToUI
import uk.q3c.krail.core.vaadin.DefaultOptionBinder
import uk.q3c.krail.core.view.component.optionKeyFlagSize
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.option.Option
import uk.q3c.krail.util.ResourceUtils
import java.util.*

/**
 * Created by David Sowerby on 12 Oct 2017
 */
class DefaultOptionBinderTest {

    lateinit var optionBinder: DefaultOptionBinder
    val dataTypeToUI: DataTypeToUI = mock()
    val converterFactory: ConverterFactory = mock()
    val currentLocale: CurrentLocale = mock()
    var option: Option = mock()
    val resourceUtils: ResourceUtils = mock()

    @Before
    fun setUp() {
        optionBinder = DefaultOptionBinder(option, converterFactory)
    }


    @Test
    fun textFieldValueSet() {
        // given
        var key1 = optionKeyFlagSize
        val textField = TextField()
        whenever(dataTypeToUI.componentFor(3)).thenReturn(textField)
        whenever(currentLocale.locale).thenReturn(Locale.GERMANY)
        whenever(option.get(key1)).thenReturn(7)
        whenever(converterFactory.get(String::class.java, Integer::class.java)).thenReturn(StringToIntegerConverter("X") as Converter<String, Integer>)

        // when initial binding
        optionBinder.bindOption(key1, textField)

        // then value is transferred to field
        assertThat(textField.value).isEqualTo("7")

        // when field value is changed
        textField.value = "88"
        textField.value = "99"


        // then change listener is fired, updating option value
        verify(option).set(key1, 88)
        verify(option).set(key1, 99)
    }


    @Test
    fun typing() {
        println(TypeResolver.resolveRawArgument(HasValue::class.java, TextField::class.java))
        println(TypeResolver.resolveRawArgument(HasValue::class.java, ColorPicker::class.java))
        println(TypeResolver.resolveRawArgument(HasValue::class.java, DateTimeField::class.java))
    }

}