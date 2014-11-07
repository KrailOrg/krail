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

import com.google.inject.Inject;
import com.vaadin.server.VaadinSession;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class KrailSecurityManager extends DefaultSecurityManager {
    private static Logger log = LoggerFactory.getLogger(KrailSecurityManager.class);

    @Inject
    private VaadinSessionProvider sessionProvider;

    public KrailSecurityManager() {
        super();
    }

    public KrailSecurityManager(Collection<Realm> realms) {
        super(realms);
    }

    @Override
    protected void onSuccessfulLogin(AuthenticationToken token, AuthenticationInfo info, Subject subject) {
        super.onSuccessfulLogin(token, info, subject);
        setSubject(subject);
    }

    protected void setSubject(Subject subject) {
        VaadinSession session = sessionProvider.get();
        log.debug("storing Subject instance in VaadinSession");
        session.setAttribute(Subject.class, subject);
    }

    /**
     * Method injection is needed because the constructor has to complete
     *
     * @see org.apache.shiro.mgt.SessionsSecurityManager#setSessionManager(org.apache.shiro.session.mgt.SessionManager)
     */
    @Inject
    @Override
    public void setSessionManager(SessionManager sessionManager) {
        super.setSessionManager(sessionManager);
    }

    public void setSessionProvider(VaadinSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

}
