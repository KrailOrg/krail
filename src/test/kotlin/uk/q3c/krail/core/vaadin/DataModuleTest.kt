package uk.q3c.krail.core.vaadin

import com.google.inject.Guice
import com.google.inject.Injector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by David Sowerby on 12 Oct 2017
 */
class DataModuleTest {

    lateinit var injector: Injector

    @Before
    fun setUp() {
        injector = Guice.createInjector(DataModule())
    }

    @Test
    fun interfaceBindings() {

        // when
        val factory: ConverterFactory? = injector.getInstance(ConverterFactory::class.java)

        // then
        assertThat(factory).isInstanceOf(DefaultConverterFactory::class.java)
    }


}