package advent.day12

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias Path = List<String>

typealias Graph = Map<String, Set<String>>
fun Graph.getEdges(node: Node): Set<Node> = get(node.id)
    ?.map { edgeTo ->
        when {
            edgeTo == "start" -> StartCave(edgeTo, node.path + edgeTo)
            edgeTo == "end" -> EndCave(edgeTo, node.path + edgeTo)
            edgeTo.all { it.isLowerCase() }-> SmallCave(edgeTo, node.path + edgeTo)
            else -> BigCave(edgeTo, node.path + edgeTo)
        }
    }
    ?.toSet()
    ?: emptySet()

sealed class Node {
    abstract val id: String
    abstract val path: Path
}
open class SmallCave(override val id: String, override val path: Path): Node()
data class BigCave(override val id: String, override val path: Path): Node()
data class StartCave(override val id: String, override val path: Path = listOf(id)): SmallCave(id, path)
data class EndCave(override val id: String, override val path: Path): SmallCave(id, path)

operator fun <K> Map<K, Set<K>>.plus(other: Map<K, Set<K>>) =
    this + other.map { (k, v) -> k to (getOrDefault(k, emptySet()) + v) }

data class IntermediateState(
    val graph: Graph,
    val queue: List<Node> = listOf(StartCave("start")),
    val completePaths: Set<Path> = emptySet()
)
fun Graph.toIntermediateState() = IntermediateState(graph = this)

@OptIn(ExperimentalStdlibApi::class)
fun part1(input: File) = input.readLines()
    .map { it.split("-") }
    .fold(emptyMap<String, Set<String>>()) { graph, (edgeFrom, edgeTo) ->
        graph + mapOf(edgeFrom to setOf(edgeTo)) + mapOf(edgeTo to setOf(edgeFrom))
    }
    .onEach(::println)
    .let(Graph::toIntermediateState)
    .let(::findAllPaths)
    .onEach(::println)
    .count()

@OptIn(ExperimentalStdlibApi::class)
typealias DeepRecursiveBfs = DeepRecursiveFunction<IntermediateState, Set<Path>>

@OptIn(ExperimentalStdlibApi::class)
fun findAllPaths(initialState: IntermediateState) =  DeepRecursiveBfs { state ->
        val (graph, queue, completePaths) = state
        when {
            queue.isEmpty() -> completePaths
            else -> {
                val curr = queue.first()
                if (curr is EndCave) {
                    callRecursive(
                        state.copy(queue = queue.drop(1), completePaths = completePaths + setOf(curr.path))
                    )
                } else {
                    val edges = graph.getEdges(curr)
                        .filterNot { edgeTo -> curr.path.contains(edgeTo.id) && edgeTo is SmallCave }

                    callRecursive(state.copy(queue = queue.drop(1) + edges))
                }
            }
        }
    }.invoke(initialState)

fun part2(input: File) = Unit

fun main() {
//    with(loadInput(day = 12, filename = "input_example.txt")) {
//    with(loadInput(day = 12, filename = "input_example2.txt")) {
//    with(loadInput(day = 12, filename = "input_example3.txt")) {
    with(loadInput(day = 12)) {
        println(part1(this))
        println(part2(this))
    }
}