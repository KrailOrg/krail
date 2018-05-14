package uk.q3c.krail.core.guice

import com.github.mcollovati.vertx.vaadin.VaadinVerticle
import com.github.mcollovati.vertx.vaadin.VertxVaadinService
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.SecurityManager
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import uk.q3c.krail.core.guice.RuntimeEnvironment.SERVLET
import uk.q3c.krail.core.guice.RuntimeEnvironment.VERTX
import uk.q3c.util.guice.InjectorLocator
import java.io.InputStream
import java.nio.file.Paths

/**
 * Created by David Sowerby on 18 Mar 2018
 */


class VertxInjectorLocator : InjectorLocator {
    private val injectorKey = "Injector"
    override fun put(injector: Injector) {
        Vertx.currentContext().put(injectorKey, injector)
    }


    override fun get(): Injector {

        val injector: Injector? = Vertx.currentContext().get(injectorKey)
        if (injector == null) {
            val injectorFactory = InjectorFactory()
            val newInjector = injectorFactory.createInjector(VERTX)
            put(newInjector)
            return newInjector
        } else {
            return injector
        }
    }

}

class ServletInjectorLocator : InjectorLocator {

    override fun put(injector: Injector) {
        InjectorHolder.setInjector(injector)
    }

    override fun get(): Injector {
        if (!InjectorHolder.hasInjector()) {
            InjectorHolder.setInjector(InjectorFactory().createInjector(SERVLET))
        }
        return InjectorHolder.getInjector()
    }
}


enum class RuntimeEnvironment { SERVLET, VERTX }

class InjectorFactory {
    private val log = LoggerFactory.getLogger(this.javaClass.name)


    /**
     * Selects the correct environment module propagates call to other [createInjector]
     */
    fun createInjector(runtimeEnvironment: RuntimeEnvironment): Injector {
        val bootstrapModule = if (runtimeEnvironment == SERVLET) {
            KrailServletBootstrapModule()
        } else {
            KrailVertxBootstrapModule()
        }
        return createInjector(runtimeEnvironment, bootstrapModule)
    }

    /**
     * Creates a bootstrap injector first, so that the location for the Injector can be established.
     *
     * Then creates the real injector and puts it in the correct place for the environment (determined by the
     * environment specific implementation of [InjectorLocator])
     */

    fun createInjector(runtimeEnvironment: RuntimeEnvironment, bootstrapModule: Module): Injector {
        val bootstrapInjector = Guice.createInjector(bootstrapModule)
        log.debug("bootstrap injector created")
        val bootstrapLoader = bootstrapInjector.getInstance(BootstrapLoader::class.java)
        val bootstrapConfig = bootstrapLoader.load()
        log.debug("bootstrap config loaded: $bootstrapConfig")
        val environmentConfig =
                when (runtimeEnvironment) {
                    SERVLET -> bootstrapConfig.servletConfig
                    VERTX -> bootstrapConfig.vertxConfig
                }
        log.debug("environment config is: $environmentConfig")
        val collatorClass = Class.forName(bootstrapConfig.collator)
        log.debug("collator class identified, $collatorClass")
        val collator = bootstrapInjector.getInstance(collatorClass) as BindingsCollator
        log.debug("collator instantiated")
        val additionalModuleNames = environmentConfig.additionalModules
        val additionalModules: MutableList<Module> = mutableListOf()
        for (moduleName in additionalModuleNames) {
            val moduleClass = Class.forName(moduleName)
            log.debug("instantiating additional module: $moduleName")
            val module = bootstrapInjector.getInstance(moduleClass) as Module
            additionalModules.add(module)
        }
        val allModules: MutableList<Module> = mutableListOf()
        allModules.addAll(collator.allModules())
        allModules.addAll(additionalModules)
        allModules.add(bootstrapModule) // we need the InjectorLocator binding
        log.debug("module list composed for real injector")
        val injectorLocator = bootstrapInjector.getInstance(InjectorLocator::class.java)
        log.debug("injectorLocator is a ${injectorLocator.javaClass}")
        val realInjector = Guice.createInjector(allModules)
        log.debug("application injector created")
        val securityManager: SecurityManager = realInjector.getInstance(SecurityManager::class.java)
        SecurityUtils.setSecurityManager(securityManager)
        log.debug("Security manager set")
        injectorLocator.put(realInjector)
        return realInjector
    }
}


