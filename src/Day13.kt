import Node13.Leaf
import Node13.NodeList

fun main() {


    fun parse(tokens: ArrayDeque<String>): Node13 {
        val mutable = arrayListOf<Node13>()
        while (tokens.isNotEmpty()) {
            when (val token = tokens.removeFirst()) {
                "]" -> return NodeList(mutable)
                "[" -> mutable.add(parse(tokens))
                else -> mutable.add(Leaf(token.toInt()))
            }
        }
        return mutable[0]
    }

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .filterNot(String::isBlank)
            .map { Regex("\\[|]|\\d+").findAll(it).map(MatchResult::value).toList() }
            .map(::ArrayDeque)
            .chunked(2)
            .map { it.take(2) }
            .map { (fst, snd) -> parse(fst) to parse(snd) }
            .mapIndexed { index, pair ->
                val (a, b) = pair
                if (a < b) index + 1 else 0
            }
            .sum()
    }


    fun part2(input: List<String>): Int {
        return (input + listOf("[[2]]", "[[6]]"))
            .asSequence()
            .filterNot(String::isBlank)
            .map { Regex("\\[|]|\\d+").findAll(it).map(MatchResult::value).toList() }
            .map(::ArrayDeque)
            .map(::parse)
            .sorted()
            .map { it.also(::println) }
            .mapIndexed { index, packet ->
                if (packet.toString() == "[[2]]" || packet.toString() == "[[6]]") index + 1
                else 1
            }
            .reduce(Int::times)
    }

    val testInput = readInput("Day13_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 13) { "Actual result: $testPart1" }

    val input = readInput("Day13")
    println(part1(input))
    val testPart2 = part2(testInput)
    check(testPart2 == 140) { "Actual result: $testPart2" }
    println(part2(input))
}

sealed interface Node13 : Comparable<Node13> {

    override operator fun compareTo(other: Node13): Int

    @JvmInline
    value class Leaf(val input: Int) : Node13 {
        override fun compareTo(other: Node13): Int = when (other) {
            is Leaf -> input.compareTo(other.input)
            is NodeList -> NodeList(listOf(this)).compareTo(other)
        }

        override fun toString(): String = input.toString()
    }

    @JvmInline
    value class NodeList(val input: List<Node13> = emptyList()) : Node13 {
        override fun compareTo(other: Node13): Int {
            return when (other) {
                is Leaf -> this.compareTo(NodeList(listOf(other)))
                is NodeList -> {
                    var pointer = 0
                    while (true) {
                        when {
                            input.size == other.input.size && input.size == pointer -> return 0
                            input.size > pointer && other.input.size > pointer -> {
                                val comparison = input[pointer].compareTo(other.input[pointer])
                                if (comparison != 0) return comparison
                                else {
                                    pointer++
                                    continue
                                }
                            }

                            input.size == pointer -> return -1
                            other.input.size == pointer -> break
                        }
                    }
                    1
                }
            }
        }

        override fun toString(): String = "[${input.joinToString(", ")}]"
    }
}


