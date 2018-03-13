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
package uk.q3c.krail.core.user

import com.google.inject.AbstractModule
import uk.q3c.krail.core.user.notify.DefaultUserNotifier
import uk.q3c.krail.core.user.notify.DefaultVaadinNotification
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.user.notify.VaadinNotification

class UserModule : AbstractModule() {

    override fun configure() {
        bindUserNotifier()
        bindVaadinNotification()
        bindUserObjectProvider()
    }

    private fun bindUserObjectProvider() {
        bind(UserQueryDao::class.java).to(DefaultUserQueryDao::class.java)
    }


    protected fun bindVaadinNotification() {
        bind(VaadinNotification::class.java).to(DefaultVaadinNotification::class.java)
    }


    /**
     * Override this method to bind your own UserNotifier implementation
     */
    protected fun bindUserNotifier() {
        bind(UserNotifier::class.java).to(DefaultUserNotifier::class.java)
    }


}
