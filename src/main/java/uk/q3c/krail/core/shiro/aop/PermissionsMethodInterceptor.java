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
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.shiro.SubjectProvider;

/**
 * AOP MethodInterceptor to detect whether a user has the required permissions.  Detection logic is a copy of the native Shiro version in {@link
 * PermissionAnnotationHandler}
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class PermissionsMethodInterceptor extends ShiroMethodInterceptor<RequiresPermissions> {

    @Inject
    public PermissionsMethodInterceptor(Provider<SubjectProvider> subjectProviderProvider, Provider<AnnotationResolver> annotationResolverProvider) {
        super(RequiresPermissions.class, subjectProviderProvider, annotationResolverProvider);
    }

    /**
     * The 'check' methods will throw an exception as necessary
     *
     * @param rpAnnotation the annotation to use for checking permissions
     */
    @Override
    public void assertAuthorized(RequiresPermissions rpAnnotation) {

        String[] perms = rpAnnotation.value();
        Subject subject = getSubject();
        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        if (Logical.AND == (rpAnnotation.logical())) {
            getSubject().checkPermissions(perms);
            return;
        }
        if (Logical.OR == (rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms) {
                if (getSubject().isPermitted(permission)) {
                    hasAtLeastOnePermission = true;
                }
            }
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOnePermission) {
                getSubject().checkPermission(perms[0]);
            }

        }
    }
}
