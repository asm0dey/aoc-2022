fun main() {
    operator fun List<CharArray>.get(point: Pair<Int, Int>) = this[point.first][point.second]

    fun neighbors(
        current: Pair<Int, Int>,
        input: List<CharArray>,
        validator: (Char, Char) -> Boolean,
    ) = listOf(
        current.copy(first = current.first - 1),
        current.copy(first = current.first + 1),
        current.copy(second = current.second - 1),
        current.copy(second = current.second + 1)
    )
        .filter {
            val (i, j) = it
            if (i < 0 || j < 0 || i >= input.size || j >= input[0].size) false
            else validator(input[current], input[it])
        }

    fun bfs(
        input: List<CharArray>,
        current: Pair<Int, Int>,
        visitor: (first: Char, second: Char) -> Boolean
    ): HashMap<Pair<Int, Int>, Int> {
        val results = hashMapOf(current to 0)
        val visit = ArrayDeque(listOf(current))
        while (visit.isNotEmpty()) {
            val first = visit.removeFirst()
            for (neighbor in neighbors(first, input, visitor)) {
                if (results.containsKey(neighbor)) continue
                results[neighbor] = results[first]!! + 1
                visit.add(neighbor)
            }
        }
        return results
    }

    val visitor = { first: Char, second: Char ->
        val end = first in 'y'..'z' && second == 'E'
        val start = first == 'S' && second in 'a'..'b'
        val validJump = first.isLowerCase() && second.isLowerCase() && first.code - second.code >= -1
        end || start || validJump
    }

    fun part1(input: List<String>): Int {
        val x = input.filter(String::isNotBlank).map(String::toCharArray)
        val start = x.mapIndexed { index, c -> index to c.indexOf('S') }.first { (a, b) -> (a >= 0) and (b >= 0) }
        val finish = x.mapIndexed { index, c -> index to c.indexOf('E') }.first { (a, b) -> (a >= 0) and (b >= 0) }
        return bfs(x, start, visitor)[finish]!!
    }


    fun part2(input: List<String>): Int {
        val coords = arrayListOf<Pair<Int, Int>>()
        val x = input.filter(String::isNotBlank).map { it.replace('S', 'a').toCharArray() }
        for ((index, line) in x.withIndex()) {
            for ((charIndex, char) in line.withIndex()) {
                if (char == 'a') {
                    coords.add(index to charIndex)
                }
            }
        }
        val finish = x.mapIndexed { index, c -> index to c.indexOf('E') }.first { (a, b) -> (a >= 0) and (b >= 0) }
        return coords.minOf { bfs(x, it, visitor)[finish] ?: Int.MAX_VALUE }
    }

    val testInput = readInput("Day12_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 31) { "Actual result: $testPart1" }

    val input = readInput("Day12")
    println(part1(input))
    val testPart2 = part2(testInput)
    check(testPart2 == 29) { "Actual result: $testPart2" }
    println(part2(input))
}



