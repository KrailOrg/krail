package uk.q3c.krail.core.guice

import com.google.inject.AbstractModule
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.apache.shiro.SecurityUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.BootstrapConfig
import uk.q3c.krail.core.env.BootstrapLoader
import uk.q3c.krail.core.env.EnvironmentConfig
import uk.q3c.krail.core.env.InjectorFactory
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.shiro.KrailSecurityManager
import uk.q3c.util.guice.InjectorLocator

/**
 * Created by David Sowerby on 19 Mar 2018
 */
object InjectorFactoryTest : Spek({


    given("an InjectorFactory") {
        val injectorFactory = InjectorFactory()

        on("creating the injector for the Servlet environment") {
            injectorFactory.createInjector(RuntimeEnvironment.SERVLET, TestBootstrapModule())

            it("creates and sets the SecurityManager") {
                SecurityUtils.getSecurityManager().shouldBeInstanceOf(KrailSecurityManager::class.java)
            }

            it("has used the Servlet configuration") {
                InjectorHolder.getInjector().shouldNotBeNull()
            }
        }

    }
})

class TestBootstrapModule : AbstractModule() {
    val mockBootstrapLoader: BootstrapLoader = mock()
    val collatorName = "uk.q3c.krail.core.guice.CoreBindingsCollator"


    override fun configure() {
        val bootstrapConfig = BootstrapConfig(collator = collatorName, servletConfig = EnvironmentConfig(), vertxConfig = EnvironmentConfig(), modules = listOf())
        whenever(mockBootstrapLoader.load()).thenReturn(bootstrapConfig)
        bind(BootstrapLoader::class.java).toInstance(mockBootstrapLoader)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
    }

}

