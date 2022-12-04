import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets
import java.security.cert.PKIXParameters
import java.util.IllegalFormatCodePointException
import java.util.Scanner

class Day2 {
}

enum class Result {
    BEAT,
    TIE,
    LOSS
}

fun toTarget(ch: String): Result {
    return when (ch) {
        "X" -> Result.LOSS
        "Y" -> Result.TIE
        "Z" -> Result.BEAT
        else -> throw IllegalArgumentException("$ch is invalid")
    }
}

sealed interface RockPaperScissors {
    fun score(): Int
    fun result(other: RockPaperScissors): Result
    fun pickTarget(target: Result): RockPaperScissors
}

object Rock : RockPaperScissors {
    override fun score(): Int = 1

    override fun result(other: RockPaperScissors): Result {
        return when (other) {
            Paper -> Result.LOSS
            Rock -> Result.TIE
            Scissors -> Result.BEAT
        }
    }

    override fun pickTarget(target: Result): RockPaperScissors {
        return when (target) {
            Result.BEAT -> Paper
            Result.TIE -> Rock
            Result.LOSS -> Scissors
        }
    }

}

object Paper : RockPaperScissors {
    override fun score(): Int = 2

    override fun result(other: RockPaperScissors): Result {
        return when (other) {
            Paper -> Result.TIE
            Rock -> Result.BEAT
            Scissors -> Result.LOSS
        }
    }

    override fun pickTarget(target: Result): RockPaperScissors {
        return when (target) {
            Result.BEAT -> Scissors
            Result.TIE -> Paper
            Result.LOSS -> Rock
        }
    }
}

object Scissors : RockPaperScissors {
    override fun score(): Int = 3

    override fun result(other: RockPaperScissors): Result {
        return when (other) {
            Paper -> Result.BEAT
            Rock -> Result.LOSS
            Scissors -> Result.TIE
        }
    }

    override fun pickTarget(target: Result): RockPaperScissors {
        return when (target) {
            Result.BEAT -> Rock
            Result.TIE -> Scissors
            Result.LOSS -> Paper
        }
    }
}

fun toRockPaperScissors(option: String): RockPaperScissors {
    return when (option) {
        "A", "X" -> Rock
        "B", "Y" -> Paper
        "C", "Z" -> Scissors
        else -> throw IllegalArgumentException("Unknown option $option")
    }
}

data class Game(val opponent: RockPaperScissors, val us: RockPaperScissors) {
    fun result(): Int {
        return when (us.result(opponent)) {
            Result.BEAT -> 6
            Result.TIE -> 3
            Result.LOSS -> 0
        } + us.score()
    }
}

private const val Input1 = """
A Y
B X
C Z
"""

fun buildGames(input: String): List<Game> {
    return input.split("\n").filter(String::isNotBlank).map { line ->
        val parts = line.split(" ")
        Game(toRockPaperScissors(parts[0]), toRockPaperScissors(parts[1]))
    }
}

fun buildGamesPart2(input: String): List<Game> {
    return input.split("\n").filter(String::isNotBlank).map { line ->
        val parts = line.split(" ")
        val opponentChoice = toRockPaperScissors(parts[0])
        Game(opponentChoice, opponentChoice.pickTarget(toTarget(parts[1])))
    }
}

fun main() {
    val games = buildGames(
        ClassLoader.getSystemClassLoader().getResourceAsStream("day2/input1.txt").readAllBytes()
            .toString(StandardCharsets.UTF_8)
    )

    val score = games.sumOf(Game::result)
    println(score)

    val games2 = buildGamesPart2(
        ClassLoader.getSystemClassLoader().getResourceAsStream("day2/input1.txt").readAllBytes()
            .toString(StandardCharsets.UTF_8)
    )
//    val games2 = buildGamesPart2(Input1)

//    println(games2)
    val score2 = games2.sumOf(Game::result)
    println(score2)
}