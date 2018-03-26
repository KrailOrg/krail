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

package uk.q3c.krail.core.shiro

import com.google.inject.Inject
import com.vaadin.server.VaadinSession
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.ConcurrentAccessException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.ExcessiveAttemptsException
import org.apache.shiro.authc.ExpiredCredentialsException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.LockedAccountException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.subject.Subject
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.Event
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.user.LoginDescriptionKey
import uk.q3c.krail.core.user.LoginLabelKey
import uk.q3c.krail.core.user.UserHasLoggedIn
import uk.q3c.krail.core.user.UserHasLoggedOut
import uk.q3c.krail.core.user.UserLoginFailed
import uk.q3c.krail.core.user.UserQueryDao
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream

/**
 * A [Subject] is not stored directly in a [VaadinSession] as it is not Serializable, but rather a JWT representation of it.
 * The JWT is retrieved by a subsequent call to [get], and a new Subject instance created by [Subject.Builder] from data in the JWT
 *
 *
 *
 * Created by David Sowerby on 10/06/15.
 */
class DefaultSubjectProvider @Inject constructor(

        /**
         * The security manager for the application.
         */
        private val serializationSupport: SerializationSupport,
        @Transient private var securityManager: SecurityManager,
        @Transient private val eventBusProvider: SessionBusProvider,
        private val userQueryDao: UserQueryDao,
        private val jwtProvider: JWTProvider<KrailJWTBody>)


    : SubjectProvider {


    private val log = LoggerFactory.getLogger(this.javaClass.name)


    /**
     * Sets the security manager for the application. To support push, normally a
     * [DefaultSecurityManager] is used rather than a web specific one
     * because the normal HTTP request/response cycle isn't used.
     *
     * @param securityManager the security manager to set
     */
    fun setSecurityManager(securityManager: SecurityManager) {
        this.securityManager = securityManager
    }

    /**
     * Returns the subject for the application and thread which represents the current user.
     *
     * If there is an entry in the session (a JWT) for the subject attribute, the Subject is
     * reconstructed from that information, otherwise an anonymous Subject is returned
     *
     * @return the subject for the current application and thread
     *
     * @see SecurityUtils.getSubject
     */
    override fun get(): Subject {
        val builder = Subject.Builder(securityManager)
        val subject: Subject
        if (sessionContainsSubjectJWT()) {
            val tokenObject = getJWTFromSession()
            val principals = SimplePrincipalCollection()
            val principal = tokenObject.subject
            val realm: String = tokenObject.realmName
            principals.add(principal, realm)
            builder.principals(principals)
            builder.authenticated(true) // JWT exists, treat as authenticated
        } else {
            builder.authenticated(false) // probably not necessary, but safer
        }
        subject = builder.buildSubject()
        return subject
    }


    override fun login(source: UserStatusChangeSource, token: UsernamePasswordToken): Subject {
        val subject = get()
        try {
            subject.login(token)
            val knownAs = userQueryDao.user(token.username).knownAs
            storeInSession(subject)
            publishStatusChangeMessage(loggedIn = true, primaryPrincipal = token.username, name = knownAs, source = source)

        } catch (uae: AuthenticationException) {
            val username = token.username ?: ""
            publishExceptionEvent(username, uae)
        }
        return subject
    }

    private fun vaadinSession(): VaadinSession {
        val session = VaadinSession.getCurrent()
                ?: throw IllegalStateException("Unable to locate VaadinSession to store Shiro Subject.")
        return session
    }

    private fun publishStatusChangeMessage(loggedIn: Boolean, primaryPrincipal: String, name: String, source: UserStatusChangeSource) {
        val message: Event =
                if (loggedIn) {
                    UserHasLoggedIn(aggregateId = primaryPrincipal, knownAs = name, source = source)
                } else {
                    UserHasLoggedOut(aggregateId = primaryPrincipal, knownAs = name, source = source)
                }
        log.debug("Publishing user status message ${message.javaClass.simpleName} from ${this.javaClass
                .simpleName}")
        eventBusProvider.get().publish(message)
    }

    private fun publishExceptionEvent(username: String, exception: AuthenticationException) {
        val errorKey: LoginLabelKey
        val errorDescriptionKey: LoginDescriptionKey


        when (exception) {
            is UnknownAccountException -> {
                errorKey = LoginLabelKey.Unknown_Account
                errorDescriptionKey = LoginDescriptionKey.Unknown_Account
            }

            is IncorrectCredentialsException -> {
                errorKey = LoginLabelKey.Unknown_Account
                errorDescriptionKey = LoginDescriptionKey.Unknown_Account
            }
            is ExpiredCredentialsException -> {
                errorKey = LoginLabelKey.Account_Expired
                errorDescriptionKey = LoginDescriptionKey.Account_Expired
            }
            is LockedAccountException -> {
                errorKey = LoginLabelKey.Account_Locked
                errorDescriptionKey = LoginDescriptionKey.Account_Locked
            }
            is ExcessiveAttemptsException -> {
                errorKey = LoginLabelKey.Too_Many_Login_Attempts
                errorDescriptionKey = LoginDescriptionKey.Too_Many_Login_Attempts
            }
            is DisabledAccountException -> {
                errorKey = LoginLabelKey.Account_is_Disabled
                errorDescriptionKey = LoginDescriptionKey.Account_is_Disabled
            }
            is ConcurrentAccessException -> {
                errorKey = LoginLabelKey.Account_Already_In_Use
                errorDescriptionKey = LoginDescriptionKey.Account_Already_In_Use
            }
            else -> {
                errorKey = LoginLabelKey.Authentication_Failed
                errorDescriptionKey = LoginDescriptionKey.Authentication_Failed
            }

        }
        log.debug("Mapping login exception ${exception.javaClass.simpleName} to enum identifier $errorKey")
        val message = UserLoginFailed(label = errorKey, description = errorDescriptionKey, aggregateId = username)
        eventBusProvider.get().publish(message)

    }

    private fun storeInSession(subject: Subject): String {
        val session = vaadinSession()

        val jwt = jwtProvider.encodeToHeadlessJWT(subject)
        try {
            session.lockInstance.lock()
            session.setAttribute(SUBJECT_ATTRIBUTE, jwt)
        } finally {
            session.lockInstance.unlock()
        }
        return jwt
    }


    override fun logout(source: UserStatusChangeSource) {
        if (sessionContainsSubjectJWT()) {
            val jwtBody = getJWTFromSession() // we want the info before removing the JWT
            val subject = get()
            subject.logout()
            val session = vaadinSession()
            try {
                session.lockInstance.lock()
                session.setAttribute(SUBJECT_ATTRIBUTE, null)
            } finally {
                session.lockInstance.unlock()
            }
            publishStatusChangeMessage(loggedIn = false, primaryPrincipal = jwtBody.subject, name = jwtBody.knownAs, source = source)
        } else {
            log.warn("User attempted to log out when not logged in.  Should not matter but may indicate a logic problem")
        }


    }

    /**
     * Precede with call to [sessionContainsSubjectJWT] to ensure JWT exists
     */
    private fun getJWTFromSession(): KrailJWTBody {
        val session = vaadinSession()
        val jwtToken = session.getAttribute(SUBJECT_ATTRIBUTE) as String
        return jwtProvider.headlessTokenAsObject(jwtToken)

    }

    private fun sessionContainsSubjectJWT(): Boolean {
        val session = vaadinSession()
        return session.getAttribute(SUBJECT_ATTRIBUTE) != null
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        serializationSupport.deserialize(this, inputStream)
    }
}


