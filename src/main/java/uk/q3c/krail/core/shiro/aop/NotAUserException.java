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

import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;

/**
 * Indicates current {@link Subject} is not a User in the sense of {@link RequiresUser}.  Only used by the Shiro annotations AOP
 * <p>
 * Created by David Sowerby on 11/06/15.
 */
public class NotAUserException extends ShiroException {

    public NotAUserException() {
    }
}
