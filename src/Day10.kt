import kotlin.math.abs
import kotlin.properties.Delegates.notNull

fun main() {


    fun part1(input: MutableList<String>): Int {
        val importantCycles = setOf(20, 60, 100, 140, 180, 220)
        return sequence {
            @Suppress("LocalVariableName") var X = 1
            var readNext = true
            var op: String by notNull()
            for (cycle in 1..220) {
                if (importantCycles.contains(cycle)) {
                    yield(X * cycle)
                }
                if (readNext) {
                    op = input.removeFirst()
                    if (op.startsWith("add")) readNext = false
                } else {
                    readNext = true
                    X += op.split(' ')[1].toInt()
                }
            }
        }.sum()
    }

    fun part2(input: MutableList<String>) {
        @Suppress("LocalVariableName") var X = 1
        var readNext = true
        var op: String by notNull()
        for (cycle in 1..240) {
            val pixel = (cycle - 1) % 40
            if (pixel == 0) print("\n")
            if (abs(X - pixel) <= 1) print('█') else print(' ')
            if (readNext) {
                op = input.removeFirst()
                if (op.startsWith("add")) readNext = false
            } else {
                readNext = true
                X += op.split(' ')[1].toInt()
            }
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput.toMutableList()) == 13140)

    val input = readInput("Day10")
    println(part1(input.toMutableList()))
    part2(testInput.toMutableList())
    println()
    part2(input.toMutableList())
}

