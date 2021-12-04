package advent.day1

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

fun part1(input: File) = input.readLines()
    .map { it.toInt() to 0 }
    .reduce { (prev, count), (next, _) ->
        if (next > prev)
            next to count + 1
        else
            next to count
    }.also {
        println(it.second)
    }

fun part2(input: File) = input.readLines()
    .map { it.toInt() }
    .windowed(3, 1)
    .map { it.sum() to 0 }
    .reduce { (prev, count), (next, _) ->
        if (next > prev)
            next to count + 1
        else
            next to count
    }.also {
        println(it.second)
    }

fun main() {
    with(loadInput(day = 1)) {
        part1(this)
        part2(this)
    }
}