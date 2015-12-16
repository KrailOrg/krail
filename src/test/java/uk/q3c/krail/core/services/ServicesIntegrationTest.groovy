package uk.q3c.krail.core.services

import com.google.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.persist.InMemoryModule
import uk.q3c.krail.testutil.TestI18NModule
import uk.q3c.krail.testutil.TestOptionModule
import uk.q3c.krail.testutil.TestUIScopeModule
import uk.q3c.krail.testutil.TestVaadinSessionScopeModule

/**
 * Created by David Sowerby on 16 Dec 2015
 */
@UseModules([ServicesModule, TestI18NModule, EventBusModule, TestVaadinSessionScopeModule, TestUIScopeModule, TestOptionModule, InMemoryModule])
class ServicesIntegrationTest extends Specification {

    @Inject
    ServicesModel model

    def setup() {

    }


    def "construction"() {
        expect:
        model.getRegisteredServices() != null
    }
}
