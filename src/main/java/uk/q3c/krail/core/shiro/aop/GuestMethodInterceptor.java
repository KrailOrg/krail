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
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.aop.GuestAnnotationHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;

/**
 * An AOP MethodInterceptor to detect whether a user is a Guest or not.  Typical error message might be: "Attempting to perform a guest-only operation.  The
 * current Subject is not a guest (they have been authenticated or remembered from a previous login).  Access denied."
 * <p>
 * Detection logic is a copy of the native Shiro version in {@link GuestAnnotationHandler}
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class GuestMethodInterceptor extends ShiroMethodInterceptor<RequiresGuest> {

    @Inject
    public GuestMethodInterceptor(Provider<SubjectProvider> subjectProviderProvider, Provider<AnnotationResolver> annotationResolverProvider) {
        super(RequiresGuest.class, subjectProviderProvider, annotationResolverProvider);
    }


    /**
     * Ensures that the calling <code>Subject</code> is NOT a <em>user</em>, that is, they do not
     * have an {@link org.apache.shiro.subject.Subject#getPrincipal() identity} before continuing.  If they are
     * a user ({@link org.apache.shiro.subject.Subject#getPrincipal() Subject.getPrincipal()} != null), {@link #exception()} is called, indicating that
     * execution is not allowed to continue.
     *
     * @param a
     *         the annotation to check for one or more roles
     */
    public void assertAuthorized(RequiresGuest a) throws AuthorizationException {

        if (getSubject().getPrincipal() != null) {
            throw new NotAGuestException();
        }
    }
}
