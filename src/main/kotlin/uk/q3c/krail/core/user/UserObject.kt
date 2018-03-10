package uk.q3c.krail.core.user

/**
 * A read only DAO to obtain the details of a user from a unique user id
 *
 * Created by David Sowerby on 08 Mar 2018
 */
interface UserObjectProvider {

    fun user(id: String): UserObject

}

/**
 * A placeholder implementation which simply returns a DefaultUserObject with 'knownAs' truncated from the id (assumes id is an email address)
 */
class DefaultUserObjectProvider : UserObjectProvider {
    override fun user(id: String): DefaultUserObject {
        return DefaultUserObject(id = id, knownAs = id.substringBefore("."))
    }
}


interface UserObject {
    /**
     * The unique identifier for the user, used as login name and Subject primary principal.
     */
    val id: String
    /**
     * The (usually short) name typically displayed on screen to show who is logged in.
     */
    val knownAs: String

}

data class DefaultUserObject(override val id: String, override val knownAs: String) : UserObject


