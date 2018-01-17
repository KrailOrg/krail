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
import java.io.File
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
            data = mutableListOf()

            for (supportedLocale in supportedLocales) {
                data.add(supportedLocale)
                iconGenerator.addLocale(supportedLocale)
                log.debug("Added supported locale for: '{}'", supportedLocale.toLanguageTag())
            }

            iconGenerator.load()
            loaded = true
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
    fun addLocale(locale: Locale)
    fun load()
}

class DefaultLocaleIconGenerator @Inject constructor(private val resourceUtils: ResourceUtils, private val option: Option) : LocaleIconGenerator {
    override var flagSize: Int = 0
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private val lookup: MutableMap<Locale, Resource?> = mutableMapOf()
    private var flagsDir = File(".")
    private var flagSizedDir = File(".")
    var loaded = false


    private fun updateFlagSizeFromOption(): Int {
        flagSize = option.get(optionKeyFlagSize)
        return flagSize
    }

    /**
     * This method can be invoked directly to load the icons, but note that it will only load if [loaded] is false
     */
    override fun load() {
        if (!loaded) {
            updateFlagSizeFromOption()
            val webInfDir = resourceUtils.configurationDirectory()
            val iconsDir = File(webInfDir, "icons")
            flagsDir = File(iconsDir, "flags_iso")
            flagSizedDir = File(flagsDir, flagSize.toString())
            reloadIcons()
        }
    }

    override fun addLocale(locale: Locale) {
        val resource = findResource(locale)
        lookup.put(locale, resource)
    }

    override fun apply(item: Locale): Resource? {
        if (lookup.containsKey(item)) {
            val r = lookup[item]
            log.debug("returning icon {} for Locale: {}", r, item)
            return lookup[item]
        }
        throw LocaleException(item)
    }

    private fun findResource(locale: Locale): FileResource? {

        var flag: FileResource? = null
        // if the directory is missing don't bother with file
        if (flagSizedDir.exists()) {
            val filename = locale.country.toLowerCase() + ".png"
            val file = File(flagSizedDir, filename)
            if (file.exists()) {
                flag = FileResource(file)
            } else {
                log.warn("File {} for locale flag does not exist.", file.absolutePath)
            }

        } else {
            log.warn("{} directory for flags does not exist.", flagSizedDir.absolutePath)
        }
        return flag
    }

    override fun optionInstance(): Option {
        return option
    }

    @Handler
    fun optionValueChanged(msg: OptionChangeMessage<*>) {
        if (msg.optionKey === optionKeyFlagSize) {
            updateFlagSizeFromOption()
            flagSizedDir = File(flagsDir, flagSize.toString())
            reloadIcons()
        }
    }

    private fun reloadIcons() {
        for ((k) in lookup) {
            val r = findResource(k)
            lookup.put(k, r)
            if (r != null) {
                log.debug("Icon Resource {} added for Locale: {}", r.sourceFile, k)
            } else {
                log.debug("No resource icon for {}", k)
            }
        }

    }


}

val optionKeyFlagSize = OptionKey(32, LocaleIconGenerator::class.java, LabelKey.Locale_Flag_Size, DescriptionKey.Locale_Flag_Size)