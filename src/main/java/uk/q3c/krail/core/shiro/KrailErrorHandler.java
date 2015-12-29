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
package uk.q3c.krail.core.shiro;

import com.google.inject.Inject;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.shiro.aop.NotAGuestException;
import uk.q3c.krail.core.shiro.aop.NotAUserException;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.view.component.LoginFormException;

/**
 * Extends the {@link DefaultErrorHandler} to intercept known V& exceptions, including Shiro related exceptions -
 * {@link UnauthorizedException} and {@link UnauthenticatedException}. Uses pluggable handlers for all caught
 * exceptions.
 *
 * @author David Sowerby 4 Jan 2013
 */
public class KrailErrorHandler extends DefaultErrorHandler {

    private final UnauthenticatedExceptionHandler authenticationHandler;
    private final NotAGuestExceptionHandler notAGuestExceptionHandler;
    private final NotAUserExceptionHandler notAUserExceptionHandler;
    private final UnauthorizedExceptionHandler authorisationHandler;
    //    private final InvalidURIHandler invalidUriHandler;
    private final Navigator navigator;
    private UserNotifier userNotifier;

    @Inject
    protected KrailErrorHandler(UnauthenticatedExceptionHandler authenticationHandler, NotAGuestExceptionHandler notAGuestExceptionHandler,
                                NotAUserExceptionHandler notAUserExceptionHandler, UnauthorizedExceptionHandler authorisationHandler,
                                Navigator navigator, UserNotifier userNotifier) {
        super();
        this.authenticationHandler = authenticationHandler;
        this.notAGuestExceptionHandler = notAGuestExceptionHandler;
        this.notAUserExceptionHandler = notAUserExceptionHandler;
        this.authorisationHandler = authorisationHandler;
        this.navigator = navigator;
        this.userNotifier = userNotifier;
    }

    @Override
    public void error(ErrorEvent event) {
        Throwable originalError = event.getThrowable();


        // handle an unauthorised access attempt
        int unauthorised = ExceptionUtils.indexOfThrowable(originalError, UnauthorizedException.class);
        if (unauthorised >= 0) {
            authorisationHandler.invoke();
            return;
        }

        // handle an unauthenticated access attempt
        int unauthenticated = ExceptionUtils.indexOfThrowable(originalError, UnauthenticatedException.class);
        if (unauthenticated >= 0) {
            authenticationHandler.invoke();
            return;
        }


        int notAUser = ExceptionUtils.indexOfThrowable(originalError, NotAUserException.class);
        if (notAUser >= 0) {
            notAUserExceptionHandler.invoke();
            return;
        }


        int notAGuest = ExceptionUtils.indexOfThrowable(originalError, NotAGuestException.class);
        if (notAGuest >= 0) {
            notAGuestExceptionHandler.invoke();
            return;
        }


        // catch-all handle an unauthorised access attempt, exceptions are not always thrown at more specific level
        unauthorised = ExceptionUtils.indexOfThrowable(originalError, AuthorizationException.class);
        if (unauthorised >= 0) {
            authorisationHandler.invoke();
            return;
        }


        // no handler identified, display the exception on the error page
        int loginEmpty = ExceptionUtils.indexOfThrowable(originalError, LoginFormException.class);
        if (loginEmpty > 0) {
            LoginFormException lfe = (LoginFormException) ExceptionUtils.getThrowableList(originalError)
                                                                        .get(loginEmpty);
            userNotifier.notifyWarning(lfe.getMsgKey(), lfe.getParams());
            return;
        }

        navigator.error(event.getThrowable());

    }

}
