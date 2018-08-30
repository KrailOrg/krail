package uk.q3c.krail

import com.vaadin.server.VaadinService
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.util.DefaultResourceUtils
import java.io.File

/**
 * Created by David Sowerby on 01 Jul 2018
 */
object DefaultResourceUtilsTest : Spek({
    val baseDirectoryName = "/user/home/temp"
    val baseDirectory = File(baseDirectoryName)

    given("a DefaultResourceUtils instance") {
        lateinit var util: DefaultResourceUtils
        lateinit var vaadinService: VaadinService

        beforeEachTest {
            vaadinService = mockk(relaxed = true)
            util = DefaultResourceUtils()
            VaadinService.setCurrent(null)
        }

        on("ApplicationBasePath called when VaadinService is not running") {
            val result = { util.applicationBasePath() }

            it("throws exception") {
                result.shouldThrow(IllegalStateException::class)
            }
        }

        on("ApplicationBasePath called when VaadinService is running") {
            VaadinService.setCurrent(vaadinService)
            every { vaadinService.baseDirectory } returns baseDirectory

            it("returns the application base path") {
                util.applicationBasePath().shouldBeEqualTo(baseDirectoryName)
            }
        }

        on("ApplicationBaseDirectory called when VaadinService is not running") {
            val result = { util.applicationBaseDirectory() }

            it("throws exception") {
                result.shouldThrow(IllegalStateException::class)
            }
        }

        on("ApplicationBaseDirectory called when VaadinService is running") {
            VaadinService.setCurrent(vaadinService)
            every { vaadinService.baseDirectory } returns baseDirectory

            it("returns the application base path") {
                util.applicationBaseDirectory().shouldEqual(baseDirectory)
            }
        }

        on("calling userTempDirectory") {
            VaadinService.setCurrent(vaadinService)
            every { vaadinService.baseDirectory } returns baseDirectory
            val temp = util.userTempDirectory()
            val home = util.userHomeDirectory()

            it("returns correct value") {
                temp.shouldEqual(File(System.getProperty("user.home") + "/temp"))
                home.shouldEqual(File(System.getProperty("user.home")))
                util.configurationDirectory().shouldEqual(File(baseDirectory, "WEB-INF"))
                util.webInfDirectory().shouldEqual(File(baseDirectory, "WEB-INF"))
                util.resourcePath("test").shouldEqual(File("$baseDirectoryName/test"))
            }
        }

        on("calling resourcePath with no VaadinService") {
            it("should still return a valid path") {
                util.resourcePath("test").shouldEqual(File("src/main/resources" + "/test"))
            }
        }
    }
})


