package uk.q3c.krail.testutil

import io.mockk.every
import io.mockk.mockk
import net.engio.mbassy.bus.error.PublicationError
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.testutil.eventbus.TestEventBusErrorHandler
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 24 Jun 2018
 */
object TestEventBusErrorHandlerTest : Spek({

    given("a handler") {
        lateinit var errorMessage: PublicationError
        lateinit var handler: TestEventBusErrorHandler
        lateinit var logMonitor: LogMonitor

        beforeEachTest {
            errorMessage = mockk(relaxed = true)
            handler = TestEventBusErrorHandler()
            logMonitor = LogMonitor()
            logMonitor.addClassFilter(TestEventBusErrorHandler::class.java)

            every {
                errorMessage.message
            } returns "an error message"

            every {
                errorMessage.cause
            } returns NullPointerException()
        }

        on("receiving an error message") {
            handler.handleError(errorMessage)

            it("logs the error") {
                logMonitor.errorCount().shouldBe(1)
                logMonitor.errorLogs().shouldContain("an error message")
            }
        }

    }
})