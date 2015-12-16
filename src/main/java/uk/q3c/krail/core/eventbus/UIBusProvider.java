package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.bus.common.PubSubSupport;

/**
 * Created by david on 17/11/15.
 */
public interface UIBusProvider {
    PubSubSupport<BusMessage> getUIBus();
}
