package uk.q3c.krail.core.guice

import com.google.inject.AbstractModule

/**
 * Created by David Sowerby on 23 Mar 2018
 */
class ServletEnvironmentModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
    }
}


class VertxEnvironmentModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(VertxInjectorLocator::class.java)
    }
}


