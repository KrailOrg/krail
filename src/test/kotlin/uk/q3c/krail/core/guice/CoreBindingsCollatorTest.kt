package uk.q3c.krail.core.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.TypeLiteral
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.validation.KrailInterpolator
import uk.q3c.krail.i18n.persist.I18NPersistenceHelper
import uk.q3c.krail.option.persist.ActiveOptionSourceDefault
import uk.q3c.krail.option.persist.OptionDaoDelegate
import uk.q3c.krail.persist.InMemory
import uk.q3c.krail.persist.inmemory.dao.InMemoryOptionDaoDelegate
import uk.q3c.krail.testutil.dummy.DefaultDummy
import uk.q3c.krail.testutil.dummy.Dummy
import javax.validation.MessageInterpolator

/**
 * Created by David Sowerby on 19 Mar 2018
 */
object CoreBindingsCollatorTest : Spek({

    given(" a CoreBindingsCollator") {
        val collator = CoreBindingsCollator(DummyModule(), ServletEnvironmentModule())

        on("creating the Injector") {
            val injector = Guice.createInjector(collator.allModules())

            it("set InMemory option dao as active dao") {
                activeOptionDao(injector).shouldEqual(InMemory::class.java)
            }

            it("OptionDaoDelegate is bound to InMemoryOptionDao with InMemory annotation") {
                optionDao(injector).shouldBeInstanceOf(InMemoryOptionDaoDelegate::class.java)
            }

            it("default option source is set to InMemory") {
                activeOptionSource(injector).shouldEqual(InMemory::class.java)
            }

            it("provides replacement interpolator") {
                injector.getInstance(MessageInterpolator::class.java).shouldBeInstanceOf(KrailInterpolator::class.java)
            }

            it("provides I18NKey substitutes for JSR303") {

                val interpolator: KrailInterpolator = injector.getInstance(MessageInterpolator::class.java) as KrailInterpolator
                interpolator.javaxValidationSubstitutes.size.shouldBe(15)
            }

            it("has added Dummy module from its constructor") {
                injector.getInstance(Dummy::class.java).shouldNotBeNull()
            }
        }
    }


})


fun optionDao(injector: Injector): OptionDaoDelegate {
    val delegateTL = object : TypeLiteral<OptionDaoDelegate>() {

    }
    return getBinding(injector, delegateTL, InMemory::class.java)
}

fun activeOptionSource(injector: Injector): Class<out Annotation> {
    val typeLiteral = object : TypeLiteral<Class<out Annotation>>() {}
    return getBinding(injector, typeLiteral, ActiveOptionSourceDefault::class.java)
}

fun activeOptionDao(injector: Injector): Class<out Annotation> {
    return getBinding(injector, I18NPersistenceHelper.annotationClassLiteral(), ActiveOptionSourceDefault::class.java)
}

fun <T> getBinding(injector: Injector, typeLiteral: TypeLiteral<T>): T {
    val key = Key.get(typeLiteral)
    val binding = injector.getBinding(key)
    return binding.provider.get()
}


fun <T> getBinding(injector: Injector, typeLiteral: TypeLiteral<T>, annotationClass: Class<out Annotation>): T {
    val key = Key.get(typeLiteral, annotationClass)
    val binding = injector.getBinding(key)
    return binding.provider.get()
}

interface Dummy

class DefaultDummy : Dummy

class DummyModule : AbstractModule() {

    var isModuleCalled = false

    override fun configure() {
        isModuleCalled = true
        bind(Dummy::class.java).to(DefaultDummy::class.java)
    }

}