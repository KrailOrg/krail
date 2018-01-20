/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.view.component

import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.server.FileResource
import com.vaadin.server.Resource
import com.vaadin.server.ThemeResource
import com.vaadin.ui.IconGenerator
import com.vaadin.ui.ItemCaptionGenerator
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.option.VaadinOptionContext
import uk.q3c.krail.eventbus.GlobalMessageBus
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.i18n.SupportedLocales
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.OptionChangeMessage
import uk.q3c.krail.option.OptionKey
import uk.q3c.krail.util.ResourceUtils
import java.util.*

interface LocaleContainer : ItemCaptionGenerator<Locale> {
    val loadedLocales: List<Locale>
    val dataProvider: ListDataProvider<Locale>
    fun forceReload()
    val iconGenerator: LocaleIconGenerator
}

@Listener
@SubscribeTo(GlobalMessageBus::class)
class DefaultLocaleContainer @Inject constructor(
        @param:SupportedLocales private val supportedLocales: Set<Locale>,
        override val iconGenerator: LocaleIconGenerator)

    : LocaleContainer {

    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private lateinit var data: MutableList<Locale>
    private var loaded = false
    override val loadedLocales: List<Locale>
        get() {
            load()
            return ImmutableList.copyOf(data)
        }

    override val dataProvider: ListDataProvider<Locale>
        get() {
            log.debug("Retrieving data provider, this will cause LocaleContainer to load")
            load()
            return ListDataProvider(data)
        }

    override fun apply(item: Locale): String {
        load()
        if (data.contains(item)) {
            return item.getDisplayName(item)
        } else {
            throw LocaleException(item)
        }
    }

    /**
     * Loads [data] with text from [Locale.getDisplayName], and an icon for the country flag if
     * there is one. If there is no image flag, the flag property is left as null.  The result is that the combo
     * contains an entry for a country in the language of that country (for example Germany is always Deutsch
     * (Deutschland), regardless of the current locale).  This means the user looking for a language will see it in its
     * most familiar form.
     */
    private fun load() {
        if (!loaded) {
            log.debug("Loading LocaleContainer")
            data = mutableListOf()

            for (supportedLocale in supportedLocales) {
                data.add(supportedLocale)
                iconGenerator.addLocale(supportedLocale)
                log.debug("Added supported locale for: '{}'", supportedLocale.toLanguageTag())
            }

            iconGenerator.load()
            loaded = true
        } else {
            log.debug("LocaleContainer.load() called, but 'loaded' is already true, so call ignored")
        }
    }

    override fun forceReload() {
        loaded = false
        load()
    }


}

class LocaleException(locale: Locale) : RuntimeException("Unrecognised Locale: $locale")

interface LocaleIconGenerator : IconGenerator<Locale>, VaadinOptionContext {
    var flagSize: Int
    /**
     * We add the locale here, but do not attempt to find the resource yet - do that by calling [load]
     */
    fun addLocale(locale: Locale)
    fun load()
}

class DefaultLocaleIconGenerator @Inject constructor(private val resourceUtils: ResourceUtils, private val option: Option) : LocaleIconGenerator {
    override var flagSize: Int = 0
    val flagsDir = "icons/flags_iso"
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private val lookup: MutableMap<Locale, Resource?> = mutableMapOf()
    var loaded = false


    private fun updateFlagSizeFromOption(): Int {
        flagSize = option.get(optionKeyFlagSize)
        return flagSize
    }

    /**
     * This method can be invoked directly to load the icons, but note that it will only load if [loaded] is false
     */
    override fun load() {
        log.debug("Loading supported locales into LocaleContainer")
        if (!loaded) {
            updateFlagSizeFromOption()
            reloadIcons()
        }
    }


    override fun addLocale(locale: Locale) {
        lookup.put(locale, null)
    }

    override fun apply(item: Locale): Resource? {
        if (lookup.containsKey(item)) {
            val r = lookup[item]
            if (r is FileResource) {
                log.debug("returning icon {} for Locale: {}", r.sourceFile, item)
            } else {
                log.debug("returning icon {} for Locale: {}", r, item)
            }
            return lookup[item]
        }
        throw LocaleException(item)
    }

    /**
     * Note that this returns a ThemeResource even if the file is not present
     */
    private fun findResource(locale: Locale): ThemeResource {

        val iconPath = "$flagsDir/$flagSize/${locale.country.toLowerCase()}.png"
        log.debug("Looking for flag icon at {} for Locale {}", iconPath, locale)
        return ThemeResource(iconPath)
    }

    override fun optionInstance(): Option {
        return option
    }

    @Handler
    fun optionValueChanged(msg: OptionChangeMessage<*>) {
        if (msg.optionKey === optionKeyFlagSize) {
            updateFlagSizeFromOption()
            reloadIcons()
        }
    }

    private fun reloadIcons() {
        log.debug("Loading icons")
        for ((k) in lookup) {
            val r = findResource(k)
            lookup.put(k, r)
        }

    }


}

val optionKeyFlagSize = OptionKey(32, LocaleIconGenerator::class.java, LabelKey.Locale_Flag_Size, DescriptionKey.Locale_Flag_Size)