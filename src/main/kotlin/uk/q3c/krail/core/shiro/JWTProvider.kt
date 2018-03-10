package uk.q3c.krail.core.shiro

import io.jsonwebtoken.SignatureException
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.subject.Subject

/**
 * Converts a Shiro [Subject] to a Json Web Token (JWT), and provides for verifying a JWT.
 * A "Headless" JWT approach is recommended but is implementation dependent.
 *
 *
 * The recommended approach follows the principles laid out in https://neilmadden.wordpress.com/2017/03/15/should-you-use-jwt-jose/ , and specifically:
 *
 * "If you followed my advice of using “headless” JWTs and direct authenticated encryption with a symmetric key,
 * you’d end up not far off from the advice of just encrypting a JSON object with libsodium or using Fernet."
 *
 * Note that Key management is the responsibility of the implementation of [JWTKeyProvider], and is left to the Krail developer
 * as the implementation will need to be environment specific.
 *
 * reference: https://dev.to/neilmadden/7-best-practices-for-json-web-tokens
 *
 * Created by David Sowerby on 05 Mar 2018
 */
interface JWTProvider<T : JWTBody> {

    /**
     * Encodes a JWT from `subject`.  A JWT should be returned only if the subject has authenticated.
     * RememberMe is not currently supported
     *
     * @param subject The Shiro Subject to encode
     * @return a JWT representing the Shiro subject
     * @throws AuthenticationException if subject has not been authenticated
     */
    fun encodeToHeadlessJWT(subject: Subject): String

    /**
     * Verifies the signature of a headless JWT. Also calls [confirmSource]
     *
     * @param headlessToken the token to be verified
     * @throws SignatureException if verification fails
     */
    fun verifyHeadlessToken(headlessToken: String)

    /**
     * Confirms that the token was issued by this application.  Implementations may simply do nothing if this check is
     * considered superfluous, as it will require a lookup from persistence to be reliable (and therefore potentially reduce performance)
     */
    fun confirmSource(headlessToken: String)


    /**
     * Returns an implementation specific object representation of a headless JWT token. Implementations should call [verify]
     * before converting to the object representation
     *
     * @param headlessToken token String to parse to object
     * @return  implementation specific object representation
     */
    fun headlessTokenAsObject(headlessToken: String): T
}
