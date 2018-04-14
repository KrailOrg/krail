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
package uk.q3c.krail.service.monitor

import com.google.common.collect.ImmutableList
import com.google.common.collect.MapMaker
import com.google.inject.Inject
import com.google.inject.Singleton
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.service.Service
import uk.q3c.krail.service.ServiceBusMessage
import uk.q3c.krail.service.ServiceMonitor
import uk.q3c.krail.service.ServiceStatusRecord
import uk.q3c.krail.service.State
import java.time.LocalDateTime
import javax.annotation.concurrent.ThreadSafe

/**
 * Uses the MessageBus to monitor changes to Service status
 */
@Singleton
@Listener
@ThreadSafe
class DefaultServiceMonitor @Inject constructor(@field:Transient private val messageBus: MessageBus) : ServiceMonitor {
    private val services: MutableMap<Service, ServiceStatusRecord>
    @Transient
    private var lock = Any()

    init {
        this.services = MapMaker().weakKeys().makeMap()
        messageBus.subscribe(this)
    }


    override fun stopAllServices() {
        synchronized(lock) {
            services.keys.forEach({ s -> s.stop() })
        }
    }


    /**
     * Returns a list of service being monitored.  If a service has never started, it will not be in the list
     *
     * @return a list of service being monitored.
     */
    override val monitoredServices: ImmutableList<Service>
        get() {
            synchronized(lock) {
                return ImmutableList.copyOf(services.keys)
            }
        }

    fun clear() {
        synchronized(lock) {
            services.clear()
        }
    }


    /**
     * Stores a [ServiceStatusRecord] for the last status change only
     *
     * @param busMessage describes the status change
     */

    @Handler
    @Synchronized
    fun serviceStatusChange(busMessage: ServiceBusMessage) {
        val service = busMessage.service
        val currentStatus = getServiceStatus(service) // this will be 'empty' if no previous record
        val currentTime = LocalDateTime.now()
        val lastStartTime = if (busMessage.toState == State.RUNNING) {
            currentTime  // TODO https://github.com/KrailOrg/krail-service-api/issues/2 use message timestamp instead
        } else {
            currentStatus.lastStartTime
        }
        val lastStopTime = if (busMessage.toState == State.STOPPED || busMessage.toState == State.FAILED) {
            currentTime
        } else {
            currentStatus.lastStopTime
        }

        val newStatus = ServiceStatusRecord(service = service, lastStartTime = lastStartTime, lastStopTime = lastStopTime, statusChangeTime = currentTime, currentState = busMessage.toState, previousState = busMessage.fromState)
        services[service] = newStatus

    }


    override fun getServiceStatus(service: Service): ServiceStatusRecord {
        return services.getOrDefault(service, ServiceStatusRecord(service))
    }


    companion object {

        private val log = LoggerFactory.getLogger(DefaultServiceMonitor::class.java)
    }
}