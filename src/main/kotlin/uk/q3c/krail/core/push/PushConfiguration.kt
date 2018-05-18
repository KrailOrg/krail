package uk.q3c.krail.core.push

import com.github.mcollovati.vertx.vaadin.communication.SockJSPushConnection
import com.google.inject.Inject
import com.vaadin.server.communication.AtmospherePushConnection
import com.vaadin.server.communication.PushConnection
import com.vaadin.shared.communication.PushMode
import com.vaadin.shared.ui.ui.Transport
import com.vaadin.shared.ui.ui.UIState
import com.vaadin.ui.PushConfiguration
import com.vaadin.ui.UIDetachedException
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.RuntimeEnvironment.SERVLET
import uk.q3c.krail.core.env.RuntimeEnvironment.VERTX
import uk.q3c.krail.core.ui.ScopedUI
import java.util.*

/**
 *  See the [Developer Guide](http://krail.readthedocs.io/en/develop/devguide/devguide-push.html) for the reasons for these classes
 *
 * This code is mostly a copy of [PushConfigurationImpl] from Vaadin core at 8.3.3  The changes are:
 *
 * - The [ui] property is set directly instead of through the constructor (to allow injection)
 * - Migration to Kotlin (which required a couple of tweaks for null handling)
 * - use of [RuntimeEnvironment]
 * - revised [setPushMode] to call [createConnection]
 * - This implementation is public
 *
 * Created by David Sowerby on 17 May 2018
 */
class DefaultKrailPushConfiguration @Inject constructor(@RunningOn private val runtimeEnvironment: RuntimeEnvironment) : KrailPushConfiguration {

    override lateinit var ui: ScopedUI
    private val state: UIState.PushConfigurationState
        get() = ui.state.pushConfiguration

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#getPushMode()
     */
    override fun getPushMode(): PushMode {
        return getState(false).mode
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#setPushMode(com.vaadin.shared.
     * communication .PushMode)
     */
    override fun setPushMode(pushMode: PushMode) {
        val session = ui.session ?: throw UIDetachedException(
                "Cannot set the push mode for a detached UI")

        assert(session.hasLock())

        if (pushMode.isEnabled && !session.service.ensurePushAvailable()) {
            throw IllegalStateException(
                    "Push is not available. See previous log messages for more information.")
        }

        val oldMode = state.mode
        if (oldMode != pushMode) {
            state.mode = pushMode

            if (!oldMode.isEnabled && pushMode.isEnabled) {
                // The push connection is initially in a disconnected state;
                // the client will establish the connection
                ui.pushConnection = createConnection() // This is the only real change from the Vaadin native PushConfigurationImpl
            }
            // Nothing to do here if disabling push;
            // the client will close the connection
        }
    }

    private fun createConnection(): PushConnection {
        return when (runtimeEnvironment) {
            SERVLET -> AtmospherePushConnection(ui)
            VERTX -> SockJSPushConnection(ui)
        }
    }

    override fun setPushUrl(pushUrl: String) {
        state.pushUrl = pushUrl
    }

    override fun getPushUrl(): String {
        return getState(false).pushUrl
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#getTransport()
     */
    override fun getTransport(): Transport? {
        try {
            val tr = Transport.getByIdentifier(
                    getParameter(UIState.PushConfigurationState.TRANSPORT_PARAM))
            return if (tr == Transport.WEBSOCKET && getState(false).alwaysUseXhrForServerRequests) {
                Transport.WEBSOCKET_XHR
            } else {
                tr
            }
        } catch (e: IllegalArgumentException) {
            return null
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.ui.PushConfiguration#setTransport(com.vaadin.shared.ui.ui.
     * Transport)
     */
    override fun setTransport(transport: Transport) {
        if (transport == Transport.WEBSOCKET_XHR) {
            state.alwaysUseXhrForServerRequests = true
            // Atmosphere knows only about "websocket"
            setParameter(UIState.PushConfigurationState.TRANSPORT_PARAM,
                    Transport.WEBSOCKET.identifier)
        } else {
            state.alwaysUseXhrForServerRequests = false
            setParameter(UIState.PushConfigurationState.TRANSPORT_PARAM,
                    transport.identifier)
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#getFallbackTransport()
     */
    override fun getFallbackTransport(): Transport? {
        try {
            val p = getParameter(
                    UIState.PushConfigurationState.FALLBACK_TRANSPORT_PARAM)
            return if (p == null) {
                null
            } else {
                Transport.valueOf(p)
            }
        } catch (e: IllegalArgumentException) {
            return null
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.ui.PushConfiguration#setFallbackTransport(com.vaadin.shared
     * .ui.ui.Transport)
     */
    override fun setFallbackTransport(fallbackTransport: Transport) {
        if (fallbackTransport == Transport.WEBSOCKET_XHR) {
            throw IllegalArgumentException(
                    "WEBSOCKET_XHR can only be used as primary transport")
        }
        setParameter(UIState.PushConfigurationState.FALLBACK_TRANSPORT_PARAM,
                fallbackTransport.identifier)
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#getParameter(java.lang.String)
     */
    override fun getParameter(parameter: String): String? {
        return getState(false).parameters[parameter]
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.PushConfiguration#setParameter(java.lang.String,
     * java.lang.String)
     */
    override fun setParameter(parameter: String, value: String) {
        state.parameters[parameter] = value

    }

    private fun getState(markAsDirty: Boolean): UIState.PushConfigurationState {
        return ui.getState(markAsDirty).pushConfiguration
    }

    override fun getParameterNames(): Collection<String> {
        return Collections
                .unmodifiableCollection(getState(false).parameters.keys)
    }

}

interface KrailPushConfiguration : PushConfiguration {
    var ui: ScopedUI
}

