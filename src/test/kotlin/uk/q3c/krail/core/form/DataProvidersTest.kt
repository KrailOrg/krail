package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockTranslate

/**
 * Created by David Sowerby on 26 Aug 2018
 */
object DataProvidersTest : Spek({

    given(" an injector") {
        val injector: Injector = Guice.createInjector(DataProvidersTestModule())

        on("constructing a YesNoDataProvider") {
            val dp = injector.getInstance(YesNoDataProvider::class.java)

            it("has the correct entries") {
                dp.items.shouldContainAll(listOf("Yes", "No"))
                dp.items.size.shouldEqual(2)
            }
        }

        on("constructing a BeforeAfterDataProvider") {
            val dp = injector.getInstance(BeforeAfterDataProvider::class.java)

            it("has the correct entries") {
                dp.items.shouldContainAll(listOf("Before", "After"))
                dp.items.size.shouldEqual(2)
            }
        }
    }
})


private class DataProvidersTestModule : AbstractModule() {
    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
    }
}