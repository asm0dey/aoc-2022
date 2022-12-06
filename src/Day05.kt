fun main() {


    fun String.parseStacks(stacks: HashMap<Int, ArrayList<Char>>) {
        chunkedSequence(4)
            .map {
                it
                    .replace(Regex("[\\[\\]\\s]"), "")
                    .firstOrNull()
            }
            .forEachIndexed { index, char ->
                if (char != null) {
                    stacks.compute(index + 1) { _, cur ->
                        cur?.apply { add(0, char) } ?: arrayListOf(char)
                    }
                }
            }
    }

    fun part1(input: List<String>): String {
        val stacks = hashMapOf<Int, ArrayList<Char>>()
        for (line in input) {
            if (line.contains('[')) {
                line.parseStacks(stacks)
            } else if (line.contains("move")) {
                val (howMany, from, to) = Regex("\\d+").findAll(line).toList().map { it.value.toInt() }
                repeat(howMany) {
                    stacks[to]!!.add(stacks[from]!!.removeLast())
                }
            }
        }

        return stacks.toSortedMap().values.map { it.last() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val stacks = hashMapOf<Int, ArrayList<Char>>()
        for (line in input) {
            if (line.contains('[')) {
                line.parseStacks(stacks)
            } else if (line.contains("move")) {
                val (howMany, from, to) = Regex("\\d+").findAll(line).toList().map { it.value.toInt() }
                stacks[to]!!.addAll(stacks[from]!!.takeLast(howMany))
                stacks[from] = ArrayList(stacks[from]!!.dropLast(howMany))
            }
        }

        return stacks.toSortedMap().values.map { it.last() }.joinToString("")
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
