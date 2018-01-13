package uk.q3c.krail.core.navigate.sitemap

import com.vaadin.data.TreeData
import java.util.*


/**
 * Created by David Sowerby on 13 Jan 2018
 */
interface NodeVisitable {
    fun accept(visitor: NodeVisitor<NodeVisitable>)
}

interface NodeVisitor<in T : NodeVisitable> {
    fun visit(node: T)
}

/**
 * Returns a count of the number of nodes visited
 */
class CountNodeVisitor<in T : NodeVisitable> : NodeVisitor<T> {

    var count = 0

    override fun visit(node: T) {
        count++
    }
}

/**
 * Returns a list of all the nodes visited
 */
class ListNodeVisitor<T : NodeVisitable> : NodeVisitor<T> {

    val list: MutableList<T> = mutableListOf()

    override fun visit(node: T) {
        list.add(node)
    }

}

class CaptionNodeVisitor<in T : NodeVisitable> : NodeVisitor<T> {

    val captions: MutableList<String> = mutableListOf()

    override fun visit(node: T) {
        if (node is UserSitemapNode) captions.add(node.label)
    }

}

class TreeDataWalker<out T : NodeVisitable>(private val treeData: TreeData<T>) {
    private val stack: Stack<T> = Stack()

    fun walk(visitor: NodeVisitor<T>) {
        stack.addAll(treeData.rootItems)
        while (stack.isNotEmpty()) {
            val node = stack.pop()
            visitor.visit(node)
            stack.addAll(treeData.getChildren(node))
        }

    }
}