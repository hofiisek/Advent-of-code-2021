package advent.day9

import advent.loadInput
import java.io.File
import kotlin.collections.ArrayDeque

/**
 * @author Dominik Hoftych
 */

data class Position(val row: Int, val col: Int)

data class Point(val height: Int, val position: Position, var basin: Basin? = null) {
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
}

fun <T> Matrix<T>.adjacents(position: Position): List<T> = listOf(-1, 1)
    .flatMap { listOf(Position(position.row + it, position.col), Position(position.row, position.col + it)) }
    .mapNotNull(this::get)


fun part1(input: File) = loadHeightmap(input)
    .let { heightmap ->
        heightmap.flatten()
            .filter { it.height < heightmap.adjacents(it.position).minOf { it.height } }
            .sumOf { it.height + 1 }
    }


data class Basin(val points: List<Point>) {

    val size: Int
        get() = points.size

    override fun toString() = buildString {
        append("Basin(\n\t")
        append(points.joinToString(",\n\t"))
        append(",\n\tlowPoint=${points.minByOrNull { it.height }}")
        append(",\n\tsize=$size")
        append("\n)")
    }
}

data class GraphNode(val point: Point, val edgesTo: List<Point>) {
    val height: Int by point::height
    val position: Position by point::position
    val basin: Basin? by point::basin
}

fun part2(input: File): Int = loadHeightmap(input)
    .let(::Matrix)
    .let(Matrix<Point>::toGraph)
    .let(Matrix<GraphNode>::detectBasins)
    .sortedBy { it.points.size }
    .takeLast(3)
    .onEach(::println)
    .map { it.size }
    .reduce(Int::times)

fun Matrix<Point>.toGraph(): Matrix<GraphNode> =
    map { rowPoints -> rowPoints.map { point -> GraphNode(point, adjacents(point.position)) } }.let(::Matrix)


fun Matrix<GraphNode>.detectBasins() = flatten().fold(emptyList<Basin>()) { basins, graphNode ->
    when {
        graphNode.height == 9 -> basins // skip the highest points
        graphNode.basin != null -> basins // point already belongs to some basin
        else -> {
            val basin: Basin = runBfs(queue = ArrayDeque(listOf(graphNode)))
                .map { it.point }
                .let(::Basin)
                .also { basin ->
                    basin.points.forEach { it.basin = basin }
                }
            basins + basin
        }
    }
}

fun Matrix<GraphNode>.runBfs(
    queue: ArrayDeque<GraphNode>, // current element in initially in queue
    acc: List<GraphNode> = emptyList()
): List<GraphNode> =
    if (queue.isEmpty()) {
        acc
    } else {
        val curr = queue.removeFirst()
        val suitableAdjacents = curr.edgesTo
            .filter { it.height != 9 } // the highest points are ignored
            .filter { edgeTo -> acc.none { it.point == edgeTo }} // filter out visited nodes
            .filter { edgeTo -> queue.none { it.point == edgeTo }} // filter out processed nodes
            .map {
                this[it.position]
                    ?: throw IllegalArgumentException("Position ${it.position} out of bounds")
            }

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