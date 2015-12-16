package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;


public class DefaultGlobalBusProvider implements GlobalBusProvider {

    private PubSubSupport<BusMessage> globalBus;

    @Inject
    protected DefaultGlobalBusProvider(@GlobalBus PubSubSupport<BusMessage> globalBus) {
        this.globalBus = globalBus;
    }

    @Override
    public PubSubSupport<BusMessage> getGlobalBus() {
        return globalBus;
    }
}
