class Day13 {

    sealed interface Packet {
        fun add(value: Packet): Packet
        fun first(): Packet
        fun removeLast(): Packet
    }

    data class ListPacket(val list: MutableList<Packet> = mutableListOf()) : Packet {
        override fun add(value: Packet): ListPacket {
            list.add(value)
            return this
        }

        override fun first(): Packet {
            return list.first()
        }

        fun rest(): ListPacket {
            return ListPacket(list.drop(1).toMutableList())
        }

        fun isEmpty(): Boolean {
            return list.isEmpty()
        }

        fun isNotEmpty(): Boolean {
            return list.isNotEmpty()
        }

        override fun removeLast(): Packet {
            return list.last()
        }

        override fun toString(): String {
            return "[" + list.joinToString(",") + "]"
        }
    }

    data class ValuePacket(val value: Int) : Packet {
        override fun add(value: Packet): ValuePacket {
            throw IllegalStateException()
        }

        override fun first(): Packet {
            throw IllegalStateException()
        }

        override fun removeLast(): ListPacket {
            throw IllegalStateException()
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    enum class ComparisonResult {
        RIGHT,
        WRONG,
        CONTINUE
    }

    companion object {
        fun compare(left: Packet, right: Packet): ComparisonResult {
            if (left is ValuePacket && right is ValuePacket) {
                return if (left.value < right.value) {
                    ComparisonResult.RIGHT
                } else if (left.value > right.value) {
                    ComparisonResult.WRONG
                } else {
                    ComparisonResult.CONTINUE
                }
            } else if (left is ListPacket && right is ListPacket) {
                return if (left.isEmpty() && right.isNotEmpty()) {
                    ComparisonResult.RIGHT
                } else if (left.isNotEmpty() && right.isEmpty()) {
                    ComparisonResult.WRONG
                } else if (left.isEmpty() && right.isEmpty()) {
                    ComparisonResult.CONTINUE
                } else {
                    when (compare(left.first(), right.first())) {
                        ComparisonResult.RIGHT -> ComparisonResult.RIGHT
                        ComparisonResult.WRONG -> ComparisonResult.WRONG
                        ComparisonResult.CONTINUE -> compare(left.rest(), right.rest())
                    }
                }
            } else if (left is ValuePacket) {
                return compare(ListPacket(mutableListOf(left)), right)
            } else if (right is ValuePacket) {
                return compare(left, ListPacket(mutableListOf(right)))
            } else {
                throw IllegalStateException("$left -> $right")
            }
        }

        fun parse(string: String): Packet {
            val parseStack = ArrayDeque<ListPacket>()

            var i = 0
            while (true) {
                if (i == string.length) {
                    break
                }

                when (val c = string[i]) {
                    '[' -> parseStack.add(ListPacket())
                    ']' -> {
                        val s = parseStack.removeLast()
                        if (parseStack.isEmpty()) {
                            return s
                        } else {
                            parseStack.last().add(s)
                        }
                    }

                    ',' -> {}
                    else -> {
                        if (string[i + 1].isDigit()) {
                            val intVal = Integer.parseInt(c.toString() + string[i + 1])
                            parseStack.last().add(ValuePacket(intVal))
                            i += 1;
                        } else {
                            val intVal = Integer.parseInt(c.toString())
                            parseStack.last().add(ValuePacket(intVal))
                        }
                    }
                }
                i += 1
            }

            return ListPacket(parseStack.toMutableList())
        }

    }
}

private val input = """
    [1,1,3,1,1]
    [1,1,5,1,1]

    [[1],[2,3,4]]
    [[1],4]

    [9]
    [[8,7,6]]

    [[4,4],4,4]
    [[4,4],4,4,4]

    [7,7,7,7]
    [7,7,7]

    []
    [3]

    [[[]]]
    [[]]

    [1,[2,[3,[4,[5,6,7]]]],8,9]
    [1,[2,[3,[4,[5,6,0]]]],8,9]
    
    [[[],0,10],[[[8],[4,0,1],[5],[7],0],8],[9,8,0,8,9],[[],[[0]],1,7,[9,6,[7],[1,1],6]],[[4,[6,5],[3,9,9,8,8]],[[],[10,5,5,7,3],5,5],9,5,2]]
    [[8,2,[1,4,9,9],6,6],[],[[3,[3,5,3,10],10,[],[7,0,3,1]]],[[[]],6,[[10,6]]],[[2,3,[4,2,4,6],[10,5],[7]],[[3,8,2,4,7],9,[2],[]],10,[[],8,9],[[4],[3,9],6,[9,10,2],6]]]
""".trimIndent()

fun main() {
    val packets = Utils.readLines("day13/input.txt").filter(String::isNotBlank).map { Day13.parse(it) }


    val pairs = (packets.indices step 2).map { Pair(packets[it], packets[it + 1]) }

    val result = pairs.flatMapIndexed { index, pair ->
        requireNotNull(pair.first)
        requireNotNull(pair.second)
        if (Day13.compare(pair.first!!, pair.second!!) == Day13.ComparisonResult.RIGHT) {
            listOf(index + 1)
        } else {
            emptyList()
        }
    }
    println(result.sum())

    val markerPackets = listOf(
        Day13.ListPacket(mutableListOf(Day13.ListPacket(mutableListOf(Day13.ValuePacket(2))))), Day13.ListPacket(
            mutableListOf(Day13.ListPacket(mutableListOf(Day13.ValuePacket(6))))
        )
    )
    val fixedPackets = packets + markerPackets

    val sortedPackets = fixedPackets.sortedWith { left, right ->
        when (Day13.compare(left, right)) {
            Day13.ComparisonResult.CONTINUE -> 0
            Day13.ComparisonResult.WRONG -> 1
            Day13.ComparisonResult.RIGHT -> -1
        }
    }

    println((sortedPackets.indexOf(markerPackets[0]) + 1) * (sortedPackets.indexOf(markerPackets[1]) + 1))
}