package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.bus.common.PubSubSupport;
import uk.q3c.krail.eventbus.BusMessage;

import java.io.Serializable;

/**
 * Created by David Sowerby on 08 Jan 2018
 */
public interface BusProvider extends Serializable {
    PubSubSupport<BusMessage> get();
}
