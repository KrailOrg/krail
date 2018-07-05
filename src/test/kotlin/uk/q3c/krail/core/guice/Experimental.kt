package uk.q3c.krail.core.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.name.Names
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import javax.inject.Named
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

/**
 * Created by David Sowerby on 24 Mar 2018
 */
class Processor {
    fun process(target: Any) {
        val kotlinClass = target.javaClass.kotlin
        val members = kotlinClass.members
        val declaredMemberProperties = kotlinClass.declaredMemberProperties.toMutableList()
        val propertyAnnotations = declaredMemberProperties[0].annotations
        val ann: com.google.inject.name.Named? = declaredMemberProperties[0].findAnnotation()
        println()
    }
}


class Wiggly

class Subject @Inject constructor(@Named("1") val wiggly: Wiggly)

class AModule : AbstractModule() {
    override fun configure() {
        bind(Subject::class.java)
        bind(Wiggly::class.java).annotatedWith(Names.named("1")).toInstance(Wiggly())
    }

}


object Test : Spek({

    given("processor") {
        val processor = Processor()
        val injector = Guice.createInjector(AModule())

        on("process kotlin") {
            val subject = injector.getInstance(Subject::class.java)
            processor.process(subject)
        }

        on("process java") {
            val subject = injector.getInstance(AJavaClass::class.java)
            processor.process(subject)
        }
    }
})