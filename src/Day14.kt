import kotlin.math.max
import kotlin.math.min

fun main() {
    operator fun Pair<Int, Int>.rangeTo(other: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val minX = min(first, other.first)
        val maxX = max(first, other.first)
        val minY = min(second, other.second)
        val maxY = max(second, other.second)
        return buildSet {
            for (i in minX..maxX) {
                for (j in minY..maxY) {
                    add(i to j)
                }
            }
        }

    }

    fun Pair<Int, Int>.tryMove(occupied: HashSet<Pair<Int, Int>>): Pair<Int, Int> {
        val down = copy(second = second + 1)
        return if (!occupied.contains(down)) down
        else {
            val downLeft = copy(first = first - 1, second = second + 1)
            if (!occupied.contains(downLeft)) downLeft
            else {
                val downRight = copy(first = first + 1, second = second + 1)
                if (!occupied.contains(downRight)) downRight
                else this
            }
        }
    }

    fun List<String>.buildMap(): HashSet<Pair<Int, Int>> {
        return map { it.split(Regex(",| -> ")).chunked(2).map { (a, b) -> a.toInt() to b.toInt() } }
            .flatMap { it.windowed(2).flatMap { (a, b) -> a..b } }
            .toHashSet()
    }

    fun part1(input: List<String>): Int {
        val occupied = input.buildMap()
        val bottomLine = occupied.maxBy { it.second }.second
        var counter = 0
        outer@ while (true) {
            counter++
            var grain = 500 to 0
            while (true) {
                val next = grain.tryMove(occupied)
                if (next == grain) {
                    occupied.add(next)
                    break
                } else if (next.second > bottomLine) {
                    counter--
                    break@outer
                } else {
                    grain = next
                }
            }
        }
        return counter
    }


    fun part2(input: List<String>): Int {
        val occupied = input.buildMap()
        val bottomLine = occupied.maxBy { it.second }.second
        var counter = 0
        val start = 500 to 0
        outer@ while (true) {
            counter++
            var grain = start
            while (true) {
                val next = grain.tryMove(occupied)
                if (next == grain) {
                    occupied.add(next)
                    if (next == start) break@outer
                    break
                } else if (next.second > bottomLine) {
                    occupied.add(next)
                    break
                } else {
                    grain = next
                }
            }
        }
        return counter

    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)

    val input = readInput("Day14")
    println(part1(input))
    check(part2(testInput) == 93)
    println(part2(input))
}
