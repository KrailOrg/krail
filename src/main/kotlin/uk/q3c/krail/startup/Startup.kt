package uk.q3c.krail.startup

import com.google.inject.AbstractModule
import com.google.inject.Inject

/**
 * Invoked to enable the developer to define different start up regimes - for example to start services.
 *
 * Created by David Sowerby on 14 Apr 2018
 */
interface ApplicationStartup {

    fun invoke()
}


class DefaultApplicationStartup @Inject constructor() : ApplicationStartup {
    override fun invoke() {
        TODO()
    }
}


class DefaultStartupModule : AbstractModule() {

    override fun configure() {
        bind(ApplicationStartup::class.java).to(DefaultApplicationStartup::class.java)
    }

}