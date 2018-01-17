package uk.q3c.krail.core.i18n

import java.util.*

/**
 * Created by David Sowerby on 16 Jan 2018
 */
class TestKrailI18NModule2 : KrailI18NModule() {


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
        supportedLocales(Locale("de", "CH"))
    }

}