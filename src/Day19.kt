import kotlin.math.max
import kotlin.math.min

fun main() {
    fun List<Int>.elemAdd(other: List<Int>) = this.mapIndexed { i, e -> e + other[i] }
    fun List<Int>.elemAdd(vararg other: Int) = this.mapIndexed { i, e -> e + other[i] }
    fun List<Int>.elemSubtract(other: List<Int>) = this.mapIndexed { i, e -> e - other[i] }
    fun <E> List<E>.subList(fromIndex: Int) = this.subList(fromIndex, this.size - 1)
    fun String.ints() = "-?\\d+".toRegex(RegexOption.MULTILINE).findAll(this).map { it.value.toInt() }.toList()
    fun List<String>.blueprints() = filterNot(String::isEmpty).map(String::ints).map {
        it[0] to /* id of blueprint */ listOf(
            listOf(it[1], 0, 0, 0),  // ore robot
            listOf(it[2], 0, 0, 0),     // clay robot
            listOf(it[3], it[4], 0, 0), // obsidian robot
            listOf(it[5], 0, it[6], 0)  // geode robot
        )
    }

    fun List<Int>.canAfford(blueprint: List<Int>) = withIndex().all { it.value >= blueprint[it.index] }
    fun List<List<Int>>.solve(initialTime: Int): Int {
        // First four are resources, next four are robots, last one is the time left.
        val start = listOf(0, 0, 0, 0, 1, 0, 0, 0, initialTime)
        val queue = ArrayDeque(listOf(start))
        val seen = hashSetOf<List<Int>>()
        var best = 0

        // Maximum cost of any resource to be used in any one round
        val maxCosts = listOf(
            maxOf(List<Int>::first), // The price of most expensive ore consumption
            maxOf(List<Int>::component2), // The price of most expensive clay consumption
            this[3][2], // The price of most expensive obsidian consumption (only geode robots cost obsidian!)
        )

        outer@ while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()

            if (seen.contains(curr)) continue // We have already checked this one
            val timeLeft = curr[8]
            if (timeLeft == 0) { // Time's up. Tally up.
                best = max(best, curr[3])
                continue
            }
            // If we have no chance to make it to best, give up.
            if ((timeLeft * timeLeft + timeLeft) / 2 + curr[7] * timeLeft <= best - curr[3]) continue

            val currentInv = curr.subList(0, 4).toMutableList()
            val currentFactory = curr.subList(4)

            // Throw away excess inventory. Makes for more cache-hits of seen, makes it faster.
            currentInv[0] = min(currentInv[0], (timeLeft * maxCosts[0]) - (curr[4] * (timeLeft - 1)))
            currentInv[1] = min(currentInv[1], (timeLeft * maxCosts[1]) - (curr[5] * (timeLeft - 1)))
            currentInv[2] = min(currentInv[2], (timeLeft * maxCosts[2]) - (curr[6] * (timeLeft - 1)))

            val nextInventory = currentInv.elemAdd(currentFactory)
            val nextTime = timeLeft - 1

            // If we can afford a geo-cracking robot, build it and move on!
            if (currentInv.canAfford(this[3])) {
                queue.addFirst(nextInventory.elemSubtract(this[3]) + currentFactory.elemAdd(0, 0, 0, 1) + nextTime)
                continue@outer
            }

            // We can only build one robot at a time, so, build any robot that we can afford to build.
            for (i in 0..2) {
                // Don't build more robots than what can produce resources to be consumed in one round.
                // it's a waste of time. Except for geode-cracking robots!
                if (currentFactory[i] <= maxCosts[i] && currentInv.canAfford(this[i])) {
                    val nextFactory = currentFactory.toMutableList()
                    nextFactory[i] += 1
                    queue.addFirst(nextInventory.elemSubtract(this[i]) + nextFactory + nextTime)
                }
            }
            // We always have the possibility of not building any robots at all
            queue.addFirst(nextInventory + currentFactory + nextTime)

            seen.add(curr)
        }
        return best
    }

    fun part1(strings: List<String>) = strings.blueprints().sumOf { (a, b) -> a * b.solve(24) }
    fun part2(strings: List<String>) =
        strings.blueprints().subList(0, 3).map { it.second.solve(32) }.reduce(Int::times)

    println(part1(readInput("Day19")))
    println(part2(readInput("Day19")))


}


