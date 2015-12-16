package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.bus.common.PubSubSupport;

/**
 * Equivalent to injecting PubSubSupport with @GlobalBus annotation.  Used to prevent accidental overloading with wrong
 * bus.
 * <p>
 * Created by David Sowerby on 17/11/15.
 */
public interface GlobalBusProvider {
    PubSubSupport<BusMessage> getGlobalBus();
}
