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

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.vaadin.server.VaadinSession;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.shiro.SubjectProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A replacement of the {@link ShiroAopModule}.  This module, together with its associated {@link MethodInterceptor} implementations, uses {@link
 * SubjectProvider} in place of {@link SecurityUtils#getSubject()} - Krail (and Vaadin generally) cannot use the latter, because the {@link Subject} must be
 * tied to a {@link VaadinSession}
 * <p>
 * By default this module only binds an interceptor for the {@link RequiresPermissions} annotation, but others can be added either by overriding {@link
 * #define}, or by calling {@link #select(Class)} or {@link #selectAll()} from your Binding Manager... for example:
 * <p>
 * Override<br>
 * protected Module shiroAopModule() {<br>
 * return new KrailShiroAopModule().selectAll();<br>
 * }    <br>
 * <p>
 * using either:<br>
 * <p>
 * new KrailShiroAopModule().select(RequiresGuest.class) or<br>
 * new KrailShiroAopModule().selectAll()
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class KrailShiroAopModule extends AbstractModule {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation> allowedAnnotations[] = new Class[]{RequiresPermissions.class, RequiresUser.class, RequiresGuest.class,
            RequiresAuthentication.class, RequiresRoles.class};
    private Set<Class<? extends Annotation>> selectedAnnotations = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bindResolver();
        define();
        bindInterceptors();
    }

    /**
     * Define the interceptors to use by calling {@link #select(Class)}.  Default is to apply the {@link RequiresPermissions} only.  You can also define
     * directly from your BindingManager by calling new KrailShiroAopModule().select(RequiresGuest.class), or new KrailShiroAopModule().selectAll()
     */
    protected void define() {
        select(RequiresPermissions.class);
    }

    public KrailShiroAopModule select(Class<? extends Annotation> annotationClass) {
        List<Class<? extends Annotation>> allowed = Arrays.asList(allowedAnnotations);
        if (!allowed.contains(annotationClass)) {
            throw new ShiroException("Only Shiro annotations are valid");
        }
        selectedAnnotations.add(annotationClass);
        return this;

    }

    protected void bindInterceptors() {
        if (selectedAnnotations.contains(RequiresPermissions.class)) {
            bindMethodInterceptor((RequiresPermissions.class), permissionsInterceptor());
        }
        if (selectedAnnotations.contains(RequiresUser.class)) {
            bindMethodInterceptor((RequiresUser.class), userInterceptor());
        }
        if (selectedAnnotations.contains(RequiresGuest.class)) {
            bindMethodInterceptor((RequiresGuest.class), guestInterceptor());
        }
        if (selectedAnnotations.contains(RequiresAuthentication.class)) {
            bindMethodInterceptor((RequiresAuthentication.class), authenticatedInterceptor());
        }

        if (selectedAnnotations.contains(RequiresRoles.class)) {
            bindMethodInterceptor((RequiresRoles.class), rolesInterceptor());
        }

    }

    private void bindMethodInterceptor(Class<? extends Annotation> shiroAnnotationClass, MethodInterceptor methodInterceptor) {
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(shiroAnnotationClass), methodInterceptor);
    }

    protected PermissionsMethodInterceptor permissionsInterceptor() {
        return new PermissionsMethodInterceptor();
    }

    protected RolesMethodInterceptor rolesInterceptor() {
        return new RolesMethodInterceptor();
    }

    protected AuthenticatedMethodInterceptor authenticatedInterceptor() {
        return new AuthenticatedMethodInterceptor();
    }

    protected UserMethodInterceptor userInterceptor() {
        return new UserMethodInterceptor();
    }

    protected GuestMethodInterceptor guestInterceptor() {
        return new GuestMethodInterceptor();
    }

    protected void bindResolver() {
        bind(AnnotationResolver.class).to(DefaultAnnotationResolver.class);
    }


    public KrailShiroAopModule selectAll() {

        select(RequiresRoles.class);
        select(RequiresGuest.class);
        select(RequiresAuthentication.class);
        select(RequiresUser.class);
        return this;
    }
}
