package uk.q3c.krail.core.monitor

import uk.q3c.krail.core.guice.uiscope.UIKey
import uk.q3c.krail.eventbus.BusMessage
import java.time.OffsetDateTime

/**
 * Created by David Sowerby on 01 Jun 2018
 */
class PageReadyMessage(val uiKey: UIKey, val uiNumber: Int) : TimedMessage()

class PageLoadingMessage(val uiKey: UIKey, val uiNumber: Int) : TimedMessage()


// TODO this should move to eventbus-api https://github.com/KrailOrg/eventbus-api/issues/6
abstract class TimedMessage @JvmOverloads constructor(val timestamp: OffsetDateTime = OffsetDateTime.now()) : BusMessage