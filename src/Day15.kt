import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun IntRange.intersects(other: IntRange) = (first <= other.first && last in other.first..other.last) ||
            (last <= other.last && first in other.last..other.first) ||
            (first <= other.first && last >= other.last) ||
            (last <= other.last && first >= other.first)

    fun IntRange.removePoint(point: Int): Sequence<IntRange> =
        when {
            point !in first..last -> sequenceOf(this)
            first == last -> sequenceOf()
            first == point -> sequenceOf(first + 1..last)
            last == point -> sequenceOf(first until last)
            else -> sequenceOf(first until point, point + 1..last)
        }


    fun freeRanges(
        row: Int,
        read: List<Pair<Point15, Point15>>,
    ): Sequence<IntRange> {
        val beacons = hashSetOf<Point15>()
        return read
            .asSequence()
            .map { (beacon, sensor) ->
                val dist = sensor.manhattanDistanceTo(beacon)
                val rowDiff = abs(row - sensor.y)
                val onTheLine = dist * 2 + 1 - 2 * rowDiff
                beacons.add(beacon)
                Pair(sensor.x, onTheLine)
            }
            .filterNot { it.second <= 0 }
            .map { (center, size) ->
                val dx = size / 2
                (center - dx)..(center + dx)
            }
            .sortedBy { it.first }
            .fold(sequenceOf()) { curList: Sequence<IntRange>, nextRange ->
                val candidates = curList.toHashSet()
                val result = curList.toHashSet()
                if (result.none { it.intersects(nextRange) }) return@fold curList + listOf(nextRange)
                var next = candidates.find { it.intersects(nextRange) }
                while (next!=null) {
                    candidates.remove(next)
                    result.remove(next)
                    result.add(min(next.first, nextRange.first)..max(next.last, nextRange.last))
                    next = candidates.find { it.intersects(nextRange) }
                }
                return@fold result.asSequence()
            }
    }

    fun inputToData(input: List<String>) = input
        .asSequence()
        .map { Regex("-?\\d+").findAll(it).map { it.value.toInt() }.toList() }
        .filterNot { it.isEmpty() }
        .map { (a, b, c, d) -> Point15(c, d) to Point15(a, b) }
        .toList()

    fun part1(input: List<String>, row: Int): Int {

        val beaconsToSensors = inputToData(input)
        val freeRanges = freeRanges(row, beaconsToSensors)
        return freeRanges
            .flatMap { range ->
                beaconsToSensors
                    .map(Pair<Point15, Point15>::first)
                    .distinct()
                    .filter { it.y == row }
                    .map(Point15::x)
                    .flatMap(range::removePoint)
                    .takeIf { it.isNotEmpty() } ?: listOf(range)
            }
            .map { it.also(::println) }
            .sumOf { it.last - it.first + 1 }
    }

    fun part2(input: List<String>, row: Int): Long {
        val read = inputToData(input)
        return (0..row)
            .asSequence()
            .map { curRow ->
                val rangeList = freeRanges(curRow, read).toList()
                if (rangeList.size > 1) {
                    val x = rangeList[0].last + 1
                    return@map x.toLong() * 4000000 + curRow
                } else return@map 0
            }
            .first { it != 0L }
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    println()
    val input = readInput("Day15")
    println(part1(input, 2000000))
    println()
    println(part2(input, 4000000))

}

data class Point15(val x: Int, val y: Int) {
    fun manhattanDistanceTo(other: Point15) = abs(x - other.x) + abs(y - other.y)
}