package uk.q3c.krail.core.shiro

/**
 * Created by David Sowerby on 05 Mar 2018
 */
class DefaultJWTKeyProvider : JWTKeyProvider {
    override val asString: String
        get() = "this should be a base64EncodedSecretKey"
}
