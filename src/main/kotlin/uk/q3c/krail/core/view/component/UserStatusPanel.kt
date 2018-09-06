/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component

import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.Label
import java.io.Serializable


/**
 * Provides a component representing the user's current login status, and a button to log in / log out as appropriate
 * Responds to changes caused by the user logging and out
 *
 *
 */
interface UserStatusPanel : Component {
    val usernameLabel: Label
    val login_logout_Button: Button

    val actionLabel: String
    val userId: String
}

/**
 *
 * Provides a component representing the user's current login status, and a button to log in / log out as appropriate
 * Responds to changes caused by the user logging and out
 *
 * Replaces, and is functionally the same as [UserStatusPanel], except that this is not a component.  Rather than create
 * a layout for the two components (which may be wasted effort), this approach assumes that the constituent components
 * will be put wherever the developer wants them.  The implementation maintains the relationship between those components
 */
interface UserStatusComponents : Serializable {
    val usernameLabel: Label
    val login_logout_Button: Button

    val actionLabel: String
    val userId: String
}
