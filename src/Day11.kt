import Monkey11.Op
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.properties.Delegates.notNull

fun main() {

    fun parseMonkeys(input: List<String>): SortedMap<Int, Monkey11> {
        val monkeys = sortedMapOf<Int, Monkey11>()
        var monkeyNum: Int by notNull()
        var items: ArrayDeque<Long> by notNull()
        var operation: Op by notNull()
        var test: Long by notNull()
        var trueMonkey: Int by notNull()
        var falseMonkey: Int by notNull()
        for (line in input) {
            if (line.trim().startsWith("Monkey")) {
                monkeyNum = Regex("\\d+").find(line)!!.value.toInt()
            } else if (line.trim().startsWith("Starting")) {
                items = ArrayDeque(Regex("\\d+").findAll(line).map { it.value.toLong() }.toList())
            } else if (line.trim().startsWith("Opera")) {
                val (f, op, s) = line.split(':')[1]
                    .trim()
                    .replace("new = ", "")
                    .split(' ')
                    .filter(String::isNotBlank)
                operation = Op(f, op, s)
            } else if (line.trim().startsWith("Test")) {
                test = Regex("\\d+").find(line)!!.value.toLong()
            } else if (line.trim().startsWith("If true")) {
                trueMonkey = Regex("\\d+").find(line)!!.value.toInt()
            } else if (line.trim().startsWith("If false")) {
                falseMonkey = Regex("\\d+").find(line)!!.value.toInt()
            } else {
                monkeys[monkeyNum] =
                    Monkey11(items, operation, test, trueMonkey, falseMonkey, monkeys)
            }
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val lcm = monkeys.values.map(Monkey11::divisor).reduce(::lcm)
        repeat(20) {
            for ((_, monkey) in monkeys) {
                monkey(lcm) { (it.toDouble() / 3).toLong() }
            }
        }
        return monkeys.values.map(Monkey11::processedItems).sorted().takeLast(2).map(Int::toLong).reduce(Long::times)
    }


    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val lcm = monkeys.values.map(Monkey11::divisor).reduce(::lcm)
        repeat(10000) {
            for ((_, monkey) in monkeys) {
                monkey(lcm)
            }
        }
        return monkeys.values.map(Monkey11::processedItems).sorted().takeLast(2).map(Int::toLong).reduce(Long::times)
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
    val monkeysBuyNumbers: Map<Int, Monkey11>
) {
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

    var processedItems = 0

    inline operator fun invoke(lcm: Long, modifier: (Long) -> Long = { it }) {
        while (items.isNotEmpty()) {
            val item = items.removeFirst()
            val result = modifier(operation(item))
            val testResult = result % divisor == 0L
            (if (testResult) monkeysBuyNumbers[trueMonkey] else monkeysBuyNumbers[falseMonkey])!!.items.add(result % lcm)
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