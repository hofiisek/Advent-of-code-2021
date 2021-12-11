package advent.day8

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

/*
  0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg

 Unique-segment-length numbers: 1, 4, 7, 8
   - 2-segment-length: 1
   - 3-segment-length: 7
   - 4-segment-length: 4
   - 8-segment-length: 8
 5-segment-length numbers: 2, 3, 5
 6-segment-length numbers: 0, 6, 9
 */


fun part1(input: File) = input.readLines()
    .map { it.split(" | ") }
    .flatMap { (_, output) -> output.split(" ") }
    .count { listOf(2, 3, 4, 7).contains(it.length) }

fun String.toNumber() = when (this) {
    "abcefg" -> 0
    "cf" -> 1
    "acdeg" -> 2
    "acdfg" -> 3
    "bcdf" -> 4
    "abdfg" -> 5
    "abdefg" -> 6
    "acf" -> 7
    "abcdefg" -> 8
    "abcdfg" -> 9
    else -> throw IllegalArgumentException("Unknown combination of segments: $this")
}

fun String.isNotSingleChar() = length == 0 || length > 1
infix fun String.containsAll(other: String) = other.all { contains(it) }
infix fun String.except(chars: String): String = filterNot { chars.contains(it) }
infix fun String.singleCommonCharWith(other: String): String =
    singleOrNull { other.contains(it) }
        ?.toString()
        ?: throw IllegalArgumentException("$this has more than 1 common chars with $other")

fun Map<Char, String>.getValues(vararg chars: Char): String = chars
    .map(this::get)
    .filterNotNull()
    .distinct()
    .joinToString("")

fun part2(input: File) =
    input.readLines()
        .map { it.split(" | ") }
        .map { (inputStr, outputStr) -> inputStr.split(" ") to outputStr.split(" ") }
        .sumOf { (input, output) ->
            val charToSegment = input
                .sortedBy { it.length }
                .fold(emptyMap<Char, String>()) { charToSegment, signals ->
                    when (signals.length) {
                        2 -> mapOf('c' to signals, 'f' to signals) // number 1
                        3 -> {
                            // number 7
                            val uniqueCharLeft = signals except charToSegment.getValue('c')
                            charToSegment + mapOf('a' to uniqueCharLeft)
                        }
                        4 -> {
                            // number 4
                            val possibleChars = signals except charToSegment.getValue('c')
                            charToSegment + mapOf('b' to possibleChars, 'd' to possibleChars)
                        }
                        5 -> {
                            charToSegment + when {
                                signals containsAll charToSegment.getValues('c', 'f') -> {
                                    // must be number 3
                                    val commonCharWith4 = signals singleCommonCharWith charToSegment.getValues('b', 'd')
                                    val uniqueCharOf4 = charToSegment.getValue('b') except commonCharWith4
                                    val uniqueCharLeft = signals except charToSegment.getValues('a', 'c', 'd', 'f')
                                    mapOf(
                                        'b' to uniqueCharOf4,
                                        'd' to commonCharWith4,
                                        'g' to uniqueCharLeft
                                    )
                                }
                                signals containsAll charToSegment.getValues('b', 'd') -> {
                                    // must be number 5
                                    val comonCharWith1 = signals singleCommonCharWith charToSegment.getValues('c', 'f')
                                    val uniqueCharOf1 = charToSegment.getValue('c') except comonCharWith1
                                    val uniqueCharLeft = signals except charToSegment.getValues('a', 'b', 'd', 'f')
                                    mapOf(
                                        'c' to uniqueCharOf1,
                                        'f' to comonCharWith1,
                                        'g' to uniqueCharLeft
                                    )
                                }
                                else -> {
                                    // must be number 2
                                    val commonCharWith1 = signals singleCommonCharWith charToSegment.getValues('c', 'f')
                                    val commonCharWith4 = signals singleCommonCharWith charToSegment.getValues('b', 'd')
                                    val unambiguousCharsLeft = signals except charToSegment.getValues('a', 'c', 'd')
                                    buildMap {
                                        put('c', commonCharWith1)
                                        put('d', commonCharWith4)

                                        if (charToSegment.getValues('e').isNotSingleChar())
                                            put('e', unambiguousCharsLeft)

                                        if (charToSegment.getValues('g').isNotSingleChar())
                                            put('g', unambiguousCharsLeft)
                                    }
                                }
                            }
                        }
                        6 -> charToSegment + when {
                            signals containsAll charToSegment.getValues('a', 'b', 'c', 'd', 'f') -> {
                                // must be number 9
                                val uniqueCharOfG = signals except charToSegment.getValues('a', 'b', 'c', 'd', 'f')
                                val uniqueCharOfE = (charToSegment.getValue('e') except uniqueCharOfG)
                                mapOf(
                                    'e' to uniqueCharOfE,
                                    'g' to uniqueCharOfG
                                )
                            }
                            else -> emptyMap()

                        }
                        7 -> charToSegment // all chars are known at this point
                        else -> throw IllegalArgumentException("Invalid length of signals: $signals")
                    }
                }
                .map { (segment, chars) -> chars.first() to segment }
                .toMap()

            output
                .map { it.map(charToSegment::getValue).sorted().joinToString("").toNumber() }
                .joinToString("")
                .toInt()
        }

fun main() {
//    with(loadInput(day = 8, filename = "input_example.txt")) {
//    with(loadInput(day = 8, filename = "input_example_single.txt")) {
    with(loadInput(day = 8)) {
        println(part1(this))
        println(part2(this))
    }
}