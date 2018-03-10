package uk.q3c.krail.core.eventbus

import uk.q3c.krail.eventbus.BusMessage

/**
 *
 * Common base interfaces for Commands and Events (CQRS style)
 *
 * Created by David Sowerby on 06 Mar 2018
 */
interface Command

interface Event : BusMessage {
    val aggregateType: String
    val aggregateId: String
}


interface ExceptionEvent