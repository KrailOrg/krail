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
package uk.q3c.krail.core.user

class LoginDescriptions_de : LoginDescriptions() {
    override fun loadMap() {

        put(LoginDescriptionKey.Account_Already_In_Use, "Dieses Konto ist bereits in Verwendung. Sie müssen sich" + " " +
                "ausloggen bevor Sie sich wieder einloggen können.")
        put(LoginDescriptionKey.Account_is_Disabled, "Das Konto ist deaktiviert")
        put(LoginDescriptionKey.Account_Expired, "Ihr Konto ist abgelaufen")
        put(LoginDescriptionKey.Account_Locked, "Ihr Konto ist gesperrt")

        put(LoginDescriptionKey.Enter_your_user_name, "Geben Sie ihren Benutzernamen ein")
        put(LoginDescriptionKey.Invalid_Login, "ungültiger Login")
        put(LoginDescriptionKey.Please_log_in, "Bitte loggen Sie sich ein")
        put(LoginDescriptionKey.Too_Many_Login_Attempts, "zuviele Login-Versuche. Diese Konto muss entsperrt werden!")
        put(LoginDescriptionKey.Unknown_Account, "Ihr Benutzername und Passwort ist unbekannt")
        put(LoginDescriptionKey.You_have_not_logged_in, "Sie sind nicht eingeloggt")


    }
}