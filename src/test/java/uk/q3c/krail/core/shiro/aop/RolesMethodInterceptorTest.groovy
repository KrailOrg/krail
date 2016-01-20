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
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.subject.Subject
import spock.lang.Specification
import uk.q3c.krail.core.shiro.SubjectProvider

/**
 *
 *
 *
 * Created by David Sowerby on 19 Jan 2016
 */

class RolesMethodInterceptorTest extends Specification {

    RolesMethodInterceptor interceptor

    SubjectProvider subjectProvider = Mock()
    Provider<SubjectProvider> subjectProviderProvider = Mock()
    Subject subject1 = Mock()
    Subject subject2 = Mock()
    Provider<AnnotationResolver> annotationResolverProvider = Mock()


    def setup() {
        subjectProviderProvider.get() >> subjectProvider
        interceptor = new RolesMethodInterceptor(subjectProviderProvider, annotationResolverProvider)
    }


    def "requires roles a AND b, user1 Ok, user2 not"() {
        given:
        RequiresRoles annotation = new InterceptorTestClass().getAnnotation('requiresRoles')
        subject1.hasRole("a") >> true
        subject1.hasRole("b") >> true
        subject2.hasRole("a") >> true
        subject2.hasRole("b") >> false

        when:
        boolean result1 = interceptor.assertAuthorized(annotation)
        boolean result2 = interceptor.assertAuthorized(annotation)

        then:
        subjectProvider.get() >>> [subject1, subject2]
        result1
        !result2
    }

    def "requires roles a OR b user1 Ok, user2 not"() {
        expect: false
    }


}
