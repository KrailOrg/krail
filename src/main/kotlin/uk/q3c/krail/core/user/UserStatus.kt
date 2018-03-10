package uk.q3c.krail.core.user

import uk.q3c.krail.core.eventbus.Event
import uk.q3c.krail.core.i18n.LoginDescriptionKey
import uk.q3c.krail.core.i18n.LoginKey

/**
 * Created by David Sowerby on 06 Mar 2018
 */
data class UserHasLoggedIn(override val aggregateType: String = "User", override val aggregateId: String, val knownAs: String) : Event

data class UserHasLoggedOut(override val aggregateType: String = "User", override val aggregateId: String, val knownAs: String) : Event
data class UserLoginFailed(override val aggregateType: String = "User", override val aggregateId: String, val label: LoginKey, val description: LoginDescriptionKey) : Event