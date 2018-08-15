/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.shiro

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.server.DefaultErrorHandler
import com.vaadin.server.ErrorEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import uk.q3c.krail.core.error.SystemErrorNotificationGroup
import uk.q3c.krail.core.shiro.aop.NotAGuestException
import uk.q3c.krail.core.shiro.aop.NotAUserException
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.view.component.LoginFormException
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream

/**
 * Extends the [DefaultErrorHandler] to intercept known V& exceptions, including Shiro related exceptions -
 * [UnauthorizedException] and [UnauthenticatedException]. Uses pluggable handlers for all caught
 * exceptions.
 *
 * @author David Sowerby 4 Jan 2013
 */
class KrailErrorHandler @Inject
constructor(private val authenticationHandler: UnauthenticatedExceptionHandler, private val notAGuestExceptionHandler: NotAGuestExceptionHandler,
            private val notAUserExceptionHandler: NotAUserExceptionHandler, private val authorisationHandler: UnauthorizedExceptionHandler,
            private val userNotifier: UserNotifier,
            @field:Transient private val systemErrorNotificationGroupProvider: Provider<SystemErrorNotificationGroup>,
            private val serializationSupport: SerializationSupport)

    : DefaultErrorHandler() {

    override fun error(event: ErrorEvent) {
        val originalError = event.throwable


        // handle an unauthorised access attempt
        var unauthorised = ExceptionUtils.indexOfThrowable(originalError, UnauthorizedException::class.java)
        if (unauthorised >= 0) {
            authorisationHandler.invoke()
            return
        }

        // handle an unauthenticated access attempt
        val unauthenticated = ExceptionUtils.indexOfThrowable(originalError, UnauthenticatedException::class.java)
        if (unauthenticated >= 0) {
            authenticationHandler.invoke()
            return
        }


        val notAUser = ExceptionUtils.indexOfThrowable(originalError, NotAUserException::class.java)
        if (notAUser >= 0) {
            notAUserExceptionHandler.invoke()
            return
        }


        val notAGuest = ExceptionUtils.indexOfThrowable(originalError, NotAGuestException::class.java)
        if (notAGuest >= 0) {
            notAGuestExceptionHandler.invoke()
            return
        }


        // catch-all handle an unauthorised access attempt, exceptions are not always thrown at more specific level
        unauthorised = ExceptionUtils.indexOfThrowable(originalError, AuthorizationException::class.java)
        if (unauthorised >= 0) {
            authorisationHandler.invoke()
            return
        }


        // no handler identified, display the exception on the error page
        val loginEmpty = ExceptionUtils.indexOfThrowable(originalError, LoginFormException::class.java)
        if (loginEmpty > 0) {
            val lfe = ExceptionUtils.getThrowableList(originalError)[loginEmpty] as LoginFormException
            userNotifier.notifyWarning(lfe.msgKey, *lfe.params)
            return
        }

        systemErrorNotificationGroupProvider.get().notify(originalError)

//        log.error("An error has been thrown, reporting via the Error Popup", originalError);
////        userNotifier.notifyError()
//        val textArea = TextArea("Exception thrown")
//        textArea.value = ExceptionUtils.getStackTrace(originalError)
//        textArea.isWordWrap = true
//        textArea.isReadOnly = true
//
//
//
//        MessageBox
//                .createInfo()
//                .withIcon(null)
//                .withWidth("80%")
//                .withHeight("80%")
//                .withMessage(textArea)
//                .withOkButton()
//                .open()

    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }

}




