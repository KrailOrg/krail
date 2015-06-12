/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.shiro.aop;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.UserAnnotationHandler;

/**
 * An AOP MethodInterceptor to detect whether a user is a User or not.  Typical error message might be:
 * <p>
 * "Attempting to perform a user-only operation.  The current Subject is not a user (they haven't been authenticated or remembered from a previous login).
 * Access denied."
 * <p>
 * Detection logic is a copy of the native Shiro version in {@link UserAnnotationHandler}
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class UserMethodInterceptor extends ShiroMethodInterceptor<RequiresUser> {


    public UserMethodInterceptor() {
        super(RequiresUser.class, NotAUserException.class);
    }


    /**
     * Ensures that the calling <code>Subject</code> is a <em>user</em>, that is, they are <em>either</code>
     * {@link org.apache.shiro.subject.Subject#isAuthenticated() authenticated} <b><em>or</em></b> remembered via remember
     * me services before allowing access, and if not, {@link #exception()} is called
     *
     * @param a
     *         the RequiresUser annotation to check
     */
    public void assertAuthorized(RequiresUser a) {
        if (getSubject().getPrincipal() == null) {
            exception();
        }
    }
}
