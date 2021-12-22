package advent.day18

/**
 * @author Dominik Hoftych
 */

sealed class TreeNode {
    open var left: TreeNode? = null
    open var right: TreeNode? = null

    abstract val parent: TreeNode?
    abstract val depth: Int
}

data class RootNode(override var left: TreeNode? = null, override var right: TreeNode? = null) : TreeNode() {
    override val parent: TreeNode? = null
    override val depth = 0
    override fun toString() = "[$left,$right]"
}

data class InternalNode(
    override var left: TreeNode? = null,
    override var right: TreeNode? = null,
    override val parent: TreeNode
) : TreeNode() {
    override val depth = parent.depth + 1
    override fun toString() = "[$left,$right]"
}

data class LeafNode(var value: Int, override val parent: TreeNode) : TreeNode() {
    override val depth = parent.depth + 1
    override fun toString() = "$value"
}

fun TreeNode.appendChild(node: TreeNode) = if (left == null) {
    left = node
} else {
    right = node
}

fun TreeNode.isLeftChild() = parent?.left == this
fun TreeNode.isRightChild() = parent?.right == this

fun TreeNode.backToRoot(): RootNode = when (this) {
    is RootNode -> this
    is InternalNode -> parent.backToRoot()
    is LeafNode -> parent.backToRoot()
}

fun TreeNode.firstLeafToTheLeft(): LeafNode? {
    fun firstNodeToTheLeft(curr: TreeNode): LeafNode? = when (curr) {
        is RootNode -> null
        is InternalNode, is LeafNode -> {
            val parent = curr.parent
                ?: throw IllegalArgumentException("Internal or leaf nodes must have a parent: $curr")
            if (curr.isRightChild() && parent.left is LeafNode) {
                parent.left as LeafNode
            } else {
                firstNodeToTheLeft(parent)
            }
        }
    }

    return firstNodeToTheLeft(this)
}

fun TreeNode.firstLeafToTheRight(): LeafNode? {
    fun firstNodeToTheRight(curr: TreeNode, previous: TreeNode? = null): LeafNode? = when (curr) {
        is RootNode -> if (curr.right != null && previous != curr.right) {
            firstNodeToTheRight(curr.right!!, curr)
        } else {
            null
        }
        is InternalNode, is LeafNode -> {
            val parent = curr.parent
                ?: throw IllegalArgumentException("Internal or leaf nodes must have a parent: $curr")
            if (parent.right != curr && parent.right is LeafNode) {
                parent.right as LeafNode
            } else {
                firstNodeToTheRight(parent, curr)
            }
        }
    }

    return firstNodeToTheRight(this)
}

fun TreeNode.firstOrNull(predicate: (TreeNode) -> Boolean): TreeNode? = if (predicate(this)) {
    this
} else {
    left?.firstOrNull(predicate) ?: right?.firstOrNull(predicate)
}

fun InternalNode.explode(): RootNode {
    val left: LeafNode = left as LeafNode
    val right: LeafNode = right as LeafNode
    val leafToTheLeft = firstLeafToTheLeft()
    val leafToTheRight = firstLeafToTheRight()

    leafToTheLeft?.let { it.value += left.value }
    leafToTheRight?.let { it.value += right.value }

    if (isLeftChild()) {
        parent.left = LeafNode(parent = parent, value = 0)
    } else {
        parent.right = LeafNode(parent = parent, value = 0)
    }

    return parent.backToRoot()
}

fun LeafNode.split(): RootNode = InternalNode(parent = parent) // the node to split is no longer a leaf node
    .apply {
        left = LeafNode(value = value / 2, this)
        right = LeafNode(value = value / 2 + 1, this)
    }.also {
        if (isLeftChild())
            parent.left = it
        else
            parent.right = it
    }.backToRoot()
