package advent.day10

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias Stack = ArrayDeque<Char>

fun Char.isOpeningBracket() = listOf('(', '[', '{', '<').contains(this)

infix fun Char.doesNotClose(openingChar: Char) = !(this closes openingChar)
infix fun Char.closes(openingBracket: Char) = openingBracket.findClosingBracket() == this

fun Char.findClosingBracket() = when (this) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> throw IllegalArgumentException("Invalid opening char: $this")
}

val Char.syntaxErrorScore
    get() = when (this) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw IllegalArgumentException("Invalid closing char: $this")
    }

val Char.autocompletionScore: Long
    get() = when (this) {
        ')' -> 1L
        ']' -> 2L
        '}' -> 3L
        '>' -> 4L
        else -> throw IllegalArgumentException("Invalid closing char: $this")
    }

fun part1(input: File) = input.readLines()
    .map { chars ->
        val stack = Stack()
        chars.forEach { curr ->
            when {
                curr.isOpeningBracket() -> stack.add(curr)
                curr closes stack.last() -> stack.removeLast()
                curr doesNotClose stack.last() -> return@map curr // we only care about first illegal char
                else -> throw IllegalArgumentException("Invalid char '$curr' in input line $chars")
            }
        }

        null
    }
    .filterNotNull()
    .sumOf(Char::syntaxErrorScore)

fun part2(input: File) = input.readLines()
    .map { chars ->
        fun recursive(brackets: List<Char>, stack: List<Char> = emptyList()): List<Char> =
            brackets
                .firstOrNull()
                ?.let { bracket ->
                    when {
                        bracket.isOpeningBracket() -> recursive(brackets.drop(1), stack + brackets.first())
                        bracket closes stack.last() -> recursive(brackets.drop(1), stack.dropLast(1))
                        bracket doesNotClose stack.last() -> emptyList()
                        else -> throw IllegalArgumentException("Invalid char '$bracket' in input line $chars")
                    }
                } ?: stack

        recursive(brackets = chars.toCharArray().toList())
    }
    .filter { it.isNotEmpty() }
    .map { unclosedBrackets ->
        unclosedBrackets
            .reversed()
            .map(Char::findClosingBracket)
            .fold(0L) { totalScore, curr -> 5 * totalScore  + curr.autocompletionScore }
    }
    .sorted()
    .let { it[it.size / 2] }


fun main() {
    with(loadInput(day = 10)) {
//    with(loadInput(day = 10, filename = "input_example.txt")) {
        println(part1(this))
        println(part2(this))
    }
}