package uk.q3c.krail.core.navigate.sitemap

import com.google.inject.Inject
import com.vaadin.ui.Label
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport

/**
 * An "empty" view is one where not intended to be any user content, but may contain navigation to the next level down in
 * the page hierarchy.  For example, when displayed on a mobile, this page may be automatically populated with buttons
 * to navigate to its "child" pages. On a desktop device, maybe something else would be preferred.
 *
 * Guice binding is in [SitemapModule]
 *
 * Created by David Sowerby on 29 Apr 2018
 */
interface EmptyView : KrailView

class DefaultEmptyView @Inject constructor(translate: Translate, serializationSupport: SerializationSupport) : ViewBase(translate, serializationSupport), EmptyView {

    override fun doBuild(busMessage: ViewChangeBusMessage) {
        rootComponent = Label("empty view")
    }

}