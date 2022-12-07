import FSNode07.Dir
import FSNode07.File

fun main() {
    fun buildTree(input: List<String>): Dir {
        var current = Dir("/", null)
        for (line in input.drop(1)) {
            if (line.startsWith('$')) {
                if (line.startsWith("$ cd")) {
                    val nextDir = line.split(' ')[2]
                    current = if (nextDir == "..") current.parent!! else current
                        .children
                        .filter { it.name == nextDir }
                        .filterIsInstance<Dir>()
                        .firstOrNull() ?: Dir(nextDir, current)
                }
            } else if (line.startsWith("dir")) {
                current.children.add(Dir(line.split(' ')[1], current))
            } else if (line.isBlank()) {
                // ignore
            } else {
                val (size, name) = line.split(' ')
                current.children.add(File(name, size.toInt(), current))
            }
        }
        while (current.parent != null) {
            current = current.parent!!
        }
        return current
    }

    fun part1(input: List<String>): Int {
        val smallDirs = arrayListOf<Dir>()
        val toCheck = ArrayDeque<Dir>()
        toCheck.add(buildTree(input))
        while (toCheck.isNotEmpty()) {
            val cur = toCheck.removeFirst()
            if (cur.size <= 100000) {
                smallDirs.add(cur)
            }
            toCheck.addAll(cur.children.filterIsInstance<Dir>())
        }
        return smallDirs.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val root = buildTree(input)
        val needToFree = root.size - 40000000
        val allDirs = sortedSetOf<Dir>({ o1, o2 -> o1.size.compareTo(o2.size) })
        val toCheck = ArrayDeque<Dir>()
        toCheck.add(root)
        while (toCheck.isNotEmpty()) {
            val cur = toCheck.removeFirst()
            if (cur.size >= needToFree)
                allDirs.add(cur)
            toCheck.addAll(cur.children.filterIsInstance<Dir>())
        }
        return allDirs
            .first()
            .size
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

sealed interface FSNode07 {
    val size: Int
    val name: String
    val parent: Dir?

    data class File(override val name: String, override val size: Int, override val parent: Dir?) : FSNode07
    data class Dir(override val name: String, override val parent: Dir?) : FSNode07 {
        val children = hashSetOf<FSNode07>()
        override val size: Int by lazy { children.sumOf { it.size } }
    }
}
