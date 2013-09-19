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

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.config.IniModule;

import com.google.inject.binder.AnnotatedBindingBuilder;

/**
 * Bindings for Shiro. Logically, the binding for {@link Subject} to
 * {@link SubjectProvider} should be here, but that makes the injector creation
 * complicated, so it resides in {@link IniModule}
 * 
 * @author David Sowerby 15 Jul 2013
 * 
 */
public class DefaultShiroModule extends ShiroModule {

	public DefaultShiroModule() {
		super();
	}

	@Override
	protected void configureShiro() {
		bindCredentialsMatcher();
		bindLoginAttemptLog();
		bindRealms();
	}

	/**
	 * Override this to bind your own Realm implementation(s). Multiple calls
	 * can be made to bindRealm();
	 */
	protected void bindRealms() {
		bindRealm().to(DefaultRealm.class);
	}

	/**
	 * Override this to bind your own implementation of {@link LoginAttemptLog}
	 */
	protected void bindLoginAttemptLog() {
		bind(LoginAttemptLog.class).to(DefaultLoginAttemptLog.class);
	}

	/**
	 * Override this method to bind your own {@link CredentialsMatcher}
	 * implementation
	 */
	protected void bindCredentialsMatcher() {
		bind(CredentialsMatcher.class).to(
				AlwaysPasswordCredentialsMatcher.class);
	}

	@Override
	protected void bindSecurityManager(
			AnnotatedBindingBuilder<? super SecurityManager> bind) {
		try {
			bind.toConstructor(
					V7SecurityManager.class.getConstructor(Collection.class))
					.asEagerSingleton();
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException(
					"This really shouldn't happen.  Either something has changed in Shiro, or there's a bug in ShiroModule.",
					e);
		}
	}

	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {
		bind.to(VaadinSessionManager.class).asEagerSingleton();
	}

}
