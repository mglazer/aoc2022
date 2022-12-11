class Day5 {
    data class CrateStack(val values: List<Char>) {
        fun push(char: Char) = CrateStack(values + char)

        fun push(chars: List<Char>) = CrateStack(values + chars);

        fun pop(): Pair<Char, CrateStack> {
            val mutableValues = values.toMutableList();
            val last = mutableValues.removeLast()
            return Pair(last, CrateStack(mutableValues))
        }

        fun top(): Char {
            return values.last()
        }
    }

    data class Move(val count: Int, val from: Int, val to: Int)

    enum class ParseState {
        START,
        CRATES,
        MOVES,
    }

    companion object {
        fun applyMove(crateStacks: MutableList<CrateStack>, move: Move): MutableList<CrateStack> {
            for (i in 1..move.count) {
                val (c, stack) = crateStacks[move.from - 1].pop()
                crateStacks[move.from - 1] = stack
                crateStacks[move.to - 1] = crateStacks[move.to - 1].push(c)
            }
            return crateStacks
        }

        fun applyMultiMove(crateStacks: MutableList<CrateStack>, move: Move): MutableList<CrateStack> {
            val movedList = mutableListOf<Char>();
            for (i in 1..move.count) {
                val (c, stack) = crateStacks[move.from - 1].pop()
                crateStacks[move.from - 1] = stack
                movedList.add(c)
            }
            crateStacks[move.to - 1] = crateStacks[move.to - 1].push(movedList.reversed());
            return crateStacks
        }

        private fun parseCrates(lines: List<String>, count: Int): MutableList<CrateStack> {
            val crateStacks = (1..count).map { mutableListOf<Char>() }.toList()

            lines.reversed().forEach { line ->
                for (i in 1 until line.length step 4) {
                    if (line[i].isLetter()) {
                        crateStacks[i / 4].add(line[i])
                    }
                }
            }

            return crateStacks.map(::CrateStack).toMutableList()
        }

        val moveRegex = Regex("move (?<stack>\\d+) from (?<from>\\d+) to (?<to>\\d+)")
        private fun parseMoves(moves: List<String>): List<Move> {

            return moves.map {
                val parsed = moveRegex.matchEntire(it)
                Move(
                    parsed!!.groups["stack"]!!.value.toInt(),
                    parsed!!.groups["from"]!!.value.toInt(),
                    parsed!!.groups["to"]!!.value.toInt()
                )
            }
        }

        fun parseInput(lines: List<String>): Pair<List<CrateStack>, List<Move>> {
            var parseState = ParseState.START;
            val crateLines = mutableListOf<String>()
            val moves = mutableListOf<String>()
            var numCrateStacks = 0
            lines.forEach parse@{ line ->
                when (parseState) {
                    ParseState.START -> {
                        if (line.isBlank()) {
                            return@parse
                        } else {
                            parseState = ParseState.CRATES
                            crateLines.add(line);
                        }
                    }

                    ParseState.CRATES -> {
                        if (line.contains("[")) {
                            crateLines.add(line);
                        } else {
                            numCrateStacks = line.split(" ").filter { it.isNotBlank() }.last().toInt()
                            parseState = ParseState.MOVES
                        }
                    }

                    ParseState.MOVES -> {
                        if (line.isBlank()) {
                            return@parse
                        } else {
                            moves.add(line)
                        }
                    }
                }
            }

            return Pair(parseCrates(crateLines, numCrateStacks), parseMoves(moves))
        }
    }
}

private const val Input = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
"""

fun main() {
    val (crateStack, moves) = Day5.parseInput(Utils.readLines("day5/input.txt"))

    var mutableStack = crateStack.toMutableList()
    moves.forEach { move ->
        mutableStack = Day5.applyMove(mutableStack, move)
    }

    println(mutableStack.map { s -> s.top() }.joinToString(""))

    var mutableStack2 = crateStack.toMutableList();
    moves.forEach { move ->
        mutableStack2 = Day5.applyMultiMove(mutableStack2, move)
    }

    println(mutableStack2.map { s -> s.top() }.joinToString(""))

}