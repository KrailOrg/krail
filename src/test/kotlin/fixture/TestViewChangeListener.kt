package fixture

import com.google.inject.Singleton
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import uk.q3c.krail.core.eventbus.UIBus
import uk.q3c.krail.core.view.BeforeViewChangeBusMessage
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.eventbus.SubscribeTo
import java.util.*

/**
 * Created by David Sowerby on 29 Apr 2018
 */
@Singleton
@Listener
@SubscribeTo(UIBus::class)
class TestViewChangeListener {

    var calls: MutableMap<String, Any?> = LinkedHashMap()

    fun getCalls(): Set<String> {
        return calls.keys
    }

    @Handler
    fun beforeViewChange(msg: BeforeViewChangeBusMessage) {
        calls["beforeViewChange"] = msg
    }

    /**
     * Invoked after the view is changed. If a `beforeViewChange`
     * method blocked the view change, this method is not called. Be careful of
     * unbounded recursion if you decide to change the view again in the
     * listener.
     *
     * @param navStateExt view change event
     */
    @Handler
    fun afterViewChange(msg: AfterViewChangeBusMessage) {
        calls["afterViewChange"] = msg
    }

    fun addCall(call: String, navStateExt: NavigationStateExt?) {
        calls[call] = navStateExt
    }


    fun clear() {
        calls.clear()
    }
}
