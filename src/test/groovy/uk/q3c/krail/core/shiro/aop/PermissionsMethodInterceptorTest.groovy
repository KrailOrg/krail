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

import com.google.inject.Provider
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.subject.Subject
import spock.lang.Specification
import uk.q3c.krail.core.shiro.SubjectProvider

/**
 *
 *
 *
 * Created by David Sowerby on 19 Jan 2016
 */

class PermissionsMethodInterceptorTest extends Specification {

    PermissionsMethodInterceptor interceptor

    SubjectProvider subjectProvider = Mock()
    Provider<SubjectProvider> subjectProviderProvider = Mock()
    Subject subject1 = Mock()
    Subject subject2 = Mock()
    Provider<AnnotationResolver> annotationResolverProvider = Mock()


    def setup() {
        subjectProviderProvider.get() >> subjectProvider
        interceptor = new PermissionsMethodInterceptor(subjectProviderProvider, annotationResolverProvider)
    }


    def "requires multiple permissions (a AND b), user1 Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermissions')
        subjectProvider.get() >> subject1
        subject1.checkPermissions(["a", "b"])

        when:
        interceptor.assertAuthorized(annotation)

        then:
        noExceptionThrown()
    }

    def "requires multiple permissions (a AND b), user2 not Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermissions')
        subjectProvider.get() >> subject2
        subject2.checkPermissions(["a", "b"]) >> { throw new AuthorizationException("x") }

        when:
        interceptor.assertAuthorized(annotation)

        then:
        thrown(RuntimeException)

    }


    def "requires multiple permissions (a OR b), user1 Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermissionsOr')
        subjectProvider.get() >> subject1
        subject1.isPermitted("a") >> true
        subject1.isPermitted("b") >> false

        when:
        interceptor.assertAuthorized(annotation)

        then:
        noExceptionThrown()
    }

    def "requires multiple permissions (a OR b), user2 not Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermissionsOr')
        subjectProvider.get() >> subject2
        subject2.isPermitted("a") >> false
        subject2.isPermitted("b") >> false
        subject2.checkPermission("a") >> { throw new AuthorizationException("x") }

        when:
        interceptor.assertAuthorized(annotation)

        then:
        thrown(AuthorizationException)
    }

    def "requires single permission (a), user1 Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermission')
        subjectProvider.get() >> subject1
        subject1.isPermitted("a") >> true

        when:
        interceptor.assertAuthorized(annotation)

        then:
        noExceptionThrown()
    }

    def "requires single permission (a), user2 not Ok"() {
        given:
        RequiresPermissions annotation = new InterceptorTestClass().getAnnotation('requiresPermission')
        subjectProvider.get() >> subject2
        subject2.isPermitted("a") >> false
        subject2.checkPermission("a") >> { throw new AuthorizationException("x") }

        when:
        interceptor.assertAuthorized(annotation)

        then:
        thrown(AuthorizationException)
    }


}
