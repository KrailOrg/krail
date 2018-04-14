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
package uk.q3c.krail.service.bind;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import uk.q3c.krail.service.Service;
import uk.q3c.util.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Provides bindings and AOP in support of {@link Service}s.
 * <p>
 *
 * @author David Sowerby
 */
public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(), new FinalizeMethodInterceptor());

    }

    private static class FinalizeMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return "finalize".equals(method.getName());
        }
    }

    /**
     * Calls {@link Service#stop} before passing on the finalize() call
     */
    private static class FinalizeMethodInterceptor implements MethodInterceptor {

        public FinalizeMethodInterceptor() {
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Service service = (Service) invocation.getThis();
            service.stop();
            return invocation.proceed();
        }
    }

    /**
     * Matches classes implementing {@link Service}
     */
    private static class ServiceInterfaceMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            Set<Class<?>> interfaces = ReflectionUtils.allInterfaces(rawType);

            for (Class<?> intf : interfaces) {
                if (intf.equals(Service.class)) {
                    return true;
                }
            }
            return false;
        }
    }


}