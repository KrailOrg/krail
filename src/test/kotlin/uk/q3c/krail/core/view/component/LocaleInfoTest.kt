package uk.q3c.krail.core.view.component

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

/**
 * Created by David Sowerby on 11 Jan 2018
 */
class LocaleInfoTest {


    @Test
    fun displayNameShowsInLocaleLanguage() {
        // given:
        val info = LocaleInfo(Locale.GERMANY, null)

        // expect:
        assertThat(info.displayName()).isEqualTo("Deutsch (Deutschland)")
    }

}