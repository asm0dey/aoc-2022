fun main() {
    fun countElfCalories(input: List<String>): ArrayList<Int> {
        val results = arrayListOf<Int>()
        var current = 0
        for (s in input) {
            if (s.isBlank()) {
                results.add(current)
                current = 0
            } else {
                current += s.toInt()
            }
        }
        return results
    }

    fun part1(input: List<String>): Int {
        return countElfCalories(input).max()
    }


    fun part2(input: List<String>): Int {
        return countElfCalories(input).sortedDescending().take(3).sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
