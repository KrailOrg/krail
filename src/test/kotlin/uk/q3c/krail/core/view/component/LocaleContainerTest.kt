@file:Suppress("FunctionName")

package uk.q3c.krail.core.view.component


import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.OptionChangeMessage
import uk.q3c.krail.util.ResourceUtils
import java.io.File
import java.util.*

/**
 * Created by David Sowerby on 16 Jan 2018
 */
class LocaleContainerTest {

    val resourceUtils: ResourceUtils = mock()
    val option: Option = mock()
    lateinit var container: LocaleContainer
    lateinit var iconGenerator: LocaleIconGenerator
    var supportedLocales: MutableSet<Locale> = mutableSetOf()

    @Before
    fun setup() {
        whenever(option.get(optionKeyFlagSize)).thenReturn(48)
        whenever(resourceUtils.configurationDirectory()).thenReturn(File("src/test/java/WEB-INF"))
        iconGenerator = DefaultLocaleIconGenerator(resourceUtils, option)
        supportedLocales = mutableSetOf()
    }

    @Test
    fun normalConstruction() {

        // when container constructed

        supportedLocales.add(Locale.GERMANY)
        container = DefaultLocaleContainer(supportedLocales, iconGenerator)


        // then calling dataProvider loads the container
        container.dataProvider.items.shouldNotBeEmpty()
        container.dataProvider.items.size.shouldEqualTo(1)
        container.iconGenerator.apply(Locale.GERMANY).shouldNotBeNull()
        container.loadedLocales.size.shouldEqualTo(1)

        // then caption is provided in is own language
        container.apply(Locale.GERMANY).shouldBeEqualTo("Deutsch (Deutschland)")

        // when supportedLocales reset and container has forced reload
        supportedLocales.clear()
        supportedLocales.add(Locale.GERMANY)
        supportedLocales.add(Locale.UK)
        supportedLocales.add(Locale.CHINA)
        container.forceReload()

        // then China has no icon, but still return ThemeResource
        container.dataProvider.items.size.shouldEqualTo(3)
        container.iconGenerator.apply(Locale.UK).shouldNotBeNull()
        container.iconGenerator.apply(Locale.CHINA).shouldNotBeNull()

        // then returns the correct instance
        container.iconGenerator.optionInstance().shouldBe(option)

        // when option size is reset
        whenever(option.get(optionKeyFlagSize)).thenReturn(32)
        (container.iconGenerator as DefaultLocaleIconGenerator).optionValueChanged(OptionChangeMessage(optionKeyFlagSize, null, 1, 48, 32))

        // then
        container.iconGenerator.apply(Locale.UK).shouldNotBeNull()


    }

    @Test(expected = LocaleException::class)
    fun captionForInvalidLocale() {
        // given
        supportedLocales.add(Locale.GERMANY)
        container = DefaultLocaleContainer(supportedLocales, iconGenerator)

        // when
        container.apply(Locale.JAPAN)
    }


}

