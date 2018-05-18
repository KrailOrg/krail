/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.push

import com.google.inject.AbstractModule

/**
 * Created by David Sowerby on 27/05/15.
 */
class PushModule : AbstractModule() {

    /**
     * {@inheritDoc}
     */
    override fun configure() {
        bindPushRouter()
        bindBroadcaster()
        bindPushConfiguration()
    }

    private fun bindPushConfiguration() {
        bind(KrailPushConfiguration::class.java).to(DefaultKrailPushConfiguration::class.java)
    }


    protected fun bindPushRouter() {
        bind(PushMessageRouter::class.java).to(DefaultPushMessageRouter::class.java)
    }

    protected fun bindBroadcaster() {
        bind(Broadcaster::class.java).to(DefaultBroadcaster::class.java)
    }


}

const val SERVER_PUSH_ENABLED = "server.push.enabled"

