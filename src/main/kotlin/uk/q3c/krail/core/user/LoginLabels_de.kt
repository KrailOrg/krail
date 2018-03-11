/*
 *
 *  * Copyright (c) 2018. David Sowerby
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


class LoginLabels_de : LoginLabels() {


    override fun loadMap() {


        put(LoginLabelKey.Log_In, "Einloggen")
        put(LoginLabelKey.Log_Out, "Ausloggen")
        put(LoginLabelKey.User_Name, "Benutzername")
        put(LoginLabelKey.Password, "Passwort")
        put(LoginLabelKey.Submit, "Absenden")


    }
}
