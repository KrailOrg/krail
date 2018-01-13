package uk.q3c.krail.core.navigate.sitemap

import com.google.common.collect.ImmutableList
import com.vaadin.data.TreeData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by David Sowerby on 13 Jan 2018
 */
class TreeDataWalkerTest {

    lateinit var treeData: TreeData<TestNode>
    lateinit var walker: TreeDataWalker<TestNode>
    val node0 = TestNode("0")
    val node1 = TestNode("1")
    val node00 = TestNode("0-0")
    val node01 = TestNode("0-1")
    val node000 = TestNode("0-0-0")

    @Before
    fun setUp() {
        treeData = TreeData()
        walker = TreeDataWalker(treeData)
        treeData.addItems(null, ImmutableList.of(node0, node1))
        treeData.addItems(node0, ImmutableList.of(node00, node01))
        treeData.addItems(node00, ImmutableList.of(node000))
    }

    @Test
    fun walkAndCount() {
        // given:

        val visitor = CountNodeVisitor<TestNode>()

        // when:
        walker.walk(visitor)

        // then:
        assertThat(visitor.count).isEqualTo(5)
    }

    @Test
    fun walkAndList() {
        // given:

        val visitor = ListNodeVisitor<TestNode>()

        // when:
        walker.walk(visitor)

        // then:
        assertThat(visitor.list).containsOnly(node0, node00, node000, node01, node1)
    }

    @Test
    fun countEmptyTree() {
        // given:
        val visitor = CountNodeVisitor<TestNode>()

        // when:
        treeData.clear()
        walker.walk(visitor)

        // then:
        assertThat(visitor.count).isEqualTo(0)
    }

    @Test
    fun listEmptyTree() {
        // given:

        treeData.clear()
        val visitor = ListNodeVisitor<TestNode>()

        // when:
        walker.walk(visitor)

        // then:
        assertThat(visitor.list).isEmpty()
    }
}


data class TestNode(val name: String) : NodeVisitable {
    override fun accept(visitor: NodeVisitor<NodeVisitable>) {
        visitor.visit(this)
    }
}
