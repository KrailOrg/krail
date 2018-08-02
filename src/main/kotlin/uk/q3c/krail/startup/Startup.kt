package uk.q3c.krail.startup

import com.google.inject.AbstractModule
import com.google.inject.Inject
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.shareddata.AsyncMap
import java.time.LocalDateTime


/**
 * Invoked to enable the developer to define different start up regimes - for example to start services.
 *
 * *NOTE* This code is invoked at the start up of every application **instance**, so if you are running in a Vertx
 * environment where there may be a multiple Verticle instances, or a clustered app server environment, your startup code
 * will need to deal with the existence of multiple application instances and act accordingly.
 *
 * Created by David Sowerby on 14 Apr 2018
 */
interface ApplicationStartup {
    fun invoke()
}

/**
 * See notes in [ApplicationStartup] regarding multi-instance environments.  The default implementation for this interface
 * supports use in such an environment - sub-classing it is the easiest way to
 */
interface VertxApplicationStartup : ApplicationStartup

interface ServletApplicationStartup : ApplicationStartup


/**
 *
 */
open class DefaultVertxApplicationStartup @Inject constructor() : VertxApplicationStartup {
    override fun invoke() {
        val alreadyStarted = checkForMarker()
        if (!alreadyStarted) {
            executeCode()
            storeMarker()
        }

    }

    private fun checkForMarker(): Boolean {
        val sd = Vertx.vertx().sharedData()
        var exists = false
        sd.getAsyncMap("shared_data") { res: AsyncResult<AsyncMap<String, String>> ->
            if (res.failed()) {
                throw StartupCodeException("Failed to access Vertx shared data, cause: ${res.cause().message}")
            } else {
                res.result().get(STARTUP_ATTRIBUTE) { value: AsyncResult<String> -> exists = value.result() != null }
            }
        }
        return exists
    }

    private fun storeMarker() {
        val sd = Vertx.vertx().sharedData()
        sd.getAsyncMap("shared_data") { res: AsyncResult<AsyncMap<String, String>> ->
            if (res.failed()) {
                throw StartupCodeException("Failed to access Vertx shared data, cause: ${res.cause().message}")
            } else {
                val amap = res.result()
                amap.put(STARTUP_ATTRIBUTE, LocalDateTime.now().toString()) { comp: AsyncResult<Void> ->
                    if (comp.failed()) {
                        throw StartupCodeException("Failed to put startup marker in access Vertx shared data, cause: ${comp.cause().message}")
                    }
                }
            }
        }
    }

    open fun executeCode() {
        // do nothing by default
    }
}

class StartupCodeException(msg: String) : RuntimeException(msg)

class DefaultServletApplicationStartup @Inject constructor() : ServletApplicationStartup {
    override fun invoke() {
        // deliberately does nothing, stat up code is application specific
    }
}

/**
 * You may need to bind this as an alternative to [DefaultVertxApplicationStartup] while testing, to avoid calls to Vertx itself
 */
class VertxTestApplicationStartup @Inject constructor() : VertxApplicationStartup {
    override fun invoke() {
        // deliberately does nothing, stat up code is application specific
    }
}


@Suppress("ReplaceToWithInfixForm")
class DefaultStartupModule : AbstractModule() {

    override fun configure() {
        bind(ServletApplicationStartup::class.java).to(DefaultServletApplicationStartup::class.java)
        bind(VertxApplicationStartup::class.java).to(DefaultVertxApplicationStartup::class.java)
    }

}

class TestStartupModule : AbstractModule() {

    override fun configure() {
        bind(ServletApplicationStartup::class.java).to(DefaultServletApplicationStartup::class.java)
        bind(VertxApplicationStartup::class.java).to(VertxTestApplicationStartup::class.java)
    }

}

const val STARTUP_ATTRIBUTE = "application startup"