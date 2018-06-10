package uk.q3c.krail.core.form

import com.google.inject.Inject
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 10 Jun 2018
 */
class Form @Inject constructor(translate: Translate, serializationSupport: SerializationSupport) : ViewBase(translate, serializationSupport) {
    override fun doBuild(busMessage: ViewChangeBusMessage) {
        TODO()
    }

}