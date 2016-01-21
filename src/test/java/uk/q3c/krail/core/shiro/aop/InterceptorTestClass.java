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

import org.apache.shiro.authz.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by David Sowerby on 19 Jan 2016
 */
public class InterceptorTestClass {

    @RequiresRoles({"a", "b"})
    public void requiresRoles() {

    }

    @RequiresRoles("a")
    public void requiresRole() {

    }

    @RequiresRoles(value = {"a", "b"}, logical = Logical.OR)
    public void requiresRolesOr() {

    }

    @RequiresPermissions({"a", "b"})
    public void requiresPermissions() {

    }

    @RequiresPermissions("a")
    public void requiresPermission() {

    }

    @RequiresPermissions(value = {"a", "b"}, logical = Logical.OR)
    public void requiresPermissionsOr() {

    }

    @RequiresAuthentication
    public void requiresAuthentication() {

    }

    @RequiresGuest
    public void requiresGuest() {

    }

    @RequiresUser
    public void requiresUser() {

    }

    public Annotation getAnnotation(String methodName) {
        Method[] methods = this.getClass()
                               .getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName()
                          .equals(methodName)) {
                return methods[i].getDeclaredAnnotations()[0];
            }
        }
        throw new RuntimeException("test set up problem, method name not recognised");
    }


}
