import Graph16.*

fun main() {

    fun parseInput(input: List<String>): Set<Valve> = input
        .asSequence()
        .filter(String::isNotBlank)
        .map {
            it
                .replace(Regex("(Valve |has flow rate=|; tunnels? leads? to valves?)"), "")
                .split(' ', ',')
                .filter(String::isNotEmpty)
        }
        .map {
            val name = it[0]
            val flowRate = it[1].toInt()
            val leadsTo = it.subList(2, it.size)
            Valve(flowRate, leadsTo, name)
        }
        .toSet()


    fun part1(input: List<String>): Int {
        val allValves = parseInput(input)
        val maxOpenValves = allValves.count { it.flow > 0 }
        val start = allValves.find { it.name == "AA" }!!
        val startPath = Path1(listOf(start), HashMap())
        var allPaths = listOf(startPath)
        var bestPath = startPath

        for (time in 1..30) {
            allPaths = buildList {
                for (it in allPaths) {
                    if (it.open.size == maxOpenValves) listOf<Path1>()

                    val currentLast = it.last()
                    val currentValves = it.valves

                    // open valve
                    if (currentLast.flow > 0 && !it.open.containsKey(currentLast)) {
                        val open = it.open.toMutableMap()
                        open[currentLast] = time
                        val possibleValves = currentValves + currentLast
                        this.add(Path1(possibleValves, open))

                    }

                    // move to valve

                    currentLast.leadsTo
                        .mapTo(this) { lead ->
                            Path1(currentValves + (allValves.find { it.name == lead }!!), it.open)
                        }
                }

            }
                .sortedByDescending { it.total() }
                .take(10000)

            if (allPaths.first().total() > bestPath.total()) bestPath = allPaths.first()


        }

        return bestPath.total()

    }

    fun findPossibleValves(
        open: Boolean,
        currentLast: Valve,
        currentValves: List<Valve>,
        opened: MutableMap<Valve, Int>,
        time: Int,
        allValves: Set<Valve>
    ) = if (open) {
        opened[currentLast] = time
        listOf(currentValves + currentLast)
    } else {
        currentLast.leadsTo.map { lead ->
            // add possible path and move on
            val possibleValve = allValves.find { it.name == lead } ?: error("valve $lead not found")
            val possibleValves = currentValves + possibleValve
            possibleValves
        }
    }

    fun part2(input: List<String>): Int {
        val allValves = parseInput(input)
        val maxOpenValves = allValves.count { it.flow > 0 }

        val start = allValves.find { it.name == "AA" }!!
        val startPath = Path2(valvesMe = listOf(start), valvesElephant = listOf(start), open = HashMap())
        var allPaths = listOf(startPath)
        var bestPath = startPath


        for (time in 1..26) {
            val newPaths = buildList {

                for (currentPath in allPaths) {
                    if (currentPath.open.size == maxOpenValves) continue

                    val currentLastMe = currentPath.lastMe()
                    val currentLastElephant = currentPath.lastElephant()
                    val currentValvesMe = currentPath.valvesMe
                    val currentValvesElephant = currentPath.valvesElephant

                    val openMe = currentLastMe.flow > 0 && !currentPath.open.containsKey(currentLastMe)
                    val openElephant =
                        currentLastElephant.flow > 0 && !currentPath.open.containsKey(currentLastElephant)

                    // open both, mine or elephant's valve
                    if (openMe || openElephant) {
                        val open = currentPath.open.toMutableMap()

                        val possibleValvesMe: List<List<Valve>> =
                            findPossibleValves(openMe, currentLastMe, currentValvesMe, open, time, allValves)

                        val possibleValvesElephants: List<List<Valve>> = findPossibleValves(
                            openElephant,
                            currentLastElephant,
                            currentValvesElephant,
                            open,
                            time,
                            allValves
                        )

                        possibleValvesMe
                            .flatMapTo(this) { a ->
                                possibleValvesElephants.map { b ->
                                    Path2(a, b, open)
                                }
                            }
                    }
                    currentLastMe.leadsTo
                        .flatMap { a -> currentLastElephant.leadsTo.map { b -> a to b } }
                        .filter { (a, b) -> a != b }
                        .mapTo(this) { (leadMe, leadElephant) ->
                            Path2(
                                currentValvesMe + (allValves.find { it.name == leadMe }!!),
                                currentValvesElephant + (allValves.find { it.name == leadElephant }!!),
                                currentPath.open
                            )
                        }

                }

            }

            allPaths = newPaths.sortedByDescending { it.total() }.take(100000).toList()

            if (allPaths.first().total() > bestPath.total()) bestPath = allPaths.first()
        }

        return bestPath.total()
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))


}

class Graph16 {
    data class Valve(val flow: Int, val leadsTo: List<String>, val name: String)
    data class Path1(val valves: List<Valve>, val open: Map<Valve, Int>) {

        fun last(): Valve = valves.last()

        fun total(): Int = open.map { (valve, time) -> (30 - time) * valve.flow }.sum()

    }

    data class Path2(val valvesMe: List<Valve>, val valvesElephant: List<Valve>, val open: Map<Valve, Int>) {

        fun lastMe(): Valve = valvesMe.last()
        fun lastElephant(): Valve = valvesElephant.last()

        fun total(): Int = open.map { (valve, time) -> (26 - time) * valve.flow }.sum()

    }
}