package advent.day18

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

fun part1(input: File) = input.readLines()
//    .first()
//    .let { pairs -> buildTreeRecursively(RootNode(), pairs.drop(1))}
    .map { snailfishNumber -> buildTreeRecursively(RootNode(), snailfishNumber.drop(1))}
    .onEach(::println)
    .map { it.reduce() }
    .onEach(::println)
    .reduce { first, second -> first sumWith second }
    .let {
        val summed = it
        println("after addition: $it")

        val reduced = summed.reduce()
        println("---------------------")
        println("final sum: $reduced")
    }

fun buildTreeRecursively(curr: TreeNode, snailfishNumber: String): TreeNode = when {
    snailfishNumber.isBlank() -> curr
    snailfishNumber.first() == ',' -> buildTreeRecursively(curr, snailfishNumber.drop(1))
    snailfishNumber.first() == ']' -> when (curr) {
        is RootNode -> curr
        is InternalNode -> buildTreeRecursively(curr.parent, snailfishNumber.drop(1))
        is LeafNode -> buildTreeRecursively(curr.parent, snailfishNumber.drop(1))
    }
    snailfishNumber.first() == '[' -> when (curr) {
        is RootNode, is InternalNode -> {
            buildTreeRecursively(
                curr = InternalNode(parent = curr).also { curr.appendChild(it) },
                snailfishNumber = snailfishNumber.drop(1)
            )
        }
        is LeafNode -> throw IllegalArgumentException("Leaf node can't have any children")
    }
    snailfishNumber.first().isDigit() -> {
        curr.appendChild(LeafNode(parent = curr, value = snailfishNumber.first().digitToInt()))
        buildTreeRecursively(curr, snailfishNumber.drop(1))
    }
    else -> throw IllegalArgumentException("Invalid format of snailfish pairs: $snailfishNumber")
}

infix fun RootNode.sumWith(otherRoot: RootNode): RootNode {
    val newRoot = RootNode()
    val newLeft = this@sumWith.rebuildTreeWithNewRoot(newRoot)
    val newRight = otherRoot.rebuildTreeWithNewRoot(newRoot)
    return newRoot.apply {
        left = newLeft
        right = newRight
    }
}

fun RootNode.rebuildTreeWithNewRoot(newRoot: RootNode): TreeNode {
    fun rebuildTree(curr: TreeNode, parent: TreeNode): TreeNode = when (curr) {
        is RootNode, is InternalNode -> {
            InternalNode(parent = parent).apply {
                left = curr.left?.let { rebuildTree(it, this) }
                right = curr.right?.let { rebuildTree(it, this) }
            }
        }
        is LeafNode -> LeafNode(value = curr.value, parent = parent)
    }

   val rebuilt = rebuildTree(this, newRoot)

   return rebuilt
}


fun TreeNode.reduce(): RootNode {
    // explode action
    // - node must be nested inside four (or more) pairs
    // - node must be an internal node
    // - both children must be leaf nodes
    firstOrNull {
        it.depth >= 4
                && it is InternalNode //
                && listOfNotNull(it.left, it.right).all { it is LeafNode }
    }
        ?.let { it as InternalNode }
        ?.explode()
        ?.also { println("after explode: $it") }
        ?.reduce()

    // split action
    // - must be a leaf node with value >= 10
    firstOrNull { it is LeafNode && it.value >= 10 }
        ?.let { it as LeafNode }
        ?.split()
        ?.also { println("after split: $it") }
        ?.reduce()

    // return root node if no more nodes can explode or split
    return backToRoot()
}

fun part2(input: File) = Unit


fun main() {
//    with(loadInput(day = 18)) {
//    with(loadInput(day = 18, filename = "input_example.txt")) {
//    with(loadInput(day = 18, filename = "explode_examples.txt")) {
    with(loadInput(day = 18, filename = "sum_example.txt")) {
        part1(this)
        println(part2(this))
    }
}