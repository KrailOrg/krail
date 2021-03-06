package uk.q3c.krail.core.vaadin

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.converter.StringToIntegerConverter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.form.ConverterFactory
import uk.q3c.krail.core.form.ConverterModule
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.persist.PatternDao
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.krail.option.Option
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 12 Oct 2017
 */
class DefaultConverterFactoryTest {

    lateinit var injector: Injector

    @Before
    fun setUp() {
        injector = Guice.createInjector(ConverterModule(), DefaultConverterFactoryTestModule())
    }

    @Test
    fun converterBindings() {
        // given
        val factory = injector.getInstance(ConverterFactory::class.java)

        // expect
        assertThat(factory.get(String::class, Int::class)).isInstanceOf(StringToIntegerConverter::class.java)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun invalidPairRequested() {
        // given
        val factory = injector.getInstance(ConverterFactory::class.java)

        // when
        factory.get(Option::class, PatternDao::class)
        // expect exception
    }
}

class DefaultConverterFactoryTestModule : AbstractModule() {
    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
    }
}