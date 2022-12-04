fun main() {

    fun rangeToSet(textRange: List<String>) = (textRange[0].toInt()..textRange[1].toInt()).toSet()

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(',') }
            .filter { it.size == 2 }
            .map { it.map { range -> range.split('-') } }
            .map { (r1, r2) -> rangeToSet(r1) to rangeToSet(r2) }
            .count { (x, y) -> x.containsAll(y) || y.containsAll(x) }
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(',') }
            .filter { it.size == 2 }
            .map { it.map { range -> range.split('-') } }
            .map { (r1, r2) -> rangeToSet(r1) to rangeToSet(r2) }
            .count { (x, y) -> (x intersect y).isNotEmpty() }
    }
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
