package uk.q3c.krail.core.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.name.Named
import com.google.inject.name.Names
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldThrow
import org.apache.commons.lang3.SerializationUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockito.Mockito
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.guice.SerializationSupportException
import java.io.Serializable

/**
 * Created by David Sowerby on 17 Mar 2018
 */

fun <T : Serializable> serialize(testViewClass: Class<out T>): ByteArray? {
    val locator = ServletInjectorLocator()
    locator.put(Guice.createInjector(TestModule(), Dummy1Module()))
    val testView = locator.get().getInstance(testViewClass)
    return SerializationUtils.serialize(testView)
}

object DefaultSerializationSupportTest : Spek({


    given("an instance that needs no env injections") {

        on("serialization / deserialization") {

            val output = serialize(ClassWithNoGuiceInjections::class.java)

            it(" passes its check") {
                val result = SerializationUtils.deserialize<ClassWithNoGuiceInjections>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)

            }
        }
    }

    given("an instance which has two fields that need injection") {
        on("deserialization") {
            val output = serialize(ClassWithTwoGuiceInjections::class.java)

            it("populates both fields") {
                val result = SerializationUtils.deserialize<ClassWithTwoGuiceInjections>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)
                result.dummy1.shouldBeInstanceOf(Dummy1::class)
                result.dummy2.shouldBeInstanceOf(Dummy2::class)
            }

        }
    }


    given("an instance which has two fields of the same type, that need injection, fields annotated correctly") {
        on("deserialization") {
            val output = serialize(ClassWithTwoAnnotatedGuiceInjections::class.java)

            it("populates both fields") {
                val result = SerializationUtils.deserialize<ClassWithTwoAnnotatedGuiceInjections>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)
                result.dummy1.age.shouldEqual(23)
                result.dummy2.age.shouldEqual(99)
            }


        }
    }

    given("an instance which has two fields of the same type, that need injection, one field missing annotation") {
        on("deserialization") {
            val output = serialize(ClassWithMissingGuiceFieldAnnotation::class.java)
            it(" fails its check") {
                val result = { SerializationUtils.deserialize<ClassWithMissingGuiceFieldAnnotation>(output) }
                result.shouldThrow(SerializationSupportException::class)
            }
        }
    }

    given("an instance which has two fields of the same type, that need injection, one field missing annotation.  Missing field is excluded") {
        on("deserialization") {
            val output = serialize(ClassWithExcludedField::class.java)
            it("passes its check") {
                val result = SerializationUtils.deserialize<ClassWithExcludedField>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)
                result.dummy1.shouldBeNull()
                result.dummy2.age.shouldEqual(99)
            }
        }
    }


    given("an instance which has two fields of the same type, that need injection, fields annotated correctly, but one filled by user code before injection and one after injection") {
        on("deserialization") {
            val output = serialize(ClassWithDeeperInheritanceAndOverridingInjection::class.java)

            it("fills fields by user code action, not injection") {
                val result = SerializationUtils.deserialize<ClassWithDeeperInheritanceAndOverridingInjection>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)
                result.dummy1.age.shouldEqual(123)
                result.dummy2.age.shouldEqual(199)
                result.dummy3.shouldBeInstanceOf(Dummy2::class.java)
            }
        }
    }


    given("an instance which has two fields of the same type, deeper inheritance") {
        on("deserialization") {
            val output = serialize(ClassWithDeeperInheritance::class.java)

            it("fills all fields, with no errors") {
                val result = SerializationUtils.deserialize<ClassWithDeeperInheritance>(output)
                result.translate.shouldNotBeNull()
                isMock(result.translate).shouldBeTrue()
                result.serializationSupport.shouldNotBeNull()
                result.serializationSupport.shouldBeInstanceOf(DefaultSerializationSupport::class.java)
                result.dummy1.age.shouldEqual(23)
                result.dummy2.age.shouldEqual(99)
                result.dummy3.shouldBeInstanceOf(Dummy2::class.java)
            }
        }
    }

})

fun isMock(obj: Any): Boolean {
    return obj.javaClass.name.contains("Mockito")
}

val translate: Translate = Mockito.mock(Translate::class.java)

class TestModule : AbstractModule() {

    override fun configure() {
        bind(Translate::class.java).toInstance(translate)
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
    }

}

class Dummy1Module : AbstractModule() {
    val dummyA = Dummy1()
    val dummyB = Dummy1()

    override fun configure() {
        dummyA.age = 23
        dummyB.age = 99
        bind(Dummy1::class.java).annotatedWith(Names.named("1")).toInstance(dummyA)
        bind(Dummy1::class.java).annotatedWith(Names.named("2")).toInstance(dummyB)
    }

}

class ClassWithNoGuiceInjections @Inject constructor(translate: Translate, serializationSupport: SerializationSupport) : Serializable, ViewBase(translate, serializationSupport) {

    override fun doBuild() {

    }

}


class ClassWithTwoGuiceInjections @Inject constructor(translate: Translate, serializationSupport: SerializationSupport, @Transient val dummy1: Dummy1, @Transient val dummy2: Dummy2) : Serializable, ViewBase(translate, serializationSupport) {

    override fun doBuild() {

    }
}

open class ClassWithTwoAnnotatedGuiceInjections @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        @field:Named("1") @param:Named("1") @Transient var dummy1: Dummy1,
        @field:Named("2") @param:Named("2") @Transient var dummy2: Dummy1) : Serializable, ViewBase(translate, serializationSupport) {

    override fun doBuild() {

    }
}


open class ClassWithMissingGuiceFieldAnnotation @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        @field:Named("1") @param:Named("1") @Transient val dummy1: Dummy1,
        @Named("2") @Transient val dummy2: Dummy1) : Serializable, ViewBase(translate, serializationSupport) {

    override fun doBuild() {

    }
}

open class ClassWithExcludedField @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        @param:Named("1") @Transient val dummy1: Dummy1,
        @field:Named("2") @param:Named("2") @Transient val dummy2: Dummy1) : Serializable, ViewBase(translate, serializationSupport) {

    override fun doBuild() {

    }

    override fun beforeTransientInjection() {
        serializationSupport.excludedFieldNames = listOf("dummy1")
    }
}

class ClassWithDeeperInheritanceAndOverridingInjection @Inject constructor(translate: Translate, serializationSupport: SerializationSupport, @Named("1") dummy1: Dummy1, @Named("2") dummy2: Dummy1, @Transient val dummy3: Dummy2) : ClassWithTwoAnnotatedGuiceInjections(translate, serializationSupport, dummy1, dummy2) {

    override fun beforeTransientInjection() {
        dummy1 = Dummy1()
        dummy1.age = 123
        dummy2 = Dummy1()
        dummy2.age = 199
    }
}

class ClassWithDeeperInheritance @Inject constructor(translate: Translate, serializationSupport: SerializationSupport, @Named("1") dummy1: Dummy1, @Named("2") dummy2: Dummy1, @Transient val dummy3: Dummy2) : ClassWithTwoAnnotatedGuiceInjections(translate, serializationSupport, dummy1, dummy2)


class Dummy1 @Inject constructor() {
    var age: Int = 0
}

class Dummy2