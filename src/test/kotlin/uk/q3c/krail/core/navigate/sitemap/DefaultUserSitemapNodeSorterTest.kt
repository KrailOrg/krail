package uk.q3c.krail.core.navigate.sitemap

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.form.shouldContainExactly
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortDirection.NORMAL
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortDirection.REVERSED
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortType.*
import java.text.Collator
import java.util.*

/**
 * Created by David Sowerby on 10 Aug 2018
 */
object DefaultUserSitemapNodeSorterTest : Spek({

    given("a sorter and list of nodes") {
        val collator = Collator.getInstance(Locale.UK);
        val sorter = DefaultUserSitemapNodeSorter()
        val node1: UserSitemapNode = mockk(relaxed = true)
        val node2: UserSitemapNode = mockk(relaxed = true)
        val node3: UserSitemapNode = mockk(relaxed = true)


        every { node1.collationKey } returns collator.getCollationKey("b")
        every { node2.collationKey } returns collator.getCollationKey("a")
        every { node3.collationKey } returns collator.getCollationKey("c")

        every { node1.positionIndex } returns 3
        every { node2.positionIndex } returns 2
        every { node3.positionIndex } returns 1

        every { node1.id } returns 1
        every { node2.id } returns 2
        every { node3.id } returns 3

        val nodeList = listOf(node1, node2, node3)

        on("calling with default sort mode") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode())

            it("sorts by position index, ascending") {
                result.shouldContainExactly(listOf(node3, node2, node1))
            }
        }

        on("calling with alpha ascending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(ALPHA))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node2, node1, node3))
            }
        }

        on("calling with alpha descending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(ALPHA, REVERSED))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node3, node1, node2))
            }
        }


        on("calling with position ascending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(POSITION, NORMAL))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node3, node2, node1))
            }
        }

        on("calling with position descending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(POSITION, REVERSED))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node1, node2, node3))
            }
        }

        on("calling with insertion ascending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(INSERTION, NORMAL))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node1, node2, node3))
            }
        }

        on("calling with insertion descending") {
            val result = sorter.sort(nodeList, UserSitemapNodeSortMode(INSERTION, REVERSED))

            it("sorts correctly") {
                result.shouldContainExactly(listOf(node3, node2, node1))
            }
        }
    }
})
