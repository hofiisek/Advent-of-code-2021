package main.kotlin.advent.day2

import java.io.File

/**
 * @author Dominik Hoftych
 */

data class Position(val horizontalPos: Int = 0, val depth: Int = 0)
data class PositionWithAim(val horizontalPos: Int = 0, val depth: Int = 0, val aim: Int = 0)

sealed class Move(open val step: Int)
data class Up(override val step: Int): Move(step)
data class Down(override val step: Int): Move(step)
data class Forward(override val step: Int): Move(step)

infix fun Position.apply(move: Move) = when (move) {
    is Up -> copy(depth = depth - move.step)
    is Down -> copy(depth = depth + move.step)
    is Forward -> copy(horizontalPos = horizontalPos + move.step)
}

infix fun PositionWithAim.apply(move: Move) = when (move) {
    is Up -> copy(aim = aim - move.step)
    is Down -> copy(aim = aim + move.step)
    is Forward -> copy(horizontalPos = horizontalPos + move.step, depth = depth + (aim * move.step))
}

fun part1(input: File) = input.readLines()
    .map {
        val (dir, step) = it.split(" ")
        when (dir) {
            "up" -> Up(step.toInt())
            "down" -> Down(step.toInt())
            "forward" -> Forward(step.toInt())
            else -> throw IllegalArgumentException("Unknown direction: $dir")
        }
    }.fold(Position()) { currentPos, nextMove ->
        currentPos apply nextMove
    }
    .let { it.horizontalPos * it.depth }
    .also(::println)


fun part2(input: File) = input.readLines()
    .map {
        val (dir, step) = it.split(" ")
        when (dir) {
            "up" -> Up(step.toInt())
            "down" -> Down(step.toInt())
            "forward" -> Forward(step.toInt())
            else -> throw IllegalArgumentException("Unknown direction: $dir")
        }
    }.fold(PositionWithAim()) { currentPos, nextMove ->
        currentPos apply nextMove
    }
    .let { it.horizontalPos * it.depth }
    .also(::println)

fun main() {
    with (File(object {}.javaClass.getResource("input.txt").toURI())) {
        part1(this)
        part2(this)
    }
}