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

package uk.q3c.krail.core.eventbus

import com.google.inject.Provider
import com.google.inject.spi.InjectionListener
import net.engio.mbassy.bus.common.PubSubSupport
import net.engio.mbassy.listener.Listener
import uk.q3c.krail.eventbus.BusMessage
import uk.q3c.krail.eventbus.EventBusAutoSubscriber
import uk.q3c.krail.eventbus.SubscribeTo
import kotlin.reflect.KClass

/**
 * Provides logic for automatically subscribing to event buses.  This is used as an [InjectionListener], and cannot therefore use injection in its
 * constructor
 *
 *
 * Created by David Sowerby on 13/03/15.
 */
class VaadinEventBusAutoSubscriber(private val uiBusProvider: Provider<PubSubSupport<BusMessage>>, private val sessionBusProvider: Provider<PubSubSupport<BusMessage>>) : EventBusAutoSubscriber {

    /**
     * Invoked by Guice after it injects the fields and methods of instance.  `injectee` must have a [Listener] annotation in order to get this
     * far (the matcher will only select those which have).
     *
     *
     * See [SubscribeTo] annotation for expected behaviour
     *
     * @param injectee
     * instance that Guice injected dependencies into
     */


    override fun afterInjection(injectee: Any) {
        val clazz = injectee.javaClass
        val subscribeTo = clazz.getAnnotation(SubscribeTo::class.java)
        var subscriptions: MutableList<KClass<out Annotation>>
        if (subscribeTo == null) { //default behaviour
            subscriptions = mutableListOf()
        } else { //defined by SubscribeTo
            subscriptions = subscribeTo.value.toMutableList()
        }

        // just ignore any unrecognised annotations, they may be handled by another InjectionListener
        for (target in subscriptions) {
            when (target) {
                SessionBus::class -> sessionBusProvider.get().subscribe(injectee)
                UIBus::class -> uiBusProvider.get().subscribe(injectee)
            }

        }

    }
}
