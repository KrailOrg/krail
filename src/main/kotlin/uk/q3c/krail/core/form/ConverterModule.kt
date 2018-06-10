package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder


/**
 *
 * Provides data related configuration where there is some dependency Vaadin, which prevents being provided by
 * an external utility
 *
 * Provides mappings of data converters as:
 *
 *    Set<ConverterSet>
 *
 * Note that MutableMap is used rather than Map, even though Guice MapBinder provides an immutable result - this is
 * because of a Guice 'feature'
 *
 * Created by David Sowerby on 11 Oct 2017
 */
open class ConverterModule : AbstractModule() {

    private lateinit var dataConverters: Multibinder<ConverterSet>

    override fun configure() {
        dataConverters = Multibinder.newSetBinder(binder(), ConverterSet::class.java)
        define()
        bindConverterFactory()
    }

    protected fun define() {
        dataConverters.addBinding().to(BaseConverterSet::class.java)
    }

    /**
     * Provides a factory for converting data types for display by Vaadin.  Override this method to provide your own implementation
     */
    protected fun bindConverterFactory() {
        bind(ConverterFactory::class.java).to(DefaultConverterFactory::class.java).asEagerSingleton()
    }
}
