package uk.q3c.krail.core.guice

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.BootstrapConfig
import uk.q3c.krail.core.env.BootstrapYAMLReader
import uk.q3c.krail.core.env.KrailVertxBootstrapModule
import uk.q3c.util.testutil.TestResource


/**
 * Created by David Sowerby on 18 Mar 2018
 */
object BootstrapConfigTest : Spek({

    given("a BootStrapConfig") {
        val resourceReference = KrailVertxBootstrapModule()
        lateinit var bootstrapConfig: BootstrapConfig

        on("loading a good file") {
            val sourceFile = TestResource.resource(resourceReference, "krail-bootstrap1.yml")
            val source = sourceFile?.readText()
            if (source != null) {
                bootstrapConfig = BootstrapYAMLReader().read(source)
            }

            it("correctly sets up config object") {
                with(bootstrapConfig) {
                    version.shouldEqual(1)
                    collator.shouldBeEqualTo("uk.q3c.krail.core.guice.CoreBindingsCollator")
                    modules.shouldContain("uk.q3c.krail.core.vaadin.DataModule")
                    servletConfig.additionalModules.shouldContain("uk.q3c.krail.core.env.ServletEnvironmentModule")
                    vertxConfig.additionalModules.shouldContain("uk.q3c.krail.core.env.VertxEnvironmentModule")
                }
            }
        }

        on("optional elements missing") {
            val sourceFile = TestResource.resource(resourceReference, "krail-bootstrap2.yml")
            val source = sourceFile!!.readText()
            bootstrapConfig = BootstrapYAMLReader().read(source)

            it("uses defaults") {
                with(bootstrapConfig) {
                    version.shouldEqual(1)
                    collator.shouldBeEqualTo("uk.q3c.krail.core.guice.CoreBindingsCollator")
                    modules.shouldBeEmpty()
                    servletConfig.additionalModules.shouldContain("uk.q3c.krail.core.env.ServletEnvironmentModule")
                    vertxConfig.additionalModules.shouldContain("uk.q3c.krail.core.env.VertxEnvironmentModule")
                }
            }
        }

        on("environment module elements missing or empty") {
            val sourceFile = TestResource.resource(resourceReference, "krail-bootstrap3.yml")
            val source = sourceFile!!.readText()
            bootstrapConfig = BootstrapYAMLReader().read(source)

            it("throws an exception") {
                with(bootstrapConfig) {
                    version.shouldEqual(1)
                    collator.shouldBeEqualTo("uk.q3c.krail.core.guice.CoreBindingsCollator")
                    modules.shouldContain("uk.q3c.krail.core.vaadin.DataModule")
                    servletConfig.additionalModules.shouldContain("uk.q3c.krail.core.env.ServletEnvironmentModule")
                    vertxConfig.additionalModules.shouldBeEmpty()
                }
            }
        }
    }
})