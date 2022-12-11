import Monkey11.Op

fun main() {

    fun parseMonkeys(input: List<String>): List<Monkey11> {
        val digits = Regex("\\d+")
        return buildList {
            input.chunked(7) {
                val (f, op, s) = it[2].split(':')[1]
                    .trim()
                    .replace("new = ", "")
                    .split(' ')
                    .filter(String::isNotBlank)
                add(
                    Monkey11(
                        ArrayDeque(digits.findAll(it[1]).map { it.value.toLong() }.toList()),
                        Op(f, op, s),
                        digits.find(it[3])!!.value.toLong(),
                        digits.find(it[4])!!.value.toInt(),
                        digits.find(it[5])!!.value.toInt(),
                    )
                )
            }
        }
            .also { all -> all.forEach { it.monkeys = all } }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        repeat(20) {
            for (monkey in monkeys) {
                monkey { (it.toDouble() / 3).toLong() }
            }
        }
        return monkeys.map(Monkey11::processedItems).sorted().takeLast(2).map(Int::toLong).reduce(Long::times)
    }


    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        repeat(10000) {
            monkeys.forEach(Monkey11::invoke)
        }
        return monkeys.map(Monkey11::processedItems).sorted().takeLast(2).map(Int::toLong).reduce(Long::times)
    }

    val testInput = readInput("Day11_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 10605L) { "Actual result: $testPart1" }

    val input = readInput("Day11")
    println(part1(input))
    val testPart2 = part2(testInput)
    check(testPart2 == 2713310158L) { "Actual result: $testPart2" }
    println(part2(input))
}

class Monkey11(
    val items: ArrayDeque<Long>,
    val operation: Op,
    val divisor: Long,
    val trueMonkey: Int,
    val falseMonkey: Int,
) {
    lateinit var monkeys: List<Monkey11>
    var processedItems = 0
    val lcm: Long by lazy { monkeys.map(Monkey11::divisor).reduce(::lcm) }

    class Op(private val f: String, private val op: String, private val s: String) {
        operator fun invoke(input: Long): Long {
            val firstArg = if (f == "old") input else f.toLong()
            val secondArg = if (s == "old") input else s.toLong()
            return when (op) {
                "*" -> firstArg * secondArg
                "+" -> firstArg + secondArg
                else -> error("Unsupported op $op")
            }
        }
    }

    inline operator fun invoke(modifier: (Long) -> Long = { it }) {
        while (items.isNotEmpty()) {
            val item = items.removeFirst()
            val result = modifier(operation(item))
            val testResult = result % divisor == 0L
            (if (testResult) monkeys[trueMonkey] else monkeys[falseMonkey]).items.add(result % lcm)
            processedItems++
        }

    }
}

tailrec fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}