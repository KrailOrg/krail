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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public abstract class ShiroIntegrationTestBase extends AbstractShiroTest {

    protected static final String view1 = "private/view1";
    protected static final String view2 = "public/view2";

    protected Subject subject;
    @Mock
    protected Provider<Subject> subjectPro;
    @Mock
    HttpSession httpSession;
    @Mock
    ServletContext servletContext;
    @Mock
    HttpServletRequest servletRequest;
    @Mock
    HttpServletResponse servletResponse;
    @Inject
    Realm realm;

    @BeforeClass
    public static void beforeClass() {
        // 0. Build and set the SecurityManager used to build Subject instances used in your tests
        // This typically only needs to be done once per class if your shiro.ini doesn't change,
        // otherwise, you'll need to do this logic in each test that is different
        // Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:test.shiro.ini");
        // setSecurityManager(factory.getInstance());
        setSecurityManager(new KrailSecurityManager());
    }

    @Before
    public void setupShiro() {
        // trick the SubjectBuilder into using a WebSubject
        when(servletRequest.getSession(false)).thenReturn(httpSession);
        RealmSecurityManager rsm = getSecurityManager();
        rsm.setRealm(getRealm());
        // 1. Build the Subject instance for the test to run:
        subject = new WebSubject.Builder(getSecurityManager(), servletRequest, servletResponse).buildSubject();
        // 2. Bind the subject to the current thread:
        setSubject(subject);
        when(subjectPro.get()).thenReturn(subject);

    }

    protected Realm getRealm() {
        return realm;
    }

    @After
    public void tearDownSubject() {
        // 3. Unbind the subject from the current thread:
        clearSubject();
    }

    @ModuleProvider
    protected AbstractModule realmModule() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(LoginAttemptLog.class).to(DefaultLoginAttemptLog.class);
                bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);
            }

        };
    }

    @ModuleProvider
    protected ShiroWebModule webModule() {
        return new ShiroWebModule(servletContext) {

            @Override
            protected void configureShiroWeb() {
                bind(Realm.class).to(DefaultRealm.class);
                expose(Realm.class);
                bindRealm().to(Realm.class);

            }

        };
    }

}
