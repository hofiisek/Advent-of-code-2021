package main.kotlin.advent.day3

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

fun part1(input: File) {
    val lines = input.readLines()
    val numBits = lines.firstOrNull()?.length ?: throw IllegalArgumentException("Empty input")

    lines
        .map { bits -> bits.map { it.digitToInt() } } // string to int array
        .fold(List(numBits) { 0 }) { acc, curr ->
            // sum bits on each position, positive = 1, negative = -1
            curr.mapIndexed { idx, bit ->
                when (bit) {
                    0 -> acc[idx] - 1
                    1 -> acc[idx] + 1
                    else -> throw IllegalArgumentException("Not a bit :D")
                }
            }
        }
        .let { sums ->
            // convert sum back to bits, negative sum = 0, positive sum = 1
            val gammaBits = sums.joinToString(separator = "") { sum -> if (sum < 0) "0" else "1" }
            val epsilonBits = sums.joinToString(separator = "") { sum -> if (sum < 0) "1" else "0" }

            gammaBits.toInt(2) * epsilonBits.toInt(2)
        }
        .also(::println)
}

fun part2(input: File) {
    val lines = input.readLines().map { bits -> bits.map { it.digitToInt() } }
    val numBits = lines.firstOrNull()?.size ?: throw IllegalArgumentException("Empty input")

    var oxygenLines = lines
    for (i in 0 until numBits) {
        oxygenLines = oxygenLines
            .map { it[i] }
            .let { bits ->
                // alternative way than in part one - no need to convert 0 to -1
                val isPositiveSum = bits.sum() >= oxygenLines.size/2.0
                oxygenLines.filter { if (isPositiveSum) it[i] == 1 else it[i] == 0 }
            }
            .also(::println)

        if (oxygenLines.size == 1){
            break
        }
    }

    var co2lines = lines
    for (i in 0 until numBits) {
        co2lines = co2lines
            .map { it[i] }
            .let { bits ->
                val positiveSum = bits.sum() >= co2lines.size/2.0
                co2lines.filter { if (positiveSum) it[i] == 0 else it[i] == 1 }
            }
            .also(::println)

        if (co2lines.size == 1){
            break
        }
    }

    val oxygen = oxygenLines.first().joinToString(separator = "").toInt(2)
    val co2 = co2lines.first().joinToString(separator = "").toInt(2)
    println(oxygen)
    println(co2)
    println(oxygen * co2)
}

fun main() {
    with(loadInput(day = 3)) {
        part1(this)
        part2(this)
    }
}