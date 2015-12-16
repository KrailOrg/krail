package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;


public class DefaultUIBusProvider implements UIBusProvider {

    private PubSubSupport<BusMessage> uiBus;

    @Inject
    protected DefaultUIBusProvider(@UIBus PubSubSupport<BusMessage> uiBus) {
        this.uiBus = uiBus;
    }

    @Override
    public PubSubSupport<BusMessage> getUIBus() {
        return uiBus;
    }
}
