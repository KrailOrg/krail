package uk.q3c.krail.core.shiro

import com.google.common.collect.ImmutableSet
import com.nhaarman.mockito_kotlin.whenever
import io.jsonwebtoken.MalformedJwtException
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.Subject
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.user.DefaultUserQueryDao


/**
 * Created by David Sowerby on 07 Mar 2018
 */
object DefaultJWTProviderTest : Spek({
    val headlessToken = "eyJzdWIiOiJkYXZpZCIsImtub3duQXMiOiJkYXZpZCIsInJlYWxtTmFtZSI6ImRlZmF1bHRSZWFsbSJ9.QKkeO1w4HwGXLRuTxofDlEp7PsH6N8nYyhak7P0SKnn-OuvG8OTuuFne0bhAmMuN3dY3iOHNvHXzP4uMxr6sQA"
    val fullToken = "eyJhbGciOiJIUzUxMiJ9.$headlessToken"


    given("a JWT Provider") {
        val jwtKeyProvider = DefaultJWTKeyProvider()
        val provider = DefaultJWTProvider(jwtKeyProvider, DefaultUserQueryDao())

        lateinit var validSubject: Subject
        lateinit var principals: PrincipalCollection

        beforeEachTest {
            validSubject = mock()
            principals = mock()
            whenever(validSubject.principals).thenReturn(principals)
            whenever(principals.primaryPrincipal).thenReturn("david")
            whenever(principals.realmNames).thenReturn(ImmutableSet.of("defaultRealm"))

        }


        on("requesting a token from a valid Subject") {
            val token = provider.encodeToHeadlessJWT(validSubject)
            println(token)
            val tokenSegments = token.count { c -> c == ".".toCharArray()[0] }

            it("should create a headless token") {
                tokenSegments.shouldEqual(1)
                token.shouldEqual(headlessToken)
            }
        }

        on("verifying a correct headless token, header is correctly replaced") {
            provider.verifyHeadlessToken(headlessToken)
            it("does not throw exception") { }
        }

        on("verifying a token which already has a header, fails") {
            val verify = { provider.verifyHeadlessToken(fullToken) }
            it("fails") {
                verify.shouldThrow(MalformedJwtException::class)
            }
        }

        on("returning an object representation of headless JWT ") {
            val jwtBody = provider.headlessTokenAsObject(headlessToken)

            it("should contain all the correct attributes") {
                jwtBody.subject.shouldEqual("david")
                jwtBody.knownAs.shouldEqual("david")
                jwtBody.realmName.shouldEqual("defaultRealm")
            }
        }
    }

})