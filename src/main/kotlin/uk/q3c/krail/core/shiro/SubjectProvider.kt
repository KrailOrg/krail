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
package uk.q3c.krail.core.shiro

import com.google.inject.Provider
import com.vaadin.server.VaadinSession
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.subject.Subject
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import java.io.Serializable

/**
 * Use this instead of using [SecurityUtils.getSubject] - that will fail because of various issues around threading and session management.
 *
 * Login and Logout should also be performed using this class
 *
 *
 * This is actually a re-badged "VaadinSecurityContext" referred to in Mike's blog (see below)
 *
 *
 * With thanks to Mike Pilone http://mikepilone.blogspot.co.uk/2013/07/vaadin-shiro-and-push.html
 *
 * @author mpilone
 * @author David Sowerby 15 Jul 2013
 */
interface SubjectProvider : Provider<Subject>, Serializable {

    /**
     * Attempt to login
     *
     * @param source the source of the login request (see [UserStatusChangeSource])
     * @param token user name and password
     * @throws Exception as described by [Subject.login]
     */
    fun login(source: UserStatusChangeSource, token: UsernamePasswordToken): Subject

    /**
     * Logs out the current user
     */
    fun logout(source: UserStatusChangeSource)


}

/**
 * The attribute name used in the [VaadinSession] to store the [Subject]s information.
 */
const val SUBJECT_ATTRIBUTE = "SubjectProvider.subject"
