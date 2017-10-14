package uk.q3c.krail.core.vaadin

import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.converter.StringToIntegerConverter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.i18n.persist.PatternDao
import uk.q3c.krail.option.Option

/**
 * Created by David Sowerby on 12 Oct 2017
 */
class DefaultConverterFactoryTest {

    lateinit var injector: Injector

    @Before
    fun setUp() {
        injector = Guice.createInjector(DataModule())
    }

    @Test
    fun converterBindings() {
        // given
        val factory = injector.getInstance(ConverterFactory::class.java)

        // expect
        assertThat(factory.get(String::class.java, Integer::class.java)).isInstanceOf(StringToIntegerConverter::class.java)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun invalidPairRequested() {
        // given
        val factory = injector.getInstance(ConverterFactory::class.java)

        // when
        factory.get(Option::class.java, PatternDao::class.java)
        // expect exception
    }
}