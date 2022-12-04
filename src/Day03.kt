fun main() {

    fun letterToCode(it: Char) = if (it.isUpperCase()) it.code - 38 else it.code - 96
    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .filterNot(String::isBlank)
            .map(String::toList)
            .map { it.subList(0, it.size / 2).toSet().intersect(it.subList(it.size / 2, it.size).toSet()) }
            .filter { it.size == 1 }
            .map { it.single() }
            .sumOf(::letterToCode)
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .filterNot(String::isBlank)
            .chunked(3) {
                it.map(String::toSet).reduce(Set<Char>::intersect)
            }
            .filter { it.size == 1 }
            .map { it.single() }
            .sumOf(::letterToCode)
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
