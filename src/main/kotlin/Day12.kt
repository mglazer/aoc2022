import java.util.PriorityQueue

class Day12 {

    data class WeightedSource(val weight: Int, val coord: Pair<Int, Int>) : Comparable<WeightedSource> {
        override fun compareTo(other: WeightedSource): Int {
            return weight.compareTo(other.weight)
        }
    }

    class Grid(
        private val levels: Array<IntArray>,
        val source: Pair<Int, Int>,
        private val target: Pair<Int, Int>
    ) {
        fun getStarts(): List<Pair<Int, Int>> {
            val list = mutableListOf<Pair<Int, Int>>()
            for (row in levels.indices) {
                for (col in levels[row].indices) {
                    if (levels[row][col] <= 1) {
                        list.add(Pair(row, col))
                    }
                }
            }
            return list.toList()
        }

        fun findPath2(start: Pair<Int, Int>): Int? {
            val queue = ArrayDeque<Pair<Int, Int>>()

            queue.add(start)

            val distances = mutableMapOf<Pair<Int, Int>, Int>()
            distances[start] = 0
            while (queue.isNotEmpty()) {

                val current = queue.removeFirst()
                if (current == target) {
                    return distances.getValue(current)
                }

                val neighbors = findNeighbors(current).map(WeightedSource::coord)
                for (neighbor in neighbors) {
                    if (!distances.containsKey(neighbor)) {
                        distances[neighbor] = distances.getValue(current) + 1
                        queue.add(neighbor)
                    }
                }
            }

            return null
        }

        private fun findNeighbors(point: Pair<Int, Int>): List<WeightedSource> {
            val current = levels[point.first][point.second]

            val neighbors = mutableListOf<WeightedSource>()
            val pairsToCheck = listOf(
                Pair(point.first - 1, point.second),
                Pair(point.first + 1, point.second),
                Pair(point.first, point.second - 1),
                Pair(point.first, point.second + 1),
            )
            pairsToCheck.filter(::inBounds).forEach { pair ->
                val diff = levels[pair.first][pair.second] - current
                if (diff <= 1) {
                    neighbors.add(WeightedSource(levels[pair.first][pair.second], pair))
                }
            }

            return neighbors
        }

        private fun inBounds(pair: Pair<Int, Int>): Boolean {
            return pair.first >= 0 && pair.first < levels.size && pair.second >= 0 && pair.second < levels[0].size
        }
    }


    companion object {
        fun parse(lines: List<String>): Grid {
            var source: Pair<Int, Int>? = null
            var target: Pair<Int, Int>? = null
            val levels = lines.mapIndexed { row, line ->
                line.toCharArray().mapIndexed { col, c ->
                    when (c) {
                        'S' -> {
                            source = Pair(row, col)
                            0
                        }

                        'E' -> {
                            target = Pair(row, col)
                            27
                        }

                        else -> c.code - 'a'.code + 1
                    }
                }.toIntArray()
            }

            require(source != null)
            require(target != null)

            return Grid(levels.toTypedArray(), source!!, target!!)
        }
    }


}

private val input = """
    Sabqponm
    abcryxxl
    accszExk
    acctuvwj
    abdefghi
""".trimIndent()

/*
v..v<<<<
>v.vv<<^
.>vv>E^^
..v>>>^^
..>>>>>^
 */

fun main() {
    val grid = Day12.parse(Utils.readLines("day12/input.txt"))
//    val grid = Day12.parse(input.split("\n").filter(String::isNotBlank))

    println("Path: ${grid.findPath2(grid.source)}")

    val shortest = grid.getStarts().map { grid.findPath2(it) }.filterNotNull().min()
    println("Shortest: ${shortest}")
//    println("Length: ${grid.findPath().size - 1}")
}