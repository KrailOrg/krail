package uk.q3c.krail.core.shiro

import com.google.common.collect.ImmutableList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.vaadin.server.VaadinSession
import net.engio.mbassy.bus.common.PubSubSupport
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.realm.Realm
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockito.InOrder
import org.mockito.Mockito
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.user.DefaultUserQueryDao
import uk.q3c.krail.core.user.UserHasLoggedIn
import uk.q3c.krail.core.user.UserHasLoggedOut
import uk.q3c.krail.core.user.UserLoginFailed
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.eventbus.BusMessage
import uk.q3c.util.guice.SerializationSupport
import java.util.*
import java.util.concurrent.locks.Lock

/**
 * Created by David Sowerby on 07 Mar 2018
 */
object DefaultSubjectProviderTest : Spek({

    given("a SubjectProvider") {
        val jwtProvider = DefaultJWTProvider(DefaultJWTKeyProvider(), DefaultUserQueryDao())
        lateinit var realm: MockRealm
        lateinit var securityManager: KrailSecurityManager
        lateinit var eventBusProvider: SessionBusProvider
        lateinit var eventBus: PubSubSupport<BusMessage>
        lateinit var subjectProvider: SubjectProvider
        lateinit var session: VaadinSession
        lateinit var lock: Lock
        lateinit var source: UserStatusChangeSource
        lateinit var serializationSupport: SerializationSupport

        beforeEachTest {
            serializationSupport = mock()
            realm = MockRealm()
            securityManager = KrailSecurityManager(ImmutableList.of(realm) as Collection<Realm>?, Optional.empty())
            eventBusProvider = mock()
            eventBus = mock()

            subjectProvider = DefaultSubjectProvider(serializationSupport, securityManager, eventBusProvider, DefaultUserQueryDao(), jwtProvider)
            session = mock()
            lock = mock()
            source = mock()

            VaadinSession.setCurrent(session)
            whenever(eventBusProvider.get()).thenReturn(eventBus)
            whenever(session.lockInstance).thenReturn(lock)

        }


        on("get when there is no subject in the session") {
            val subject = subjectProvider.get()

            it("returns an anonymous subject") {
                subject.principal.shouldBeNull()
            }

            it("does not store JWT in session, as user not authenticated") {
                verify(session, never()).setAttribute(any(), any())
            }

            it("returns a subject which is not authenticated") {
                subject.isAuthenticated.shouldBeFalse()
            }

        }

        on("get when there is a valid subject store in the session") {

            whenever(session.getAttribute(SUBJECT_ATTRIBUTE)).thenReturn(validHeadlessJWT())
            val subject = subjectProvider.get()

            it("returns a Subject with correct principal") {
                subject.principal.shouldEqual("david")
            }

            it("returns a Subject as authenticated") {
                subject.isAuthenticated.shouldBeTrue()
            }


        }

        on("successful log in") {
            SecurityUtils.setSecurityManager(securityManager)
            val authenticationToken = UsernamePasswordToken("david", "password")
            val subject = subjectProvider.login(source, authenticationToken)
            it("sends a UserHasLoggedIn event") {
                verify(eventBus).publish(any<UserHasLoggedIn>())
            }

            it("stores a JWT in the session") {
                val orderVerifier: InOrder = Mockito.inOrder(lock, session, lock)
                orderVerifier.verify(lock).lock()
                orderVerifier.verify(session).setAttribute(SUBJECT_ATTRIBUTE, jwtProvider.encodeToHeadlessJWT(subject))
                orderVerifier.verify(lock).unlock()
            }
        }

        on("unsuccessful login attempt") {
            val authenticationToken = UsernamePasswordToken("david", "rubbish")
            val subject = subjectProvider.login(source, authenticationToken)

            it("sends a UserLoginFailed event with identified cause") {

                argumentCaptor<BusMessage>().apply {
                    verify(eventBus).publish(capture())
                    (firstValue as UserLoginFailed).aggregateId.shouldBe("david")
                }
            }

            it("returns an anonymous subject") {
                subject.principal.shouldBeNull()
            }
        }

        on("logout from being logged in") {
            whenever(session.getAttribute(SUBJECT_ATTRIBUTE)).thenReturn(validHeadlessJWT())
            subjectProvider.logout(source)

            it("sends a UserHasLoggedOut event") {
                verify(eventBus).publish(any<UserHasLoggedOut>())
            }

            it("removes the JWT from the session") {
                val orderVerifier: InOrder = Mockito.inOrder(lock, session, lock)
                orderVerifier.verify(lock).lock()
                orderVerifier.verify(session).setAttribute(SUBJECT_ATTRIBUTE, null)
                orderVerifier.verify(lock).unlock()
            }
        }

    }
})

class MockRealm : AuthorizingRealm() {
    var account: SimpleAccount

    init {
        val principalCollection = SimplePrincipalCollection()
        principalCollection.add("david", "defaultRealm")

        account = SimpleAccount(principalCollection, "password")
    }

    override fun doGetAuthenticationInfo(token: AuthenticationToken?): AuthenticationInfo? {
        val up = token as UsernamePasswordToken
        if (up.password.contentEquals("password".toCharArray())) {
            return account
        } else {
            return null
        }
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection?): AuthorizationInfo {
        return account
    }

}

fun validHeadlessJWT(): String {
    return "eyJzdWIiOiJkYXZpZCJ9.hUsNuHefSBZw_BvB8Ht7NzxtxFf0xuSx76jMqcxPx2TZP5iDKbPSBDhiDmGsPNcIlEOi5-Gi7MNSTGnvBClC6g"
}