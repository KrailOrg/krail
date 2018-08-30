package uk.q3c.krail.core.shiro

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provider
import com.vaadin.server.ErrorEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.error.ErrorModule
import uk.q3c.krail.core.error.SystemErrorNotificationGroup
import uk.q3c.krail.core.eventbus.UIBusProvider
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.shiro.aop.NotAGuestException
import uk.q3c.krail.core.shiro.aop.NotAUserException
import uk.q3c.krail.core.user.notify.DefaultUserNotifier
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.serial.tracer.SerializationTracer

/**
 * Created by David Sowerby on 14 Aug 2018
 */
object KrailErrorHandlerTest : Spek({

    given(" a KrailErrorHandler") {
        lateinit var event: ErrorEvent
        lateinit var handler: KrailErrorHandler
        lateinit var authenticationHandler: UnauthenticatedExceptionHandler
        lateinit var authorisationHandler: UnauthorizedExceptionHandler
        lateinit var notAGuestExceptionHandler: NotAGuestExceptionHandler
        lateinit var notAUserExceptionHandler: NotAUserExceptionHandler
        lateinit var userNotifier: UserNotifier
        lateinit var systemErrorNotificationGroupProvider: Provider<SystemErrorNotificationGroup>
        lateinit var systemErrorNotificationGroup: SystemErrorNotificationGroup

        beforeEachTest {
            event = mockk(relaxed = true)
            authenticationHandler = mockk(relaxed = true)
            authorisationHandler = mockk(relaxed = true)
            notAGuestExceptionHandler = mockk(relaxed = true)
            notAUserExceptionHandler = mockk(relaxed = true)
            userNotifier = mockk(relaxed = true)
            systemErrorNotificationGroupProvider = mockk(relaxed = true)
            systemErrorNotificationGroup = mockk(relaxed = true)
            every { systemErrorNotificationGroupProvider.get() } returns systemErrorNotificationGroup
            val serializationSupport: SerializationSupport = mockk(relaxed = true)
            handler = KrailErrorHandler(authenticationHandler, notAGuestExceptionHandler, notAUserExceptionHandler, authorisationHandler, userNotifier, systemErrorNotificationGroupProvider, serializationSupport)
        }
        on("catching a UnauthenticatedException") {
            val exception = UnauthenticatedException()
            every { event.throwable } returns exception
            handler.error(event)

            it("invokes the correct handler") {
                verify { authenticationHandler.invoke() }
            }
        }

        on("catching a UnauthorizedException") {
            val exception = UnauthorizedException()
            every { event.throwable } returns exception
            handler.error(event)

            it("invokes the correct handler") {
                verify { authorisationHandler.invoke() }
            }
        }

        on("catching a NotAGuestException") {
            val exception = NotAGuestException()
            every { event.throwable } returns exception
            handler.error(event)

            it("invokes the correct handler") {
                verify { notAGuestExceptionHandler.invoke() }
            }
        }

        on("catching a NotAUserException") {
            val exception = NotAUserException()
            every { event.throwable } returns exception
            handler.error(event)

            it("invokes the correct handler") {
                verify { notAUserExceptionHandler.invoke() }
            }
        }

        on("catching a general exception") {
            val exception = NullPointerException()
            every { event.throwable } returns exception
            handler.error(event)

            it("invokes the system error notification group") {
                verify { systemErrorNotificationGroup.notify(exception) }
            }
        }


    }
})


object KrailErrorHandlerSerialisationTest : Spek({

    given("an injected instance") {


        on("serialisation") {
            val injector = Guice.createInjector(ErrorModule(), ShiroVaadinModule(), LocalTestModule())
            InjectorHolder.setInjector(injector)
            val handler = injector.getInstance(KrailErrorHandler::class.java)
            val tracer = SerializationTracer()
            tracer.trace(handler)

            it("should have no errors") {
                tracer.shouldNotHaveAnyDynamicFailures()
            }
        }

    }
})

private class LocalTestModule : AbstractModule() {
    val uiBusProvider: UIBusProvider = mockk(relaxed = true)

    override fun configure() {
        bind(UserNotifier::class.java).to(DefaultUserNotifier::class.java)
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(UIBusProvider::class.java).toInstance(uiBusProvider)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
    }

}

