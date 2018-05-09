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

import com.google.common.base.Preconditions.checkNotNull
import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.ui.Component
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.I18N
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters
import uk.q3c.krail.core.option.VaadinOptionContext
import uk.q3c.krail.eventbus.GlobalMessageBus
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.OptionChangeMessage
import uk.q3c.krail.option.OptionKey
import uk.q3c.util.guice.SerializationSupport
import java.util.*


interface SubPagePanel : Component, UserSitemapSorters {

    /**
     * Sets the sort type but only rebuilds the tree if `rebuild` is true. Useful to call with
     * `rebuild=false` if you want to make several changes to the tree before rebuilding, otherwise just use
     * [UserSitemapSorters.setOptionKeySortType]
     *
     * @param sortType the sort type to use
     * @param rebuild set to true to rebuild
     */
    fun setOptionSortType(sortType: SortType, rebuild: Boolean)

    /**
     * Sets the sort direction but only rebuilds the tree if `rebuild` is true. Useful to call with
     * `rebuild=false` if you want to make several changes to the tree before rebuilding, otherwise just use
     * [UserSitemapSorters.setOptionSortAscending]
     *
     * @param ascending set to true for ascending sort, false for descending sort
     * @param rebuild set to true to rebuild
     */

    fun setSortAscending(ascending: Boolean, rebuild: Boolean)
}


@I18N
@Listener
@SubscribeTo(GlobalMessageBus::class, SessionBus::class)
@AssignComponentId
class DefaultSubPagePanel @Inject constructor(
        navigatorProvider: Provider<Navigator>,
        userSitemapProvider: Provider<UserSitemap>,
        @field:Transient private val optionProvider: Provider<Option>,
        private val sorters: UserSitemapSorters,
        serializationSupport: SerializationSupport)

    : NavigationButtonPanel(navigatorProvider, userSitemapProvider, serializationSupport), VaadinOptionContext, SubPagePanel {



    init {
        this.addFilter(NoNavFilter())
        setup()
    }

    private fun setup() {
        sorters.setOptionSortAscending(getOptionSortAscending())
        sorters.setOptionKeySortType(getOptionSortType())
    }

    fun getOptionSortAscending(): Boolean {
        return optionProvider.get().get(optionSortAscending)
    }

    override fun setOptionSortAscending(ascending: Boolean) {
        setSortAscending(ascending, true)
    }

    override fun setSortAscending(ascending: Boolean, rebuild: Boolean) {
        sorters.setOptionSortAscending(ascending)
        optionProvider.get().set(optionSortAscending, ascending)
        rebuildRequired = true
        if (rebuild) {
            build()
        }
    }

    override fun build() {
        if (rebuildRequired) {
            log.debug("building")
            // premature calls can be made before the navigator has started up properly
            if (navigatorProvider.get().currentNavigationState != null) {
                val currentNode = navigatorProvider.get().currentNode
                if (currentNode == null) {
                    log.debug("currentNode is null, it has probably been removed by change of authorisation")
                } else {
                    log.debug("current node is '{}'", sitemapProvider.get().uri(currentNode))
                }
                val authorisedSubNodes = sitemapProvider.get().getChildren(currentNode)
                Collections.sort(authorisedSubNodes, sortComparator)
                organiseButtons(authorisedSubNodes)
                rebuildRequired = false
            }
        } else {
            log.debug("build not required")
        }
    }

    override fun getSortComparator(): Comparator<UserSitemapNode> {
        return sorters.sortComparator
    }

    fun getOptionSortType(): SortType {
        return optionProvider.get().get(optionSortType)
    }

    override fun setOptionKeySortType(sortType: SortType) {
        checkNotNull(sortType)
        setOptionSortType(sortType, true)
    }

    override fun setOptionSortType(sortType: SortType, rebuild: Boolean) {
        sorters.setOptionKeySortType(sortType)
        optionProvider.get().set(optionSortType, sortType)
        rebuildRequired = true
        if (rebuild) {
            build()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun labelsChanged(busMessage: UserSitemapLabelChangeMessage) {
        rebuildRequired = true
        build()

    }

    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun structureChanged(busMessage: UserSitemapStructureChangeMessage) {
        rebuildRequired = true
        build()
    }


    override fun optionInstance(): Option {
        return optionProvider.get()
    }

    @Handler
    fun optionValueChanged(optionChangeMessage: OptionChangeMessage<*>) {
        if (optionChangeMessage.optionKey.context == DefaultSubPagePanel::class.java) {
            rebuildRequired = true
            build()
        }
    }

    companion object {
        val optionSortType = OptionKey(SortType.ALPHA, DefaultSubPagePanel::class.java, LabelKey.Sort_Type, DescriptionKey
                .Sort_Type)
        val optionSortAscending = OptionKey(true, DefaultSubPagePanel::class.java, LabelKey.Sort_Ascending,
                DescriptionKey
                        .Sort_Ascending)
        private val log = LoggerFactory.getLogger(DefaultSubPagePanel::class.java)
    }


}
