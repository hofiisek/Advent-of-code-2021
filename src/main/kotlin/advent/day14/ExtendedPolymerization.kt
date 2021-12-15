package advent.day14

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias PairInsertionRules = Map<String, String>

fun <K> MutableMap<K, Long>.putOrSumValue(key: K, count: Long) =
    compute(key) { _, currCount -> (currCount ?: 0) + count }

fun <T, K> Grouping<T, K>.eachCountLong(): Map<K, Long> = eachCount().map { (k, v) -> k to v.toLong() }.toMap()

fun String.toChar() = single()

fun part1(input: File) = input.readLines()
    .filter(String::isNotBlank)
    .let { it.first() to it.drop(1) }
    .let { (polymerTemplate, insertionRules) ->
        val rules: PairInsertionRules = insertionRules
            .map { it.split(" -> ") }
            .associate { (pair, charToInsert) -> pair to charToInsert }

        runPolymerizationNaive(polymerTemplate, rules, 10)
            .groupingBy { it }
            .eachCount()
            .let { charsToCount ->
                charsToCount.maxOf { it.value } - charsToCount.minOf { it.value }
            }
    }

fun runPolymerizationNaive(
    currentPolymer: String,
    rules: PairInsertionRules,
    maxSteps: Int,
    step: Int = 1
): String = when {
    step > maxSteps -> currentPolymer
    else -> currentPolymer
        .windowed(size = 2)
        .map { pair ->
            when (val charToInsert = rules[pair]) {
                null -> pair.first()
                else -> pair.first() + charToInsert
            }
        }
        .joinToString("")
        .let { polymer -> runPolymerizationNaive(polymer + currentPolymer.last(), rules, maxSteps, step + 1) }
}

fun part2(input: File) = input.readLines()
    .filter(String::isNotBlank)
    .let { it.first() to it.drop(1) }
    .let { (polymerTemplate, insertionRules) ->
        val rules: PairInsertionRules = insertionRules
            .map { it.split(" -> ") }
            .associate { (pair, charToInsert) -> pair to charToInsert }

        var pairsCount: Map<String, Long> = polymerTemplate
            .windowed(size = 2)
            .groupingBy { it }
            .eachCountLong()
            .toMap()

        val charsCount: MutableMap<Char, Long> = polymerTemplate
            .groupingBy { it }
            .eachCountLong()
            .toMutableMap()

        val steps = 40

        // literally no idea how to do this functionally and immutable :(
        repeat(steps) { step ->
            println("step: ${step + 1}, pairs: $pairsCount, chars: $charsCount")

            val updatedPairsCount = mutableMapOf<String, Long>()
            pairsCount.forEach { (pair, count) ->
                when (val charToInsert = rules[pair]) {
                    null -> updatedPairsCount.putOrSumValue(pair, count)
                    else -> {
                        listOf(pair[0] + charToInsert, charToInsert + pair[1])
                            .forEach { updatedPairsCount.putOrSumValue(it, count) }

                        charsCount.putOrSumValue(charToInsert.toChar(), count)
                    }
                }
            }

            pairsCount = updatedPairsCount
        }

        charsCount
    }
    .let { it.maxOf { it.value } - it.minOf { it.value } }


fun main() {
    with(loadInput(day = 14)) {
//    with(loadInput(day = 14, filename = "input_example.txt")) {
        println(part1(this))
        println(part2(this))
    }
}