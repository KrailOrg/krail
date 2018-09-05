package uk.q3c.krail.core.view

import com.google.inject.Inject
import com.google.inject.Provider
import uk.q3c.krail.core.navigate.sitemap.EmptyView
import uk.q3c.krail.core.view.component.PageNavigationPanel
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport

/**
 * Presents a set of buttons for navigation to child pages
 *
 * Created by David Sowerby on 11 Aug 2018
 */
interface NavigationView : EmptyView

class DefaultNavigationView @Inject constructor(serialisationSupport: SerializationSupport, translate: Translate, @field:Transient val navigationPanelProvider: Provider<PageNavigationPanel>) : ViewBase(translate, serialisationSupport), NavigationView {


    override fun doBuild() {
        val navigationPanel = navigationPanelProvider.get()
        rootComponent = navigationPanel
    }

}