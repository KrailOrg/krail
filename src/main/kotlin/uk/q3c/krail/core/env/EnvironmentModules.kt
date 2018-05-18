package uk.q3c.krail.core.env

import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory
import uk.q3c.util.guice.InjectorLocator

/**
 * Created by David Sowerby on 23 Mar 2018
 */
class ServletEnvironmentModule : AbstractModule() {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun configure() {
        log.debug("ServletEnvironmentModule configure()")
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
    }
}


class VertxEnvironmentModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(VertxInjectorLocator::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.VERTX)
    }
}


