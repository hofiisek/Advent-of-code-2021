package advent.day4

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

fun part1(input: File): Int {
    val inputLines = input.readLines()
    val numbers = inputLines.first().split(",").map { it.toInt() }
    val boards = constructBoards(inputLines.drop(1))

    numbers.forEach { number ->
        boards.forEach { board ->
            board.markValueIfPresent(number)
            if (board.bingo()) {
                return number * board.sumAllUnmarked()
            }
        }
    }

    throw IllegalArgumentException("No winning board")
}

fun part2(input: File): Int {
    val inputLines = input.readLines()
    val numbers = inputLines.first().split(",").map { it.toInt() }
    val boards = constructBoards(inputLines.drop(1))

    val winningBoardScores = linkedMapOf<Board, Int>()
    numbers.forEach { number ->
        boards.forEach { board ->
            when {
                winningBoardScores.contains(board) -> return@forEach
                else -> {
                    board.markValueIfPresent(number)
                    if (board.bingo()) {
                        winningBoardScores[board] = number * board.sumAllUnmarked()
                    }
                }
            }
        }
    }

    return winningBoardScores.values.lastOrNull() ?: throw IllegalArgumentException("No winning board")
}

fun constructBoards(inputLines: List<String>): List<Board> = inputLines
    .filter { it.isNotBlank() }
    .chunked(5)
    .map { board ->
        // chunk of 5 rows as strings
        board.map { rowStr ->
            rowStr.split("\\s+".toRegex())
                .filter { it.isNotBlank() }
                .map { Cell(it.toInt()) }
        }
    }.map(::Board)

fun main() {
//    with(loadInput(day = 4, filename = "input_example.txt")) {
    with(loadInput(day = 4)) {
        println(part1(this))
        println(part2(this))
    }
}