open class KrailVerticle : VaadinVerticle() {

    private lateinit var injectorLocator: InjectorLocator

    /**
     * When service has been initialised, create the Guice injector.  See [InjectorLocator] for its location
     */
    override fun serviceInitialized(service: VertxVaadinService, router: Router) {
        getInjector() // just to pre-load the Injector
    }

    fun getInjector(): Injector {
        return injectorLocator.get()
    }
}

interface BootstrapLoader {
    fun load(): BootstrapConfig
}

class VertxBootstrapLoader : BootstrapLoader {
    override fun load(): BootstrapConfig {
        // https@ //stackoverflow.com/questions/1900154/classpath-resource-within-jar
        val content = this.javaClass.getResourceAsStream("/$bootstrapYml")
        return BootstrapYAMLReader().read(content)
    }

}

class ServletBootstrapLoader : BootstrapLoader {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun load(): BootstrapConfig {

        try {
            log.debug("try loading bootstrap without leading /")
            val url = Thread.currentThread().contextClassLoader.getResource(bootstrapYml)
            val file = Paths.get(url.toURI()).toFile()
            val fis = file.inputStream()
            log.debug("found using Thread loader, without leading /")
            return BootstrapYAMLReader().read(fis)
        } catch (fnf: Exception) {
            throw BootstrapException("Unable to load bootstrap file from ${fnf.javaClass.simpleName}.  See the 'Bootstrap' section of the Krail User Guide", fnf)
        }
    }
}

class BootstrapException(msg: String, e: Exception) : RuntimeException(msg, e)

const val bootstrapYml = "krail-bootstrap.yml"

@Suppress("UNCHECKED_CAST")
class BootstrapYAMLReader {

    fun read(inputStream: InputStream): BootstrapConfig {
        val yaml = Yaml()
        val result = yaml.load(inputStream) as LinkedHashMap<String, Any>
        return processInput(result)
    }

    fun read(input: String): BootstrapConfig {
        val yaml = Yaml()
        val result = yaml.load(input) as LinkedHashMap<String, Any>
        return processInput(result)
    }

    private fun processInput(result: LinkedHashMap<String, Any>): BootstrapConfig {
        val filename = "krail-bootstrap.yml"
        @Suppress("UNUSED_VARIABLE")
        val version = result["version"] ?: 1  // leave here, we may need it one day
        val collator = result["collator"] as String?
                ?: throw BootstrapConfigurationException("$filename must contain a 'collator' property")
        val modules = result["modules"] as List<String>? ?: listOf<String>()


        val servlet = result["servlet"] as Map<String, Any>?
        val servletConfig = if (servlet == null) {
            EnvironmentConfig(listOf("uk.q3c.krail.core.guice.ServletEnvironmentModule"))
        } else {
            val servletModules = servlet["modules"] as List<String>? ?: listOf()
            EnvironmentConfig(servletModules)
        }

        val vertx = result["vertx"] as Map<String, Any>?
        val vertxConfig = if (vertx == null) {
            EnvironmentConfig(listOf("uk.q3c.krail.core.guice.VertxEnvironmentModule"))
        } else {
            val vertxModules = vertx["modules"] as List<String>? ?: listOf()
            EnvironmentConfig(vertxModules)
        }

        return BootstrapConfig(collator = collator, servletConfig = servletConfig, vertxConfig = vertxConfig, version = 1, modules = modules)
    }
}

data class BootstrapConfig(val version: Int = 1, val collator: String, val modules: List<String>, val servletConfig: EnvironmentConfig, val vertxConfig: EnvironmentConfig)
data class EnvironmentConfig(val additionalModules: List<String> = listOf())

class BootstrapConfigurationException(msg: String) : RuntimeException(msg)

interface BindingsCollator {
    fun allModules(): List<Module>
}

class KrailVertxBootstrapModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(VertxInjectorLocator::class.java)
        bind(BootstrapLoader::class.java).to(VertxBootstrapLoader::class.java)
    }
}

class KrailServletBootstrapModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(BootstrapLoader::class.java).to(ServletBootstrapLoader::class.java)
    }
}