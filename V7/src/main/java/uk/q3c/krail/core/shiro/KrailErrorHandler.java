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
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import uk.q3c.krail.core.navigate.InvalidURIException;
import uk.q3c.krail.core.navigate.InvalidURIExceptionHandler;
import uk.q3c.krail.core.navigate.Navigator;

/**
 * Extends the {@link DefaultErrorHandler} to intercept known V& exceptions, including Shiro related exceptions -
 * {@link UnauthorizedException} and {@link UnauthenticatedException}. Uses pluggable handlers for all caught
 * exceptions.
 *
 * @author David Sowerby 4 Jan 2013
 */
public class KrailErrorHandler extends DefaultErrorHandler {

    private final UnauthenticatedExceptionHandler authenticationHandler;
    private final UnauthorizedExceptionHandler authorisationHandler;
    private final InvalidURIExceptionHandler invalidUriHandler;
    private final Navigator navigator;

    @Inject
    protected KrailErrorHandler(UnauthenticatedExceptionHandler authenticationHandler,
                                UnauthorizedExceptionHandler authorisationHandler,
                                InvalidURIExceptionHandler invalidUriHandler, Navigator navigator) {
        super();
        this.authenticationHandler = authenticationHandler;
        this.authorisationHandler = authorisationHandler;
        this.invalidUriHandler = invalidUriHandler;
        this.navigator = navigator;
    }

    @Override
    public void error(ErrorEvent event) {
        Throwable originalError = event.getThrowable();

        // handle an attempt to navigate to an invalid page
        int invalidURI = ExceptionUtils.indexOfThrowable(originalError, InvalidURIException.class);
        if (invalidURI >= 0) {
            InvalidURIException e = (InvalidURIException) ExceptionUtils.getThrowables(originalError)[invalidURI];
            invalidUriHandler.invoke(e);
            return;
        }

        // handle an unauthenticated access attempt
        int unauthenticated = ExceptionUtils.indexOfThrowable(originalError, UnauthenticatedException.class);
        if (unauthenticated >= 0) {
            authenticationHandler.invoke();
            return;
        }

        // handle an unauthorised access attempt
        int unauthorised = ExceptionUtils.indexOfThrowable(originalError, UnauthorizedException.class);
        if (unauthorised >= 0) {
            authorisationHandler.invoke();
            return;
        }

        navigator.error(event.getThrowable());

    }

}
