package advent.day6

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

@JvmInline
value class Fish(val age: Int)

fun part1(input: File) = loadFish(input).let(::makeThoseFishHaveRecursiveSex).count()

/**
 * Just accumulate the fish in the list and count them
 */
fun makeThoseFishHaveRecursiveSex(fish: List<Fish> = emptyList(), day: Int = 0): List<Fish> = when (day) {
    80 -> fish
    else -> makeThoseFishHaveRecursiveSex(
        fish = fish.flatMap {
            if (it.age == 0)
                listOf(Fish(6), Fish(8))
            else
                listOf(Fish(it.age - 1))
        },
        day = day + 1
    )
}

fun part2(input: File) = loadFish(input)
    .groupingBy { it.age }
    .eachCount()
    .mapValues { (_, count) -> count.toLong() } // integer would overflow
    .let { (0..8).associateWith { 0L } + it }
    .let(::makeThoseFishHaveEffectiveRecursiveSex)

/**
 * Fish are perverts and in 256 days there's too many fish that a single list can effectively contain :)
 * However, we only need to know the number of fish for each age, which can be represented as a map where
 * the keys are ages and the values are the number of fish of that age.
 */
fun makeThoseFishHaveEffectiveRecursiveSex(ageToCount: Map<Int, Long>, day: Int = 0): Long = when (day) {
    256 -> ageToCount.values.sum()
    else -> ageToCount.entries.associate { (age, _) ->
        val crackedCondomsCount = ageToCount.getValue(0)
        when (age) {
            6 -> age to crackedCondomsCount + ageToCount.getValue(7)
            8 -> age to crackedCondomsCount
            else -> age to ageToCount.getValue(age + 1)
        }
    }
    .let { makeThoseFishHaveEffectiveRecursiveSex(it, day + 1) }
}

fun loadFish(input: File): List<Fish> = input.readLines()
    .first()
    .split(",")
    .map(String::toInt)
    .map(::Fish)

fun main() {
//    with(loadInput(day = 6, filename = "input_example.txt")) {
    with(loadInput(day = 6)) {
        println(part1(this))
        println(part2(this))
    }
}