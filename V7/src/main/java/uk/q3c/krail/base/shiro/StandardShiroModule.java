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
package uk.q3c.krail.base.shiro;

import com.google.inject.binder.AnnotatedBindingBuilder;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;

import java.util.Collection;

/**
 * Bindings for Shiro and user related implementations
 *
 * @author David Sowerby 15 Jul 2013
 */
public class StandardShiroModule extends ShiroModule {

    public StandardShiroModule() {
        super();
    }

    @Override
    protected void configureShiro() {

        bindCredentialsMatcher();
        bindLoginAttemptLog();
        bindRealms();
        bindSubjectIdentifier();
        expose(SubjectIdentifier.class);
        bindSubjectProvider();
    }

    /**
     * Override this to provide your own {@link SubjectIdentifier} implementation
     */
    protected void bindSubjectIdentifier() {
        bind(SubjectIdentifier.class).to(DefaultSubjectIdentifier.class);
    }

    /**
     * Override this to bind your own Realm implementation(s). Multiple calls can be made to bindRealm();
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
     * Override this method to bind your own {@link CredentialsMatcher} implementation
     */
    protected void bindCredentialsMatcher() {
        bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);
    }

    protected void bindSubjectProvider() {
        bind(Subject.class).toProvider(SubjectProvider.class);
    }

    @Override
    protected void bindSecurityManager(AnnotatedBindingBuilder<? super SecurityManager> bind) {
        try {
            bind.toConstructor(V7SecurityManager.class.getConstructor(Collection.class))
                .asEagerSingleton();
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("This really shouldn't happen.  Either something has changed in Shiro, " +
                    "or there's a bug in ShiroModule.", e);
        }
    }

    @Override
    protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
        bind.to(VaadinSessionManager.class)
            .asEagerSingleton();
    }

}
