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
package uk.co.q3c.v7.base.shiro;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.vaadin.server.ErrorHandler;

/**
 * Bindings related to Shiro, authentication and authorisation. Override any methods you need to for your application
 * 
 * @author David Sowerby 8 Jan 2013
 * 
 */
public class V7ShiroModule extends AbstractModule {

	@Override
	protected void configure() {
		bindErrorHandler();
		bindShiroExceptionHandlers();
	}

	/**
	 * error handler for the VaadinSession, needed to handle Shiro exceptions
	 */
	protected void bindErrorHandler() {
		bind(ErrorHandler.class).to(V7ErrorHandler.class);
	}

	protected void bindShiroExceptionHandlers() {
		bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
		bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
	}

	protected void bindTimeout() {
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}
}
