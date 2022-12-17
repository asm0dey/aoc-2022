val HashSet<Point17>.top
    get() = if (isEmpty()) -1 else maxOf(Point17::y)

fun main() {


    fun windVector(wind: Iterator<Char>): Point17 {
        val windVector = when (wind.next()) {
            '>' -> Point17(1, 0)
            '<' -> Point17(-1, 0)
            else -> error("Unsupported wind speed")
        }
        return windVector
    }

    fun windIterator(input: String): Iterator<Char> {
        val wind = iterator {
            while (true) {
                yieldAll(input.toList())
            }
        }
        return wind
    }

    fun shape17Iterator() = iterator {
        val x = buildList {
            add(Shape17(0, 0, 1, 0, 2, 0, 3, 0))
            add(Shape17(1, 0, 0, 1, 1, 1, 2, 1, 1, 2))
            add(Shape17(0, 0, 1, 0, 2, 0, 2, 1, 2, 2))
            add(Shape17(0, 0, 0, 1, 0, 2, 0, 3))
            add(Shape17(0, 0, 1, 0, 0, 1, 1, 1))
        }
        while (true) {
            yieldAll(x)
        }
    }

    fun part1(input: String): Int {
        val wind = windIterator(input)
        val shapes = shape17Iterator()
        val occupied = hashSetOf<Point17>()
        repeat(2022) {
            val currentTop = occupied.top
            var cur = shapes.next().relTo(currentTop).tryMove(occupied, Point17(2, 0))
            while (true) {
                cur = cur.tryMove(occupied, windVector(wind))
                val tryFall = cur.tryMove(occupied, Point17(0, -1))
                if (tryFall == cur) {
                    occupied.addAll(cur.points)
                    break
                } else cur = tryFall
            }
        }
        return occupied.maxOf(Point17::y) + 1
    }

    fun part2(input: String, lastShape: Long): Long {
        val wind = windIterator(input)

        val shapes = shape17Iterator()
        val occupied = hashSetOf<Point17>()
        val diffs = StringBuilder()
        for (i in 0 until lastShape) {
            val currentTop = occupied.top
            var cur = shapes.next().relTo(currentTop).tryMove(occupied, Point17(2, 0))
            while (true) {
                val windVector = windVector(wind)
                cur = cur.tryMove(occupied, windVector)
                val tryFall = cur.tryMove(occupied, Point17(0, -1))
                if (tryFall == cur) {
                    occupied.addAll(cur.points)
                    diffs.append(occupied.top - currentTop)
                    val periodicSequenceSearchLength = 20
                    if (diffs.length > periodicSequenceSearchLength * 2) {
                        val repetitions = diffs.windowed(periodicSequenceSearchLength).count {
                            it == diffs.takeLast(
                                periodicSequenceSearchLength
                            )
                        }
                        if (repetitions > 1) {
                            println()
                            println("FOUND PERIODIC SEQUENCE!!!")
                            val (start, period) = diffs.asSequence().withIndex().windowed(periodicSequenceSearchLength)
                                .map {
                                    val foundIndex = diffs.indexOf(
                                        it.map(IndexedValue<Char>::value).joinToString(""),
                                        it.last().index
                                    )
                                    it.first().index to (foundIndex - it.first().index)
                                }
                                .firstOrNull { it.second >= 0 } ?: break
                            val periodicSequence = diffs.substring(start until start + period)
                            println("Repeating sequence is $period shapes")
                            println("Repeating sequence is $periodicSequence")
                            val numberOfRepetitions = (lastShape - start) / period
                            println("There will be $numberOfRepetitions repetitions")
                            val repetitionIncrement = periodicSequence.map(Char::digitToInt).sum()
                            println("Each repetition adds $repetitionIncrement height")
                            val startIncrement = diffs.substring(0 until start).map(Char::digitToInt).sum()
                            println("Glass stabilization is ${start - 1} shapes")
                            println("Glass stabilization is $startIncrement height")
                            val remainder = lastShape - (start - 1) - (numberOfRepetitions * period) - 1
                            println("Tail remaining after repetitions is $remainder shapes")
                            val tailIncrement =
                                periodicSequence.take(remainder.toInt()).map(Char::digitToInt).sum()
                            println("Its increment is $tailIncrement")
                            val totalIncrement =
                                startIncrement.toLong() + (numberOfRepetitions * repetitionIncrement) + tailIncrement
                            println("Total increment is ${startIncrement.toLong()} + (${numberOfRepetitions} * ${repetitionIncrement}) + $tailIncrement =  $totalIncrement")
                            return totalIncrement
                        }
                    }
                    break
                } else cur = tryFall
            }
        }
        return -1L
    }


    val testInput = readInput("Day17_test")[0]
    check(part1(testInput) == 3068)
    val input = readInput("Day17")[0]
    println(part1(input))
    check(part2(testInput, 2022) == part1(testInput).toLong())
    check(part2(input, 2022) == part1(input).toLong())
    check(part2(testInput, 1000000000000L) == 1514285714288)
    println(part2(input, 1000000000000L))


}

data class Point17(val x: Int, val y: Int) {
    fun move(vector: Point17): Point17 {
        return Point17(x + vector.x, y + vector.y)
    }
}

data class Shape17(val points: List<Point17>) {
    constructor(vararg points: Int) : this(points.asSequence().chunked(2) { (a, b) -> Point17(a, b) }.toList())

    fun relTo(currentTop: Int): Shape17 =
        Shape17(points.map { it.copy(y = currentTop + it.y + 4) })

    fun tryMove(occupied: Set<Point17>, vector: Point17): Shape17 {
        val nextPos = points.map { it.move(vector) }
        for (point in nextPos) {
            if (occupied.contains(point) || point.x < 0 || point.x > 6 || point.y < 0) return this
        }
        return Shape17(nextPos)
    }
}
