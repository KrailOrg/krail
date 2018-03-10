/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.i18n

import uk.q3c.krail.core.i18n.LoginDescriptionKey.*
import uk.q3c.krail.i18n.EnumResourceBundle

/**
 * The base for the resource bundle of [LoginDescriptions]
 *
 * @author David Sowerby 9 Mar 2018
 */
open class LoginDescriptions : EnumResourceBundle<LoginDescriptionKey>() {

    override fun loadMap() {
        put(Unknown_Account, "That username or password was not recognised")
        put(Account_Expired, "Your account has expired")
        put(Account_Already_In_Use, "This account is already in use.  You must log out of " + "that " +
                "session before you can log in again.")
        put(Account_Locked, "Your account is locked")
        put(Too_Many_Login_Attempts, "Login has failed too many times, the account will " + "need to " +
                "be reset")
        put(Authentication_Failed, "Your login attempt failed")
    }


}
