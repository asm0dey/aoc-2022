import kotlin.math.abs
import kotlin.math.sign

data class Point09(val x: Int, val y: Int) {
    infix fun notTouches(other: Point09): Boolean = !(abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1)
    operator fun plus(other: Point09): Point09 = copy(x = x + other.x, y = y + other.y)
}

fun main() {

    infix fun Point09.moveCloserTo(other: Point09) =
        if (this notTouches other)
            copy(x = x + (other.x - x).sign, y = y + (other.y - y).sign)
        else this

    fun Char.asMovementVector() = when (this) {
        'R' -> Point09(1, 0)
        'L' -> Point09(-1, 0)
        'U' -> Point09(0, 1)
        'D' -> Point09(0, -1)
        else -> throw UnsupportedOperationException(toString())
    }

    fun part1(input: List<String>): Int {
        var head = Point09(0, 0)
        var tail = Point09(0, 0)
        return input
            .map { it.split(' ') }
            .filter { it.size == 2 }
            .map { it[0][0] to it[1].toInt() }
            .flatMap { (direction, amount) ->
                (0 until amount).map {
                    head += direction.asMovementVector()
                    tail = tail moveCloserTo head
                    tail
                }
            }
            .distinct()
            .size
    }

    fun part2(input: List<String>, size: Int): Int {
        val knots = Array(size) { Point09(0, 0) }
        return input
            .map { it.split(' ') }
            .filter { it.size == 2 }
            .map { it[0][0] to it[1].toInt() }
            .flatMap { (direction, amount) ->
                (0 until amount).map {
                    knots[0] += direction.asMovementVector()
                    for (i in knots.indices.drop(1)) {
                        knots[i] = knots[i] moveCloserTo knots[i - 1]
                    }
                    knots.last()
                }
            }
            .distinct()
            .size
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input, 10))
    check(part1(input) == part2(input, 2))
}
