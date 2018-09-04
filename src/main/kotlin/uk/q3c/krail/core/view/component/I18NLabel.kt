package uk.q3c.krail.core.view.component

import com.vaadin.ui.Label
import uk.q3c.krail.i18n.I18NKey

/**
 * A very simple extension to [Label], which uses [valueKey] in conjunction with [TranslatableComponents] to automatically
 * update when Locale changes.
 *
 * Created by David Sowerby on 04 Sep 2018
 */

class I18NLabel(val valueKey: I18NKey) : Label()

class MutableI18NLabel(var valueKey: I18NKey) : Label()