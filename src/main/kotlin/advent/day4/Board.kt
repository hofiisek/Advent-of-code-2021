package advent.day4

data class Cell(val value: Int, var marked: Boolean = false)

data class Board(
    val rows: List<List<Cell>>,
    val cols: List<List<Cell>> = (0 until 5).map { rowIdx ->
        (0 until 5).map { colIdx ->
            rows[colIdx][rowIdx]
        }
    },
    val cells: List<Cell> = rows.flatten()
)

fun Board.markValueIfPresent(value: Int) = cells
    .firstOrNull { it.value == value }
    ?.let { it.marked = true }

fun Board.bingo() = rows.any { row -> row.all { it.marked } } || cols.any { col -> col.all { it.marked } }

fun Board.sumAllUnmarked() = cells.filterNot { it.marked }.sumOf { it.value }
