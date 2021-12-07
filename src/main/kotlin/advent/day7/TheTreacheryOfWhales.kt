package advent.day7

import advent.loadInput
import java.io.File
import kotlin.math.abs

/**
 * @author Dominik Hoftych
 */

fun part1(input: File) = input.readLines()
    .firstOrNull()
    ?.split(",")
    ?.map(String::toInt)
    ?.let { positions ->
        positions.minOf { pos ->
            positions.sumOf { abs(it - pos) }
        }
    }
    ?: throw IllegalArgumentException("Invalid input")

fun part2(input: File) = input.readLines()
    .firstOrNull()
    ?.split(",")
    ?.map(String::toInt)
    ?.let { crabPositions ->
        val allPositions = crabPositions.minOf { it } .. crabPositions.maxOf { it }
        allPositions.minOf { pos ->
            crabPositions.sumOf { otherPos -> (1 .. abs(otherPos - pos)).sum() }
        }
    }
    ?: throw IllegalArgumentException("Invalid input")

fun main() {
//    with(loadInput(day = 7, filename = "input_example.txt")) {
    with(loadInput(day = 7)) {
        println(part1(this))
        println(part2(this))
    }
}