package uk.q3c.krail.core.ui

import com.google.inject.Inject
import com.vaadin.server.ErrorHandler
import com.vaadin.ui.AbstractOrderedLayout
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.KrailPushConfiguration
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 10 Aug 2018
 */
class LightweightUI @Inject constructor(
        navigator: Navigator,
        errorHandler: ErrorHandler,
        broadcaster: Broadcaster,
        pushMessageRouter: PushMessageRouter,
        applicationTitle: ApplicationTitle,
        translate: Translate,
        currentLocale: CurrentLocale,
        i18nProcessor: I18NProcessor,
        serializationSupport: SerializationSupport,
        pushConfig: KrailPushConfiguration
) : ScopedUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, i18nProcessor, serializationSupport, pushConfig) {


    override fun screenLayout(): AbstractOrderedLayout {
        TODO()
    }
}