fun main() {
    fun part1(input: String): Int {
        val distinctCharCount = 4
        return input
            .windowed(distinctCharCount)
            .mapIndexed { index, chunk -> index to (chunk.toSet().size == distinctCharCount) }
            .filter { (_, a) -> a }
            .map { (a, _) -> a + distinctCharCount }
            .first()
    }

    fun part2(input: String): Int {
        val distinctCharCount = 14
        return input
            .windowed(distinctCharCount)
            .mapIndexed { index, chunk -> index to (chunk.toSet().size == distinctCharCount) }
            .filter { (_, a) -> a }
            .map { (a, _) -> a + distinctCharCount }
            .first()
    }

    check(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)

    val input = readInput("Day06")
    println(part1(input[0]))

    check(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    println(part2(input[0]))
}
