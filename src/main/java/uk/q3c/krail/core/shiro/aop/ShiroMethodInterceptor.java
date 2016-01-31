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

package uk.q3c.krail.core.shiro.aop;

import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.shiro.SubjectProvider;

import java.lang.annotation.Annotation;

/**
 * Created by David Sowerby on 10/06/15.
 */
public abstract class ShiroMethodInterceptor<A extends Annotation> implements MethodInterceptor {
    private static Logger log = LoggerFactory.getLogger(ShiroMethodInterceptor.class);
    protected Class<A> annotationClass;
    private Provider<AnnotationResolver> annotationResolverProvider;
    private Provider<SubjectProvider> subjectProviderProvider;

    public ShiroMethodInterceptor(Class<A> annotationClass, Provider<SubjectProvider>
            subjectProviderProvider, Provider<AnnotationResolver> annotationResolverProvider) {
        this.annotationClass = annotationClass;
        this.subjectProviderProvider = subjectProviderProvider;
        this.annotationResolverProvider = annotationResolverProvider;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        assertAuthorized(invocation);
        return invocation.proceed();
    }

    public void assertAuthorized(MethodInvocation mi) {
        A annotation = getAnnotationResolver().getAnnotation(mi, annotationClass);
        if (annotation != null) {
            assertAuthorized(annotation);
        }
    }

    public AnnotationResolver getAnnotationResolver() {
        return annotationResolverProvider.get();
    }

    protected abstract void assertAuthorized(A annotation);

    public Subject getSubject() {
        return subjectProviderProvider.get()
                                      .get();
    }


}
