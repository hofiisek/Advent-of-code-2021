package advent.day4

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

fun part1(input: File) {
    val inputLines = input.readLines()
    val numbers = inputLines.first().split(",").map { it.toInt() }
    val boards = constructBoards(inputLines.drop(1))

    numbers.forEach { number ->
        boards.forEach { board ->
            board.markValueIfPresent(number)
            if (board.bingo()) {
                println(number * board.sumAllUnmarked())
                return
            }
        }
    }
}

fun part2(input: File) {
    val inputLines = input.readLines()
    val numbers = inputLines.first().split(",").map { it.toInt() }
    val boards = constructBoards(inputLines.drop(1))

    val winningBoardScores: LinkedHashMap<Int, Int> = linkedMapOf()
    numbers.forEach { number ->
        boards.forEachIndexed { idx, board ->
            when {
                winningBoardScores.contains(idx) -> return@forEachIndexed
                else -> {
                    board.markValueIfPresent(number)
                    if (board.bingo()) {
                        winningBoardScores[idx] = number * board.sumAllUnmarked()
                    }
                }
            }
        }
    }

    println(winningBoardScores.values.lastOrNull() ?: throw IllegalArgumentException("No winning board"))
}

fun constructBoards(inputLines: List<String>): List<Board> = inputLines
    .filter { it.isNotBlank() }
    .chunked(5)
    .map { chunk ->
        // chunk of 5 rows as strings
        chunk.map { rowStr ->
            rowStr.split("\\s+".toRegex())
                .filter { it.isNotBlank() }
                .map { Cell(it.toInt()) }
        }
    }.map(::Board)

fun main() {
//    with(loadInput(day = 4, filename = "input_example.txt")) {
    with(loadInput(day = 4)) {
        part1(this)
        part2(this)
    }
}