package uk.q3c.krail.core.error

import com.google.inject.AbstractModule
import com.vaadin.server.ErrorHandler
import uk.q3c.krail.core.shiro.KrailErrorHandler

/**
 * Created by David Sowerby on 13 Aug 2018
 */
open class ErrorModule : AbstractModule() {
    override fun configure() {
        bindGroup()
        bindLog()
        bindUser()
        bindErrorHandler()
    }

    open fun bindGroup() {
        bind(SystemErrorNotificationGroup::class.java).to(DefaultSystemErrorNotificationGroup::class.java)
    }

    open fun bindLog() {
        bind(SystemErrorLogNotification::class.java).to(DefaultSystemErrorLogNotification::class.java)
    }

    open fun bindUser() {
        bind(SystemErrorUserNotification::class.java).to(DefaultSystemErrorUserNotification::class.java)
    }

    /**
     * error handler for the VaadinSession, handles Krail (and therefore Shiro) exceptions
     */
    open fun bindErrorHandler() {
        bind(ErrorHandler::class.java).to(KrailErrorHandler::class.java)
    }
}