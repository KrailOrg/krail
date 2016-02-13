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

import org.aopalliance.intercept.MethodInvocation
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import spock.lang.Specification

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * Created by David Sowerby on 21 Jan 2016
 */
class DefaultAnnotationResolverTest extends Specification {

    DefaultAnnotationResolver resolver
    InterceptorTestClass interceptorTestClass
    MethodInvocation methodInvocation = Mock()

    def setup() {
        resolver = new DefaultAnnotationResolver()
        interceptorTestClass = new InterceptorTestClass()
    }

    def "called with null throws IllegalArgumentException"() {

        when:
        resolver.getAnnotation(null, RequiresAuthentication)
        then:
        thrown(IllegalArgumentException)
    }

    def "call successful, annotation found"() {
        given:
        Method method = InterceptorTestClass.class.getDeclaredMethod("requiresRole")
        methodInvocation.getMethod() >> method

        when:
        Annotation annotation = resolver.getAnnotation(methodInvocation, RequiresRoles)

        then:
        annotation != null
    }

    def "method is null, throws IllegalArgumentException"() {
        given:
        methodInvocation.getMethod() >> null

        when:
        Annotation annotation = resolver.getAnnotation(methodInvocation, RequiresRoles)

        then:
        thrown(IllegalArgumentException)
    }

//    def "method is ok, but does not have annotation, returns null"() {
//        given:
//        Method method = InterceptorTestClass.class.getDeclaredMethod("requiresPermissions")
//        methodInvocation.getMethod() >> method
//
//        when:
//        Annotation annotation=resolver.getAnnotation(methodInvocation,RequiresRoles)
//
//        then: annotation==null
//    }
}
