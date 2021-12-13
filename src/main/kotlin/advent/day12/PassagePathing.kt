package advent.day12

import advent.loadInput
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

/**
 * @author Dominik Hoftych
 */

typealias Path = List<String>
operator fun Set<Path>.plus(other: Path): Set<Path> = this + setOf(other)

typealias Graph = Map<String, Set<String>>
operator fun <K> Map<K, Set<K>>.plus(other: Map<K, Set<K>>) =
    this + other.map { (k, v) -> k to (getOrDefault(k, emptySet()) + v) }

fun Graph.getEdges(node: Node): Set<Node> = get(node.id)
    ?.map { edgeTo ->
        when {
            edgeTo == "start" -> StartCave(edgeTo, node.path + edgeTo)
            edgeTo == "end" -> EndCave(edgeTo, node.path + edgeTo)
            edgeTo.all(Char::isLowerCase) -> SmallCave(edgeTo, node.path + edgeTo)
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

fun String.isSmallCave() = all(Char::isLowerCase)

data class IntermediateState(
    val graph: Graph,
    val queue: List<Node> = listOf(StartCave("start")),
    val completePaths: Set<Path> = emptySet()
)
fun Graph.toIntermediateState() = IntermediateState(graph = this)

@OptIn(ExperimentalStdlibApi::class)
fun part1(input: File) = loadGraph(input)
    .let(Graph::toIntermediateState)
    .let { initialState ->
        DeepRecursiveFunction<IntermediateState, Set<Path>> { state ->
            val (graph, queue, completePaths) = state
            when {
                queue.isEmpty() -> completePaths
                else -> {
                    val curr = queue.first()
                    if (curr is EndCave) {
                        callRecursive(state.copy(queue = queue.drop(1), completePaths = completePaths + curr.path))
                    } else {
                        val edges = graph.getEdges(curr)
                            .filterNot { edgeTo -> curr.path.contains(edgeTo.id) && edgeTo is SmallCave }

                        callRecursive(state.copy(queue = queue.drop(1) + edges))
                    }
                }
            }
        }.invoke(initialState)
    }
    .onEach(::println)
    .count()


fun part2(input: File) = loadGraph(input)
    .let { graph ->
        val queue: ArrayDeque<Node> = ArrayDeque(listOf(StartCave("start")))
        val completePaths: MutableSet<Path> = mutableSetOf()

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (curr is EndCave) {
                completePaths.add(curr.path)
            } else {
                val pathNodesToCount = curr.path
                    .filter(String::isSmallCave)
                    .groupingBy { it }
                    .eachCount()

                graph.getEdges(curr)
                    .filterNot { edgeTo ->
                        when (edgeTo) {
                            is StartCave, is EndCave -> curr.path.contains(edgeTo.id)
                            is SmallCave ->
                                curr.path.contains(edgeTo.id) && pathNodesToCount.any { (_, count) -> count >= 2 }
                            is BigCave -> false
                        }
                    }.forEach(queue::add)
            }
        }

        completePaths
    }
    .onEach(::println)
    .count()

fun loadGraph(input: File): Graph = input.readLines()
    .map { it.split("-") }
    .fold(emptyMap()) { graph, (edgeFrom, edgeTo) ->
        graph + mapOf(edgeFrom to setOf(edgeTo)) + mapOf(edgeTo to setOf(edgeFrom))
    }

fun main() {
//    with(loadInput(day = 12, filename = "input_example.txt")) {
//    with(loadInput(day = 12, filename = "input_example2.txt")) {
//    with(loadInput(day = 12, filename = "input_example3.txt")) {
    with(loadInput(day = 12)) {
        println(part1(this))
        println(part2(this))
    }
}