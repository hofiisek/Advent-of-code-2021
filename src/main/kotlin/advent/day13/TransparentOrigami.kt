package advent.day13

import advent.Matrix
import advent.Position
import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias Paper = Matrix<PaperPosition>

fun Paper.foldUp(foldRowIdx: Int): Paper {
    return (0 until foldRowIdx).map { row ->
        (0 until cols).map { col ->
            val position = Position(row, col)
            val bottomPosition = Position(foldRowIdx * 2 - row, col)
            if (get(position) is Dot || get(bottomPosition) is Dot)
                Dot(position)
            else
                Empty(position)
        }
    }.let(::Paper)
}

fun Paper.foldLeft(foldColIdx: Int): Paper {
    return (0 until rows).map { row ->
        (0 until foldColIdx).map { col ->
            val position = Position(row, col)
            val rightPosition = Position(row, foldColIdx * 2 - col)
            if (get(position) is Dot || get(rightPosition) is Dot)
                Dot(position)
            else
                Empty(position)
        }
    }.let(::Matrix)
}

fun Paper.print() = joinToString(separator = "\n") { row ->
    row.joinToString("") { position -> if (position is Dot) "#" else "." }
}.also(::println)

sealed class PaperPosition(open val position: Position)
data class Dot(override val position: Position) : PaperPosition(position)
data class Empty(override val position: Position) : PaperPosition(position)

sealed class FoldInstruction(open val foldIdx: Int)
data class FoldUp(override val foldIdx: Int) : FoldInstruction(foldIdx)
data class FoldLeft(override val foldIdx: Int) : FoldInstruction(foldIdx)

fun part1(input: File) = input.readLines()
    .let { lines ->
        val initialPaper = parseInitialPaper(lines)
        val foldInstructions = parseFoldInstructions(lines)

        foldInstructions.take(1).fold(initialPaper) { paper, instruction ->
            when (instruction) {
                is FoldUp -> paper.foldUp(instruction.foldIdx)
                is FoldLeft -> paper.foldLeft(instruction.foldIdx)
            }
        }
    }
    .also(Paper::print)
    .sumOf { row -> row.count { it is Dot } }

fun part2(input: File) = input.readLines()
    .let { lines ->
        val initialPaper = parseInitialPaper(lines)
        val foldInstructions = parseFoldInstructions(lines)

        foldInstructions.fold(initialPaper) { paper, instruction ->
            when (instruction) {
                is FoldUp -> paper.foldUp(instruction.foldIdx)
                is FoldLeft -> paper.foldLeft(instruction.foldIdx)
            }
        }
    }
    .also(Paper::print)
    .sumOf { row -> row.count { it is Dot } }
    .also(::println)

fun parseInitialPaper(lines: List<String>): Paper = lines
    .filterNot { it.isBlank() }
    .filterNot { it.startsWith("fold") }
    .map { it.split(",").map { it.toInt() } }
    .map { (col, row) -> Position(row, col) }
    .let { dotPositions ->
        val maxRow = dotPositions.maxOf { it.row }
        val maxCol = dotPositions.maxOf { it.col }
        (0..maxRow).map { row ->
            (0..maxCol).map { col ->
                val position = Position(row, col)
                if (position in dotPositions)
                    Dot(position)
                else
                    Empty(position)
            }
        }
    }
    .let(::Paper)

fun parseFoldInstructions(lines: List<String>) = lines
    .filter { it.startsWith("fold") }
    .map { it.removePrefix("fold along ").split("=") }
    .map { (axis, idx) ->
        when (axis) {
            "x" -> FoldLeft(idx.toInt())
            "y" -> FoldUp(idx.toInt())
            else -> throw IllegalArgumentException("Invalid axis to fold along: $axis")
        }
    }

fun main() {
    with(loadInput(day = 13)) {
//    with(loadInput(day = 13, filename = "input_example.txt")) {
        println(part1(this))
        part2(this)
    }
}