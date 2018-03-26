package uk.q3c.krail.core.guice

import com.github.mcollovati.vertx.vaadin.VaadinVerticle
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import io.vertx.core.Vertx
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
        return Vertx.currentContext().get(injectorKey)
    }

}

class ServletInjectorLocator : InjectorLocator {

    override fun put(injector: Injector) {
        InjectorHolder.setInjector(injector)
    }

    override fun get(): Injector {
        return InjectorHolder.getInjector()
    }
}


enum class RuntimeEnvironment { SERVLET, VERTX }

class InjectorFactory {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    fun createInjector(runtimeEnvironment: RuntimeEnvironment) {
        val bootstrapModule = if (runtimeEnvironment == SERVLET) {
            KrailServletBootstrapModule()
        } else {
            KrailVertxBootstrapModule()
        }
        createInjector(runtimeEnvironment, bootstrapModule)
    }


    fun createInjector(runtimeEnvironment: RuntimeEnvironment, bootstrapModule: Module) {
        val bootstrapInjector = Guice.createInjector(bootstrapModule)
        val bootstrapLoader = bootstrapInjector.getInstance(BootstrapLoader::class.java)
        val bootstrapConfig = bootstrapLoader.load()
        val environmentConfig =
                when (runtimeEnvironment) {
                    SERVLET -> bootstrapConfig.servletConfig
                    VERTX -> bootstrapConfig.vertxConfig
                }
        val collatorClass = Class.forName(bootstrapConfig.collator)
        val collator = bootstrapInjector.getInstance(collatorClass) as BindingsCollator
        val additionalModuleNames = environmentConfig.additionalModules
        val additionalModules: MutableList<Module> = mutableListOf()
        for (moduleName in additionalModuleNames) {
            val moduleClass = Class.forName(moduleName)
            val module = bootstrapInjector.getInstance(moduleClass) as Module
            additionalModules.add(module)
        }
        val allModules: MutableList<Module> = mutableListOf()
        allModules.addAll(collator.allModules())
        allModules.addAll(additionalModules)
        allModules.add(bootstrapModule) // we need the InjectorLocator binding
        val injectorLocator = bootstrapInjector.getInstance(InjectorLocator::class.java)
        val realInjector = Guice.createInjector(allModules)
        log.debug("injector created")
        val securityManager: SecurityManager = realInjector.getInstance(SecurityManager::class.java)
        SecurityUtils.setSecurityManager(securityManager)
        log.debug("Security manager set")
        injectorLocator.put(realInjector)
    }
}


class KrailVerticle : VaadinVerticle()

interface BootstrapLoader {
    fun load(): BootstrapConfig
}

class VertxBootstrapLoader : BootstrapLoader {
    override fun load(): BootstrapConfig {
        val filesystem = Vertx.vertx().fileSystem()
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
        val version = result["version"] ?: 1
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

class BootstrapConfig(val version: Int = 1, val collator: String, val modules: List<String>, val servletConfig: EnvironmentConfig, val vertxConfig: EnvironmentConfig)
class EnvironmentConfig(val additionalModules: List<String> = listOf())

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