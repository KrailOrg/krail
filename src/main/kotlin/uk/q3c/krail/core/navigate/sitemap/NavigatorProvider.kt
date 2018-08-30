package uk.q3c.krail.core.navigate.sitemap

import com.vaadin.ui.UI
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.ui.ScopedUI

/**
 * Created by David Sowerby on 11 Aug 2018
 */
class NavigatorProvider {

    fun get(): Navigator {
        return (UI.getCurrent() as ScopedUI).krailNavigator
    }
}