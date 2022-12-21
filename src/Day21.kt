sealed interface Node21 {
    fun calc(): Long
    fun shouldEqual(other: Long): Long?
    data class Cont(val int: Long) : Node21 {
        override fun calc(): Long = int
        override fun shouldEqual(other: Long) = null
    }

    data class Expression(val el1: Node21, val op: String, val el2: Node21) : Node21 {
        override fun calc(): Long {
            val calc1 = el1.calc()
            val calc2 = el2.calc()
            return when (op) {
                "+" -> calc1 + calc2
                "-" -> calc1 - calc2
                "*" -> calc1 * calc2
                "/" -> calc1 / calc2
                "=" -> {
                    val try1 = el1.shouldEqual(calc2)
                    if (try1 != null) return try1
                    val try2 = el2.shouldEqual(calc1)
                    if (try2 != null) return try2
                    if (calc1 == calc2) 1L else 0L
                }

                else -> error("Operation $op is not supported")
            }
        }

        override fun shouldEqual(other: Long): Long? {
            val calc1 = el1.calc()
            val calc2 = el2.calc()
            when (op) {
                "+" -> {
                    val try1 = el1.shouldEqual(other - calc2)
                    if (try1 != null) return try1
                    val try2 = el2.shouldEqual(other - calc1)
                    if (try2 != null) return try2
                    return null
                }

                "-" -> {
                    val try1 = el1.shouldEqual(other + calc2)
                    if (try1 != null) return try1
                    val try2 = el2.shouldEqual(calc1 - other)
                    if (try2 != null) return try2
                    return null
                }

                "*" -> {
                    val try1 = el1.shouldEqual(other / calc2)
                    if (try1 != null) return try1
                    val try2 = el2.shouldEqual(other / calc1)
                    if (try2 != null) return try2
                    return null
                }

                "/" -> {
                    val try1 = el1.shouldEqual(other * calc2)
                    if (try1 != null) return try1
                    val try2 = el2.shouldEqual(calc1 / other)
                    if (try2 != null) return try2
                    return null
                }
            }
            return null
        }


    }

    data class Ref(val name: String, val provider: (String) -> Node21) : Node21 {
        override fun calc(): Long = provider(name).calc()
        override fun shouldEqual(other: Long): Long? {
            return if (name != "humn") provider(name).shouldEqual(other)
            else other
        }

    }

}

fun main() {

    fun String.toNode(provider: (String) -> Node21): Pair<String, Node21> {
        fun String.internalToNode(): Node21 {
            val sec = toLongOrNull()
            return if (sec == null) {
                Node21.Ref(this, provider)
            } else Node21.Cont(sec)
        }

        val tokens = split(' ', ':').filterNot(String::isBlank)
        val name = tokens[0]
        return if (tokens.size == 2) {
            name to tokens[1].internalToNode()
        } else
            name to Node21.Expression(tokens[1].internalToNode(), tokens[2], tokens[3].internalToNode())
    }


    fun part1(input: List<String>): Long {
        val map = hashMapOf<String, Node21>()
        input
            .filterNot { it.isBlank() }
            .map { el -> el.toNode { map[it]!! } }
            .toMap(map)
        return map["root"]!!.calc()
    }


    fun part2(input: List<String>): Long {
        val map = hashMapOf<String, Node21>()
        input
            .filterNot { it.isBlank() }
            .map { if (it.startsWith("root:")) it.replace('+', '=') else it }
            .map { el -> el.toNode { map[it]!! } }
            .toMap(map)
        return map["root"]!!.calc()

    }

    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

