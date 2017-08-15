/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.shiro.aop

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.vaadin.server.VaadinSession
import org.apache.shiro.SecurityUtils
import org.apache.shiro.ShiroException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.annotation.*
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.i18n.Caption
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.shiro.KrailSecurityManager
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.i18n.test.TestI18NModule
import uk.q3c.krail.option.test.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule
import uk.q3c.util.UtilModule

/**
 *
 *  Tests are limited to ensuring that the interceptors are called - not whether they do the right thing.  (The latter takes a lot of setting up of the Subject and is nothing to do with the module under test)
 *
 * Created by David Sowerby on 20 Jan 2016
 */
class KrailShiroAopModuleTest extends Specification {

    KrailSecurityManager krailSecurityManager = Mock()
    VaadinSession vaadinSession = Mock()
    Injector injector

    def setup() {
        SecurityUtils.setSecurityManager(krailSecurityManager)
        ThreadContext.unbindSubject()
    }

    def "PermissionsMethodInterceptor throws exception when invoked through AOP"() {

        given:
        createInjector(new KrailShiroAopModule())

        when:

        PermissionsMethodInterceptor interceptor = injector.getInstance(PermissionsMethodInterceptor)


        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresPermissions()

        then:
        thrown(UnauthenticatedException)

    }

    def "RolesMethodInterceptor not selected, does not throw exception because method AOP not invoked"() {

        given:
        createInjector(new KrailShiroAopModule())

        when:
        RolesMethodInterceptor interceptor = injector.getInstance(RolesMethodInterceptor)

        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresRoles()

        then:
        true
    }

    def "RolesMethodInterceptor throws exception when invoked through AOP"() {

        given:
        createInjector(new KrailShiroAopModule().select(RequiresRoles))

        when:

        RolesMethodInterceptor interceptor = injector.getInstance(RolesMethodInterceptor)


        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresRoles()

        then:
        thrown(RuntimeException)
    }

    def "AuthenticatedMethodInterceptor not selected, does not throw exception because method AOP not invoked"() {

        given:
        createInjector(new KrailShiroAopModule())

        when:
        AuthenticatedMethodInterceptor interceptor = injector.getInstance(AuthenticatedMethodInterceptor)

        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresAuthentication()

        then:
        true
    }

    def "AuthenticatedMethodInterceptor throws exception when invoked through AOP"() {

        given:
        createInjector(new KrailShiroAopModule().select(RequiresAuthentication))

        when:

        RolesMethodInterceptor interceptor = injector.getInstance(RolesMethodInterceptor)


        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresAuthentication()

        then:
        thrown(RuntimeException)
    }

    def "GuestMethodInterceptor not selected, does not throw exception because method AOP not invoked"() {

        given:
        createInjector(new KrailShiroAopModule())

        when:
        GuestMethodInterceptor interceptor = injector.getInstance(GuestMethodInterceptor)

        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresGuest()

        then:
        true
    }

    def "GuestMethodInterceptor throws exception when invoked through AOP"() {

        given:
        // need to mock the subject as by default will appear to be a guest, and therefore not throw an exception
        // we need the exception to ensure AOP interception has happened
        Subject subject = Mock()
        subject.getPrincipal() >> "anything"
        createInjector(new KrailShiroAopModule().select(RequiresGuest))
        vaadinSession.getAttribute(SubjectProvider.SUBJECT_ATTRIBUTE) >> subject

        when:

        GuestMethodInterceptor interceptor = injector.getInstance(GuestMethodInterceptor)


        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresGuest()

        then:
        thrown(RuntimeException)
    }

    def "UserMethodInterceptor not selected, does not throw exception because method AOP not invoked"() {

        given:
        createInjector(new KrailShiroAopModule())

        when:
        UserMethodInterceptor interceptor = injector.getInstance(UserMethodInterceptor)

        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresUser()

        then:
        true
    }

    def "UserMethodInterceptor throws exception when invoked through AOP"() {

        given:
        createInjector(new KrailShiroAopModule().select(RequiresUser))

        when:

        UserMethodInterceptor interceptor = injector.getInstance(UserMethodInterceptor)


        then:
        injector != null
        interceptor != null
        interceptor.getSubject() != null
        interceptor.getAnnotationResolver() != null

        when:

        InterceptorTestClass testClass = injector.getInstance(InterceptorTestClass)
        testClass.requiresUser()

        then:
        thrown(RuntimeException)
    }

    def "invalid Annotation type in module.select"() {
        when:

        new KrailShiroAopModule().select(Caption)

        then:

        thrown(ShiroException)
    }

    def "select default is RequiresPermissions only"() {

        given:
        KrailShiroAopModule module = new KrailShiroAopModule()

        when:
        createInjector(module)

        then:

        module.getSelectedAnnotations().size() == 1
        module.getSelectedAnnotations().contains(RequiresPermissions)
    }

    def "select default, plus a couple more"() {

        given:
        KrailShiroAopModule module = new KrailShiroAopModule().select(RequiresUser).select(RequiresGuest)

        when:
        createInjector(module)

        then:

        module.getSelectedAnnotations().size() == 3
        module.getSelectedAnnotations().containsAll(RequiresPermissions, RequiresUser, RequiresGuest)
    }

    def "select all"() {

        given:
        KrailShiroAopModule module = new KrailShiroAopModule().selectAll()

        when:
        createInjector(module)

        then:

        module.getSelectedAnnotations().size() == 5
        module.getSelectedAnnotations().containsAll(RequiresPermissions, RequiresUser, RequiresGuest, RequiresAuthentication, RequiresRoles)
    }


    private void createInjector(Module module) {
        injector = Guice.createInjector(module, new DefaultShiroModule(), new TestI18NModule(), new TestVaadinSessionScopeModule(), new EventBusModule(), new TestOptionModule(), new UtilModule(), new TestUIScopeModule(), new InMemoryModule())
        VaadinSession.setCurrent(vaadinSession)
    }
}