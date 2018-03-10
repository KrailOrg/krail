package uk.q3c.krail.core.shiro

import com.google.inject.Inject
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.shiro.subject.Subject
import uk.q3c.krail.core.user.UserObjectProvider

/**
 * Default implementation for encoding / decoding JWTs to and from [Subject] instances.  Uses the io.jsonwebtoken library
 * for token construction and verification
 *
 * Does not support multiple Shiro Realms (when transferring back from JWT, Shiro Principal has to have a realm entry)
 *
 *
 * Created by David Sowerby on 05 Mar 2018
 */
class DefaultJWTProvider @Inject
constructor(private val jwtKeyProvider: JWTKeyProvider, private val userObjectProvider: UserObjectProvider) : JWTProvider<KrailJWTBody> {

    override fun encodeToHeadlessJWT(subject: Subject): String {

        val primaryPrincipal: String = subject.principals.primaryPrincipal.toString()
        val realmName: String = if (subject.principals.realmNames.size == 1) {
            subject.principals.realmNames.elementAt(0)
        } else {
            throw InvalidRealmException(subject.principals.realmNames.toTypedArray().contentToString())
        }
        val token = Jwts.builder()
                .setSubject(primaryPrincipal)
                .claim(JWTAttribute.knownAs.name, userObjectProvider.user(primaryPrincipal).knownAs)
                .claim(JWTAttribute.realmName.name, realmName)  // TODO this assumes a single Shiro Realm for the application
                .signWith(SignatureAlgorithm.HS512, jwtKeyProvider.asString)
                .compact()
        val headlessToken = token.substringAfter(".")
        return headlessToken
    }


    override fun verifyHeadlessToken(headlessToken: String) {
        Jwts.parser().setSigningKey(jwtKeyProvider.asString).parseClaimsJws(prependHeader(headlessToken))
        confirmSource(headlessToken)
    }


    override fun confirmSource(headlessToken: String) {
        // do nothing by default
    }

    private fun tokenAsObject(fullToken: String): Jws<Claims> {
        return Jwts.parser().setSigningKey(jwtKeyProvider.asString).parseClaimsJws(fullToken)
    }

    override fun headlessTokenAsObject(headlessToken: String): KrailJWTBody {
        verifyHeadlessToken(headlessToken)
        val jws = tokenAsObject(prependHeader(headlessToken))
        val body = KrailJWTBody(
                subject = jws.body.subject,
                knownAs = jws.body[JWTAttribute.knownAs.name].toString(),
                realmName = jws.body[JWTAttribute.realmName.name].toString())
        return body
    }

    private fun prependHeader(headlessToken: String): String {
        return "eyJhbGciOiJIUzUxMiJ9.$headlessToken"
    }

}

class InvalidRealmException(msg: String) : RuntimeException("Must contain exactly one Realm but contains $msg")

enum class JWTAttribute {
    knownAs, realmName
}


interface JWTBody

data class KrailJWTBody(val subject: String, val knownAs: String, val realmName: String) : JWTBody

