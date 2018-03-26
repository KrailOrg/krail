package uk.q3c.krail.core.shiro

import java.io.Serializable

/**
 * Created by David Sowerby on 05 Mar 2018
 */
interface JWTKeyProvider : Serializable {
    val asString: String
}
