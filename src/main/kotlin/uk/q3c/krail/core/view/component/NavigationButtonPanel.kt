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

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.themes.ValoTheme
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.eventbus.UIBus
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.util.forest.NodeFilter
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream
import java.util.*

@Listener
@SubscribeTo(UIBus::class, SessionBus::class)
abstract class NavigationButtonPanel @Inject protected constructor(
        @field:Transient protected val navigatorProvider: Provider<Navigator>,
        @field:Transient protected val sitemapProvider: Provider<UserSitemap>,
        private val serializationSupport: SerializationSupport)

    : HorizontalLayout(), Button.ClickListener {

    val buttons = ArrayList<NavigationButton>()
    private val sourceFilters = LinkedList<NodeFilter<UserSitemapNode>>()

    var rebuildRequired = true
        protected set

    init {
        this.setSizeUndefined()
        this.isSpacing = true
    }

    fun moveToNavigationState() {
        log.debug("moving to navigation state")
        rebuildRequired = true
        build()
    }

    fun isRebuildRequired(): Boolean {
        return rebuildRequired
    }

    protected abstract fun build()

    /**
     * Displays buttons to represent the supplied nodes.
     *
     * @param nodeList
     * contains the list of buttons to display. It is assumed that these are in the right order
     */
    protected fun organiseButtons(nodeList: List<UserSitemapNode>) {
        log.debug("{} nodes to display before filtering", nodeList.size)
        val filteredList = filteredList(nodeList)
        log.debug("{} nodes to display after filtering", filteredList.size)
        val maxIndex = if (filteredList.size > buttons.size) filteredList.size else buttons.size
        for (i in 0 until maxIndex) {
            // nothing left in chain
            if (i + 1 > filteredList.size) {
                // but buttons still exist
                if (i < buttons.size) {
                    buttons[i].isVisible = false
                }
            } else {
                // chain continues
                var button: NavigationButton
                // steps still exist, re-use
                if (i < buttons.size) {
                    button = buttons[i]
                } else {
                    button = createButton()
                }
                setupButton(button, filteredList[i])
            }

        }
    }

    protected fun createButton(): NavigationButton {
        val button = NavigationButton()
        button.addStyleName(ValoTheme.BUTTON_LINK)
        button.addClickListener(this)
        buttons.add(button)
        this.addComponent(button)
        return button
    }

    private fun setupButton(button: NavigationButton, sitemapNode: UserSitemapNode) {

        button.node = sitemapNode
        button.isVisible = true

    }

    protected fun filteredList(list: List<UserSitemapNode>): List<UserSitemapNode> {
        val newList = ArrayList<UserSitemapNode>()
        for (node in list) {
            var accept = true
            for (filter in sourceFilters) {
                if (!filter.accept(node)) {
                    accept = false
                    break
                }
            }

            if (accept) {
                newList.add(node)
            }
        }
        return newList
    }

    @Handler
    fun localeChanged(busMessage: LocaleChangeBusMessage) {
        log.debug("responding to locale change to {}", busMessage.newLocale)
        for (button in buttons) {
            button.caption = button.node
                    .label
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun afterViewChange(busMessage: AfterViewChangeBusMessage) {
        log.debug("Responding to view change")
        rebuildRequired = true
        build()
    }


    override fun buttonClick(event: ClickEvent) {
        val button = event.button as NavigationButton
        navigatorProvider.get().navigateTo(button.node)

    }

    fun addFilter(filter: NodeFilter<UserSitemapNode>) {
        sourceFilters.add(filter)
    }

    fun removeFilter(filter: NodeFilter<UserSitemapNode>) {
        sourceFilters.remove(filter)
    }


    @Throws(ClassNotFoundException::class, IOException::class)
    protected fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }

    companion object {
        private val log = LoggerFactory.getLogger(NavigationButtonPanel::class.java)
    }
}
