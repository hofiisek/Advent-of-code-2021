package advent.day10

import advent.loadInput
import java.io.File

/**
 * @author Dominik Hoftych
 */

typealias Stack = ArrayDeque<Char>

fun Char.isOpeningChar() = listOf('(', '[', '{', '<').contains(this)
fun Char.isClosingChar() = listOf(')', ']', '}', '>').contains(this)

infix fun Char.doesNotClose(openingChar: Char) = !(this closes openingChar)
infix fun Char.closes(openingChar: Char) = isClosingChar() and when (this) {
    ')' -> openingChar == '('
    ']' -> openingChar == '['
    '}' -> openingChar == '{'
    '>' -> openingChar == '<'
    else -> throw IllegalArgumentException("Invalid closing char: $this")
}

fun Char.findClosingChar() = when (this) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> throw IllegalArgumentException("Invalid opening char: $this")
}

fun Char.syntaxErrorScore() = when (this) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> throw IllegalArgumentException("Invalid closing char: $this")
}

fun Char.autocompletionScore(): Long = when (this) {
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
                curr.isOpeningChar() -> stack.add(curr)
                curr closes stack.last() -> stack.removeLast()
                !(curr closes stack.last()) -> return@map curr // we only care about first illegal char
                else -> throw IllegalArgumentException("Invalid char '$curr' in input line $chars")
            }
        }

        null
    }
    .filterNotNull()
    .sumOf(Char::syntaxErrorScore)

fun part2(input: File) = input.readLines()
    .map { chars ->
        val stack = Stack()
        chars.forEach { curr ->
            when {
                curr.isOpeningChar() -> stack.add(curr)
                curr closes stack.last() -> stack.removeLast()
                curr doesNotClose stack.last() -> return@map emptyList() // discard corrupted lines
                else -> throw IllegalArgumentException("Invalid char '$curr' in input line $chars")
            }
        }

        stack
    }
    .filter { it.isNotEmpty() }
    .map { unclosedChars ->
        unclosedChars
            .reversed()
            .map(Char::findClosingChar)
            .fold(0L) { totalScore, curr -> 5 * totalScore  + curr.autocompletionScore() }
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