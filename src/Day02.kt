import Outcome.*
import RPS.*

private enum class Outcome(val value: Int) {
    WIN(6), LOSE(0), DRAW(3);
}

private enum class RPS(val value:Int) {
    ROCK(1) {
        override fun outcome(m: RPS): Outcome = when (m) {
            ROCK -> DRAW
            PAPER -> WIN
            SCISSORS -> LOSE
        }

        override fun findSecondSignByOutcome(outcome: Outcome): RPS = when (outcome) {
            WIN -> PAPER
            LOSE -> SCISSORS
            DRAW -> ROCK
        }
    },
    PAPER(2) {
        override fun outcome(m: RPS): Outcome = when (m) {
            ROCK -> LOSE
            PAPER -> DRAW
            SCISSORS -> WIN
        }

        override fun findSecondSignByOutcome(outcome: Outcome): RPS = when (outcome) {
            WIN -> SCISSORS
            LOSE -> ROCK
            DRAW -> PAPER
        }
    },
    SCISSORS(3) {
        override fun outcome(m: RPS): Outcome = when (m) {
            ROCK -> WIN
            PAPER -> LOSE
            SCISSORS -> DRAW
        }

        override fun findSecondSignByOutcome(outcome: Outcome): RPS = when (outcome) {
            WIN -> ROCK
            LOSE -> PAPER
            DRAW -> SCISSORS
        }
    };

    abstract fun outcome(m: RPS): Outcome
    abstract fun findSecondSignByOutcome(outcome: Outcome): RPS

}

fun main() {

    fun elfToSign(f: String): RPS = when (f) {
        "A" -> ROCK
        "B" -> PAPER
        "C" -> SCISSORS
        else -> throw IllegalArgumentException()
    }

    fun meToSign(s: String): RPS = when (s) {
        "X" -> ROCK
        "Y" -> PAPER
        "Z" -> SCISSORS
        else -> throw IllegalArgumentException()
    }

    fun meToOutcome(s: String) = when (s) {
        "X" -> LOSE
        "Y" -> DRAW
        "Z" -> WIN
        else -> throw IllegalArgumentException()
    }

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(' ') }
            .filterNot { it.size != 2 }
            .map { (f, s) -> elfToSign(f) to meToSign(s) }
            .map { (elfSign, mySign) -> elfSign.outcome(mySign).value + mySign.value }
            .sum()
    }


    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(' ') }
            .filterNot { it.size != 2 }
            .map { (f, s) -> elfToSign(f) to meToOutcome(s) }
            .map { (elfSign, outcome) -> elfSign.findSecondSignByOutcome(outcome).value + outcome.value }
            .sum()
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
