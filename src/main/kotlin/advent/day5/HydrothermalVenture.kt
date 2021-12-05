package advent.day5

import advent.loadInput
import java.io.File
import kotlin.math.abs

/**
 * @author Dominik Hoftych
 */

data class Coordinate(val x: Int, val y: Int)

data class LineSegment(val start: Coordinate, val end: Coordinate)
fun LineSegment.isHorizontalOrVertical() = start.x == end.x || start.y == end.y
fun LineSegment.isDiagonal() =
    abs(start.x - end.x) == abs(start.y - end.y) || abs(start.x - start.y) == abs(end.x - end.y)

fun LineSegment.coveredPoints(): List<Coordinate> {
    val (x1, y1) = start
    val (x2, y2) = end
    val xRange = if (x1 <= x2) x1 .. x2 else x1 downTo x2
    val yRange = if (y1 <= y2) y1 .. y2 else y1 downTo y2
    return when {
        isHorizontalOrVertical() -> xRange.flatMap { x -> yRange.map { y -> Coordinate(x, y) } }
        isDiagonal() -> xRange.zip(yRange).map { (x, y) -> Coordinate(x, y) }
        else -> throw IllegalArgumentException("Invalid segment: $this")
    }
}

fun part1(input: File) = buildSegments(input)
    .filter(LineSegment::isHorizontalOrVertical)
    .also { it.printSegments() }
    .flatMap(LineSegment::coveredPoints)
    .groupingBy { it }
    .eachCount()
    .count { it.value >= 2 }

fun part2(input: File) = buildSegments(input)
    .filter { it.isHorizontalOrVertical() || it.isDiagonal() }
    .also { it.printSegments() }
    .flatMap(LineSegment::coveredPoints)
    .groupingBy { it }
    .eachCount()
    .count { it.value >= 2 }

fun buildSegments(input: File): List<LineSegment> = input.readLines()
    .map { it.split("( -> )|,".toRegex()).map { it.toInt() } }
    .map { (x1, y1, x2, y2) ->
        // columns go first in the input (which is weird) so we have to swap it
        LineSegment(Coordinate(y1, x1), Coordinate(y2, x2))
    }

fun List<LineSegment>.printSegments() {
    val allCoordinates: List<Coordinate> = flatMap { listOf(it.start, it.end) }.distinct()
    val maxX = allCoordinates.maxOf { it.x }
    val maxY = allCoordinates.maxOf { it.y }

    val coveredPointsWithCount: Map<Coordinate, Int> = flatMap { it.coveredPoints() }
        .groupingBy { it }
        .eachCount()

    for (x in 0 .. maxX) {
        for (y in 0 .. maxY) {
            coveredPointsWithCount.getOrDefault(Coordinate(x, y), 0)
                .let { if (it > 0) "$it" else "." }
                .also(::print)
        }
        println()
    }
}

fun main() {
//    with(loadInput(day = 5, filename = "input_example.txt")) {
    with(loadInput(day = 5)) {
        println(part1(this))
        println(part2(this))
    }
}