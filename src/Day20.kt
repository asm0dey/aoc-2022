fun main() {
    class Container<T : Number>(val value: T)

    fun <T : Number> ArrayDeque<Container<T>>.mix(containers: List<Container<T>>) {
        for (cont in containers) {
            val oldIndex = indexOf(cont)
            removeAt(oldIndex)
            val newIndex = oldIndex.toLong() + cont.value.toLong()
            val newIndexWrapped =
                if (newIndex > size) newIndex % size
                else if (newIndex < 0) (newIndex % size + size) % size
                else newIndex
            add(newIndexWrapped.toInt(), cont)
        }
    }

    fun part1(strings: List<String>): Int {
        val all = strings.filterNot(String::isBlank).map(String::toInt).map(::Container)
        val buf = ArrayDeque(all)
        buf.mix(all)
        val indexOf0 =
            buf.mapIndexed(Int::to).single { (_, b) -> b.value == 0 }.first
        val i1 = buf[(1000 + indexOf0) % buf.size].value
        val i2 = buf[(2000 + indexOf0) % buf.size].value
        val i3 = buf[(3000 + indexOf0) % buf.size].value
        return i1 + i2 + i3
    }

    fun part2(strings: List<String>): Long {
        val all = strings.filterNot(String::isBlank).map(String::toLong).map { it * 811589153 }.map(::Container)
        val buf = ArrayDeque(all)
        repeat(10) {
            buf.mix(all)
        }
        val indexOf0 =
            buf.mapIndexed(Int::to).single { (_, b) -> b.value == 0L }.first
        val i1 = buf[(1000 + indexOf0) % buf.size].value
        val i2 = buf[(2000 + indexOf0) % buf.size].value
        val i3 = buf[(3000 + indexOf0) % buf.size].value
        return i1 + i2 + i3

    }

    println(part1(readInput("Day20_test")))
    println(part1(readInput("Day20")))
    println(part2(readInput("Day20_test")))
    println(part2(readInput("Day20")))

}


