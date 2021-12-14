package advent.day14

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias PairInsertionRules = Map<String, PairInsertionRule>

data class PairInsertionRule(val pair: String, val charToInsert: String)

fun part1(input: File) = input.readLines()
    .filter(String::isNotBlank)
    .let { it.first() to it.drop(1) }
    .let { (polymerTemplate, insertionRules) ->
        val rules: PairInsertionRules = insertionRules
            .map { it.split(" -> ") }
            .map { (pair, charToInsert) -> PairInsertionRule(pair, charToInsert) }
            .associateBy { it.pair }

        runPolymerization(polymerTemplate, rules, 10)
            .groupingBy { it }
            .eachCount()
            .let { charsToCount ->
                charsToCount.maxOf { it.value } - charsToCount.minOf { it.value }
            }
    }

fun part2(input: File) = input.readLines()
    .filter(String::isNotBlank)
    .let { it.first() to it.drop(1) }
    .let { (polymerTemplate, insertionRules) ->
        val rules: PairInsertionRules = insertionRules
            .map { it.split(" -> ") }
            .map { (pair, charToInsert) -> PairInsertionRule(pair, charToInsert) }
            .associateBy { it.pair }

        runPolymerization(polymerTemplate, rules, 40)
            .groupingBy { it }
            .eachCount()
            .let { charsToCount ->
                charsToCount.maxOf { it.value } - charsToCount.minOf { it.value }
            }
    }

fun runPolymerization(
    currentPolymer: String,
    rules: PairInsertionRules,
    maxSteps: Int,
    step: Int = 1
): String = when {
    step > maxSteps -> currentPolymer
    else -> currentPolymer
        .windowed(size = 2)
        .map { pair ->
            when (val charToInsert = rules[pair]?.charToInsert) {
                null -> pair.first()
                else -> {
                    pair.first() + charToInsert
                }
            }
        }
        .joinToString("")
        .let { polymer -> runPolymerization(polymer + currentPolymer.last(), rules, maxSteps, step + 1) }
}

fun main() {
//    with(loadInput(day = 14)) {
    with(loadInput(day = 14, filename = "input_example.txt")) {
        println(part1(this))
        println(part2(this))
    }
}