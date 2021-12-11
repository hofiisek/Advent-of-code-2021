package advent

data class Position(val row: Int, val col: Int)

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