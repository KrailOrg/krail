package uk.q3c.krail.core.i18n

import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.test.MockCurrentLocale
import java.util.*

/**
 * Created by David Sowerby on 16 Jan 2018
 */
class TestKrailI18NModule : KrailI18NModule() {

    var currentLocale = MockCurrentLocale()

    override fun bindCurrentLocale() {
        bind(CurrentLocale::class.java).toInstance(currentLocale)
    }


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
    }

}