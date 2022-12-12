import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.sqrt

class Day9 {

    enum class Direction {
        Up,
        Down,
        Left,
        Right;

        companion object {
            fun from(char: Char): Direction {
                return when (char) {
                    'R' -> Right
                    'L' -> Left
                    'U' -> Up
                    'D' -> Down
                    else -> throw IllegalArgumentException("Invalid direction: $char")
                }
            }
        }

    }

    data class Move(val direction: Direction, val count: Int)

    data class Position(val x: Int, val y: Int) {
        fun dist(other: Position): Double {
            return sqrt(((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble())
        }
    }

    data class Head(var position: Position) {
        val x: Int
            get() = position.x

        val y: Int
            get() = position.y

        fun move(direction: Direction): Head {
            return when (direction) {
                Direction.Left -> Head(Position(position.x - 1, position.y))
                Direction.Right -> Head(Position(position.x + 1, position.y))
                Direction.Up -> Head(Position(position.x, position.y - 1))
                Direction.Down -> Head(Position(position.x, position.y + 1))
            }
        }
    }

    data class Tail(var position: Position) {
        fun move(head: Head): Tail {
            if (position.x != head.x && position.y != head.y && position.dist(head.position) >= 2) {
                if (position.y > head.y) {
                    position = Position(position.x, position.y - 1)
                } else {
                    position = Position(position.x, position.y + 1)
                }

                if (position.x > head.x) {
                    position = Position(position.x - 1, position.y)
                } else {
                    position = Position(position.x + 1, position.y)
                }
            } else if (head.x - position.x > 1) {
                position = Position(position.x + 1, position.y)
            } else if (position.x - head.x > 1) {
                position = Position(position.x - 1, position.y)
            } else if (head.y - position.y > 1) {
                position = Position(position.x, position.y + 1)
            } else if (position.y - head.y > 1) {
                position = Position(position.x, position.y - 1)
            }
            return this
        }
    }

    companion object {
        fun parseInput(lines: List<String>): List<Move> {
            return lines.filter(String::isNotBlank).map { line ->
                val parts = line.split(' ', limit = 2)
                Move(Direction.from(parts[0][0]), parts[1].toInt())
            }
        }

        fun solve(moves: List<Move>): Int {
            var head = Head(Position(0, 0))
            var tail = Tail(Position(0, 0))

            val tailPositions = mutableSetOf<Position>()
            tailPositions.add(tail.position)
            moves.forEach { move ->
                (1..move.count).forEach {
                    head = head.move(move.direction)
                    tail = tail.move(head)
                    tailPositions.add(tail.position)
                }
            }

            return tailPositions.size
        }

        fun solveMultiKnot(moves: List<Move>, totalKnots: Int): Set<Position> {
            var head = Head(Position(0, 0))
            val tails = (1 until totalKnots).map { Tail(Position(0, 0)) }

            val tailPositions = mutableSetOf<Position>()
            tailPositions.add(tails.last().position)
            moves.forEach { move ->
                (1..move.count).forEach {
                    head = head.move(move.direction)
                    var next = head
                    tails.forEach { tail ->
                        next = Head(tail.move(next).position)
                    }
                    tailPositions.add(tails.last().position)
                }
            }

            return tailPositions
        }

    }
}

private val input = """
    R 5
    U 8
    L 8
    D 3
    R 17
    D 10
    L 25
    U 20
""".trimIndent()

fun main() {
    val moves = Day9.parseInput(Utils.readLines("day9/input.txt"))
//    val moves = Day9.parseInput(input.split("\n"))
    val solution = Day9.solve(moves)
    println(solution)

    val solution2 = Day9.solveMultiKnot(moves, 10)
    println(solution2.size)

}