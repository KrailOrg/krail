package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;


public class DefaultSessionBusProvider implements SessionBusProvider {

    private PubSubSupport<BusMessage> sessionBus;

    @Inject
    protected DefaultSessionBusProvider(@SessionBus PubSubSupport<BusMessage> sessionBus) {
        this.sessionBus = sessionBus;
    }

    @Override
    public PubSubSupport<BusMessage> getSessionBus() {
        return sessionBus;
    }
}
