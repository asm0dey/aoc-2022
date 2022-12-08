fun main() {

    fun part1(input: List<String>): Int {
        val matrix = input.map { it.map(Char::digitToInt) }
        var counter = 0
        for ((i, _) in matrix.withIndex()) {
            val row = matrix[i]
            for ((j, _) in row.withIndex()) {
                if (i == 0 || i == matrix.size - 1 || j == 0 || j == row.size - 1) {
                    counter++
                    continue
                }
                val value = row[j]
                if (row.subList(0, j).all { it < value }) {
                    counter++
                    continue
                }
                if (row.subList(j + 1, row.size).all { it < value }) {
                    counter++
                    continue
                }
                val column = matrix.map { it[j] }
                if (column.subList(0, i).all { it < value }) {
                    counter++
                    continue
                }
                if (column.subList(i + 1, column.size).all { it < value }) {
                    counter++
                    continue
                }
            }
        }
        return counter
    }

    fun part2(input: List<String>): Int {
        val matrix = input.map { it.map(Char::digitToInt) }
        return matrix
            .indices
            .flatMap { i ->
                val row = matrix[i]
                row.indices.map { j ->
                    val col = matrix.map { it[j] }
                    val value = row[j]
                    listOf(
                        row.subList(0, j).reversed(),
                        row.subList(j + 1, row.size),
                        col.subList(0, i).reversed(),
                        col.subList(i + 1, col.size)
                    )
                        .map {
                            var counter = 0
                            for (item in it) {
                                counter++
                                if (item >= value) break
                            }
                            counter
                        }
                        .reduce(Int::times)
                }
            }
            .max()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)

    val input = readInput("Day08")
    println(part1(input))
    check(part2(testInput) == 8)
    println(part2(input))
}

