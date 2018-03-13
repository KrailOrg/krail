package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.listener.Handler;
import uk.q3c.krail.eventbus.BusMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Sowerby on 13 Mar 2018
 */
public class SessionBusListener {


    private List<BusMessage> messages = new ArrayList<>();
    private List<Class<? extends BusMessage>> messagesClasses = new ArrayList<>();


    @Handler
    public void messageHandler(BusMessage busMessage) {
        System.out.println("bus listener received a " + busMessage.getClass().getName());
        messages.add(busMessage);
        messagesClasses.add(busMessage.getClass());
    }

    public void clear() {
        messages.clear();
    }

    public boolean contains(BusMessage busMessage) {
        return messages.contains(busMessage);
    }

    public boolean containsInstanceOf(Class<? extends BusMessage> busMessageClass) {
        return messagesClasses.contains(busMessageClass);
    }

}
