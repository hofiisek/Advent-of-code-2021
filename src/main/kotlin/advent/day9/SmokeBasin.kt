package advent.day9

import advent.loadInput
import java.io.File
import kotlin.collections.ArrayDeque

/**
 * @author Dominik Hoftych
 */

data class Position(val row: Int, val col: Int)

data class Point(val height: Int, val position: Position) {
    override fun toString() = "Point(height=$height, position=$position)"
}

class Matrix<T>(private val elements: List<List<T>>): List<List<T>> by elements {

    private val rows: Int = elements.size
    private val cols: Int = elements.firstOrNull()?.size ?: 0

    init {
        if (elements.isNotEmpty() && elements.any { row -> row.size != elements.first().size })
            throw IllegalArgumentException("Some rows have different sizes")
    }

    operator fun get(position: Position) =
        if (position.row < 0 || position.col < 0 || position.row >= rows || position.col >= cols)
            null
        else
            this[position.row][position.col]

    fun getOrThrow(position: Position) = get(position)
        ?: throw IllegalArgumentException("Position $position out of bounds")
}

fun <T> Matrix<T>.adjacents(position: Position): Set<T> = listOf(-1, 1)
    .flatMap { listOf(Position(position.row + it, position.col), Position(position.row, position.col + it)) }
    .mapNotNull(this::get)
    .toSet()


/**
 * Just calculate the risk level for all points that are lower than all of their adjacent point
 */
fun part1(input: File) = loadHeightmap(input)
    .let { heightmap ->
        heightmap.flatten()
            .filter { it.height < heightmap.adjacents(it.position).minOf { it.height } }
            .sumOf { it.height + 1 }
    }


data class Basin(val points: Set<Point>) {

    val size: Int
        get() = points.size

    fun contains(point: Point) = points.contains(point)

    override fun toString() = buildString {
        append("Basin(\n\t")
        append(points.joinToString(",\n\t"))
        append(",\n\tlowPoint=${points.minByOrNull { it.height }}")
        append(",\n\tsize=$size")
        append("\n)")
    }
}

data class GraphNode(val point: Point, val edgesTo: Set<Point>) {
    val height: Int by point::height
}

/**
 * Represent the heightmap as a graph where each point is a node with edges to its adjacent
 * points. Then find all basins by running BFS from each node, skipping the highest nodes (nodes with height == 9)
 * and nodes that already belong to some basin.
 */
fun part2(input: File): Int = loadHeightmap(input)
    .let(Matrix<Point>::toGraph)
    .let(Matrix<GraphNode>::detectBasins)
    .sortedBy { it.points.size }
    .takeLast(3)
    .onEach(::println)
    .map { it.size }
    .reduce(Int::times)

fun Matrix<Point>.toGraph(): Matrix<GraphNode> = map { rowPoints ->
    rowPoints.map { point -> GraphNode(point, adjacents(point.position)) }
}.let(::Matrix)


fun Matrix<GraphNode>.detectBasins() = flatten().fold(emptySet<Basin>()) { basins, currentNode ->
    when {
        currentNode.height == 9 -> basins // skip the highest points
        basins.any { it.contains(currentNode.point) } -> basins // skip points that already belong to some basin
        else ->
            // run bfs starting in the current node and add it to the set of basins
            basins + runBfs(queue = ArrayDeque(listOf(currentNode)))
                .map(GraphNode::point)
                .toSet()
                .let(::Basin)
    }
}

fun Matrix<GraphNode>.runBfs(
    queue: ArrayDeque<GraphNode>,
    acc: List<GraphNode> = emptyList()
): List<GraphNode> =
    if (queue.isEmpty()) {
        acc
    } else {
        val curr = queue.removeFirst()
        val suitableAdjacents = curr.edgesTo
            .filter { it.height != 9 } // the highest points are ignored
            .filter { edgeTo -> acc.none { it.point == edgeTo }} // filter out visited nodes
            .filter { edgeTo -> queue.none { it.point == edgeTo }} // filter out open nodes
            .map { getOrThrow(it.position) }

        runBfs(ArrayDeque((queue + suitableAdjacents).toList()), acc + curr)
    }

fun loadHeightmap(input: File): Matrix<Point> = input.readLines()
    .map { it.split("").filterNot(String::isBlank).map(String::toInt) }
    .mapIndexed { rowIdx, row ->
        row.mapIndexed { colIdx, height -> Point(height, Position(rowIdx, colIdx)) }
    }.let(::Matrix)

fun main() {
//    with(loadInput(day = 9, filename = "input_example.txt")) {
    with(loadInput(day = 9)) {
        println(part1(this))
        println(part2(this))
    }
}