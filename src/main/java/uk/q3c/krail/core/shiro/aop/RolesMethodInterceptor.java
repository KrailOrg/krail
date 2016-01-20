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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;

import java.util.Arrays;

/**
 * AOP MethodInterceptor to detect whether a user has the required roles.   Detection logic is a copy of the native Shiro version in {@link
 * RoleAnnotationHandler}
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class RolesMethodInterceptor extends ShiroMethodInterceptor<RequiresRoles> {

    @Inject
    public RolesMethodInterceptor(Provider<SubjectProvider> subjectProviderProvider, Provider<AnnotationResolver> annotationResolverProvider) {

        super(RequiresRoles.class, UnauthorizedException.class, subjectProviderProvider, annotationResolverProvider);
    }


    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified roles, and if not, calls {@link #exception()} indicating that access is
     * denied.
     *
     * @param rrAnnotation
     *         the RequiresRoles annotation to use to check for one or more roles
     */
    public void assertAuthorized(RequiresRoles rrAnnotation) {
        try {
            String[] roles = rrAnnotation.value();

            if (roles.length == 1) {
                getSubject().checkRole(roles[0]);
                return;
            }
            if (Logical.AND == (rrAnnotation.logical())) {
                getSubject().checkRoles(Arrays.asList(roles));
                return;
            }
            if (Logical.OR == (rrAnnotation.logical())) {
                // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
                boolean hasAtLeastOneRole = false;
                for (String role : roles) {
                    if (getSubject().hasRole(role)) {
                        hasAtLeastOneRole = true;
                    }
                }
                // Cause the exception if none of the role match, note that the exception message will be a bit misleading
                if (!hasAtLeastOneRole) {
                    getSubject().checkRole(roles[0]);
                }
            }
        } catch (AuthorizationException ae) {
            exception();
        }
    }
}
