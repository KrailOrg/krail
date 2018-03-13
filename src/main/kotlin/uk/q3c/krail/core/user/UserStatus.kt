package uk.q3c.krail.core.user

import uk.q3c.krail.core.eventbus.Event
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.eventbus.BusMessage

/**
 * Created by David Sowerby on 06 Mar 2018
 */

data class UserHasLoggedIn @JvmOverloads constructor(override val aggregateType: String = "User", override val aggregateId: String, val knownAs: String, val source: UserStatusChangeSource) : Event

data class UserHasLoggedOut @JvmOverloads constructor(override val aggregateType: String = "User", override val aggregateId: String, val knownAs: String, val source: UserStatusChangeSource) : Event
data class UserLoginFailed @JvmOverloads constructor(override val aggregateType: String = "User", override val aggregateId: String, val label: LoginLabelKey, val description: LoginDescriptionKey) : Event
data class UserSitemapRebuilt(val loggedIn: Boolean, val source: UserStatusChangeSource) : BusMessage