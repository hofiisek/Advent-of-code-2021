package advent

data class Position(val row: Int, val col: Int)
fun Position.plus(other: Pair<Int, Int>) = Position(row + other.first, col + other.second)

class Matrix<T>(private val elements: List<List<T>>): List<List<T>> by elements {

    val rows: Int = elements.size
    val cols: Int = elements.firstOrNull()?.size ?: 0

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

fun <T> Matrix<T>.adjacents(position: Position): Set<T> = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
    .map(position::plus)
    .mapNotNull(this::get)
    .toSet()


fun <T> Matrix<T>.adjacentsWithDiagonals(position: Position): Set<T> =
    listOf(
        0 to 1,
        0 to -1,
        1 to 0,
        -1 to 0,
        1 to 1,
        1 to -1,
        -1 to -1,
        -1 to 1
    )
    .map(position::plus)
    .mapNotNull(this::get)
    .toSet()