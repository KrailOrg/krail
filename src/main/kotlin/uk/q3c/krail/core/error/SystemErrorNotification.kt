package uk.q3c.krail.core.error

import com.google.inject.Inject
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.shiro.KrailErrorHandler
import uk.q3c.krail.core.user.notify.UserNotificationLabelKey
import uk.q3c.krail.core.user.notify.UserNotifier
import java.io.Serializable

/**
 *
 * General interface for any implementation which notifies something of a system error (a system error being any unhandled exception)
 *
 * There are a number of sub-interfaces purely for convenience, but all implementations of [SystemErrorNotification], but they can be combined as desired in a [SystemErrorNotificationGroup] implementation, and all invoked if a system error occurs.
 *
 * Collectively, these interfaces and their implementations enable a plugin approach to error handling, so that you can provide any number of responses to errors, configurable through Guice.
 *
 * All bindings are defined in the [ErrorModule]
 *
 *
 *
 * Created by David Sowerby on 13 Aug 2018
 */
interface SystemErrorNotification : Serializable {

    fun notify(error: Throwable)
}

/**
 * Notifies the occurrence of a system error (that is, any unhandled exception) to some form of logging system
 */
interface SystemErrorLogNotification : SystemErrorNotification

/**
 * Notifies the occurrence of a system error (that is, any unhandled exception) to the current user
 */
interface SystemErrorUserNotification : SystemErrorNotification

/**
 * Notifies the occurrence of a system error (that is, any unhandled exception) to an admin user
 */
interface SystemErrorAdminNotification : SystemErrorNotification

/**
 * Notifies the occurrence of a system error (that is, any unhandled exception) to a monitoring system
 */
interface SystemErrorMonitorNotification : SystemErrorNotification

/**
 * Notifies the occurrence of a system error (that is, any unhandled exception) to a Service Desk.
 */
interface SystemErrorServiceDeskNotification : SystemErrorNotification


/**
 * Groups together [SystemErrorNotification] instances for use in [KrailErrorHandler] - each instance is invoked when a system error occurs.
 *
 *
 */
interface SystemErrorNotificationGroup : Serializable {
    fun notify(error: Throwable)
}

/**
 * Invokes a [SystemErrorLogNotification] and a [SystemErrorUserNotification]
 */
class DefaultSystemErrorNotificationGroup @Inject constructor(val logNotification: SystemErrorLogNotification, val userNotification: SystemErrorUserNotification) : SystemErrorNotificationGroup {
    override fun notify(error: Throwable) {
        logNotification.notify(error)
        userNotification.notify(error)
    }

}

class DefaultSystemErrorLogNotification : SystemErrorLogNotification {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun notify(error: Throwable) {
        log.error("Unhandled exception caught by SystemErrorLogNotification: ", error)
    }
}


class DefaultSystemErrorUserNotification @Inject constructor(val userNotifier: UserNotifier) : SystemErrorUserNotification {
    override fun notify(error: Throwable) {
        userNotifier.notifyError(UserNotificationLabelKey.A_system_error_occurred, ExceptionUtils.getRootCauseStackTrace(error))
    }

}