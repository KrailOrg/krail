package uk.q3c.krail.core.view.component

import com.vaadin.server.FileResource
import java.util.*

/**
 * Created by David Sowerby on 11 Jan 2018
 */
class LocaleInfo(val locale: Locale, val flag: FileResource?) {


    fun displayName(): String {
        return locale.getDisplayName(locale)
    }


}