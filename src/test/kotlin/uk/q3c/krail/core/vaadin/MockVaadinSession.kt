package uk.q3c.krail.core.vaadin

import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinSession
import io.mockk.every
import io.mockk.mockk
import uk.q3c.krail.util.DefaultResourceUtils

/**
 * Created by David Sowerby on 19 Mar 2018
 */
class MockVaadinSession(val vaadinService: VaadinService = mockk(relaxed = true)) : VaadinSession(vaadinService) {

    override fun hasLock(): Boolean {
        return true
    }

    fun clear() {
        VaadinSession.setCurrent(null)
    }

}

fun createMockVaadinSession(): MockVaadinSession {
    val vaadinService: VaadinService = mockk(relaxed = true)
    every { vaadinService.baseDirectory }.returns(DefaultResourceUtils().userTempDirectory())
    val session = MockVaadinSession(vaadinService)
    VaadinSession.setCurrent(session)
    return session
}



