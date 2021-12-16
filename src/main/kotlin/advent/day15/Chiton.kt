package advent.day15

import advent.Matrix
import advent.Position
import advent.adjacents
import advent.loadInput
import java.io.File
import java.util.*

/**
 * @author Dominik Hoftych
 */

data class CavePoint(val riskLevel: Int, val position: Position)

data class GraphNode(
    val cavePoint: CavePoint,
    val edgesTo: Set<CavePoint>,
    var riskLevel: Int = cavePoint.riskLevel,
    var visited: Boolean = false,
    val path: MutableSet<CavePoint> = mutableSetOf()
) {
    val position: Position by cavePoint::position
}

fun Matrix<CavePoint>.toGraph(): Matrix<GraphNode> = map { rowPoints ->
    rowPoints.map { cavePoint -> GraphNode(cavePoint, adjacents(cavePoint.position)) }
}.let(::Matrix)

val <K> Matrix<K>.upperLeft: K
    get() = this[0][0]

val <K> Matrix<K>.bottomRight: K
    get() = this[rows - 1][cols - 1]

fun Matrix<CavePoint>.print() = joinToString(separator = "\n") { row ->
    row.joinToString("") { it.riskLevel.toString() }
}.also(::println)

fun <E> priorityQueueOf(element: E, comparator: Comparator<E>) = PriorityQueue(comparator).apply { add(element) }
operator fun <E> PriorityQueue<E>.plus(elements: Collection<E>) = apply { addAll(elements) }


fun part1(input: File) = loadCavePoints(input)
    .let(Matrix<CavePoint>::toGraph)
    .let(Matrix<GraphNode>::dodgeThoseChitons)
    .riskLevel

fun part2(input: File) = loadCavePoints(input)
    .let { matrix ->
        val newColsNumber = 5 * matrix.cols
        val newRowsNumber = 5 * matrix.rows

        val rightExpandedMatrix = matrix.map { row ->
            generateSequence(row) { rowPoints ->
                rowPoints.map { point ->
                    val newRiskLevel = (point.riskLevel % 9) + 1
                    val newRow = point.position.row
                    val newCol = (point.position.col + matrix.cols) % newColsNumber
                    CavePoint(newRiskLevel, Position(newRow, newCol))
                }
            }.take(5).toList().flatten()
        }

        val downExpandedMatrix = generateSequence(rightExpandedMatrix) { expandedMatrix ->
            expandedMatrix.mapIndexed { rowIdx, row ->
                row.mapIndexed { colIdx, point ->
                    val newRiskLevel = (expandedMatrix[rowIdx][colIdx].riskLevel % 9) + 1
                    val newRow = (point.position.row + matrix.rows) % newRowsNumber
                    val newCol = point.position.col
                    CavePoint(newRiskLevel, Position(newRow, newCol))
                }
            }
        }.take(5).toList().flatten()

        downExpandedMatrix
    }
    .let(::Matrix)
    .also { it.print() }
    .let(Matrix<CavePoint>::toGraph)
    .let(Matrix<GraphNode>::dodgeThoseChitons)
    .riskLevel

fun Matrix<GraphNode>.dodgeThoseChitons(): GraphNode {
    // risk level is added only when a node is entered, so starting node has zero risk level
    val start: GraphNode = upperLeft.copy(riskLevel = 0)
    val queue: PriorityQueue<GraphNode> = priorityQueueOf(start, compareBy { it.riskLevel })

    while (queue.isNotEmpty()) {
        when (queue.first().position) {
            bottomRight.position -> return queue.first()
            else -> {
                val curr = queue.remove()
                curr.edgesTo
                    .filterNot { edgeTo -> edgeTo in curr.path } // filter out visited nodes
                    .map { edgeTo -> getOrThrow(edgeTo.position) }
                    .filterNot(GraphNode::visited) // filter out open nodes
                    .map { node ->
                        node.apply {
                            riskLevel += curr.riskLevel
                            visited = true
                            path.add(node.cavePoint)
                        }
                    }
                    .forEach(queue::add)
            }
        }
    }

    throw IllegalArgumentException("No solution")
}

fun loadCavePoints(input: File): Matrix<CavePoint> = input.readLines()
    .map { it.split("").filterNot(String::isBlank).map(String::toInt) }
    .mapIndexed { rowIdx, row ->
        row.mapIndexed { colIdx, riskLevel -> CavePoint(riskLevel, Position(rowIdx, colIdx)) }
    }.let(::Matrix)


fun main() {
    with(loadInput(day = 15)) {
//    with(loadInput(day = 15, filename = "input_example.txt")) {
        println(part1(this))
        println(part2(this))
    }
}