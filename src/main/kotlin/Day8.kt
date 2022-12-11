import kotlin.math.abs

class Day8 {

    data class Coord(val x: Int, val y: Int) {
        operator fun plus(shift: Pair<Int, Int>) = Coord(x + shift.first, y + shift.second)
    }

    data class TreeGrid(private val grid: Array<IntArray>) {

        fun visibleTrees(): Set<Coord> {
            val visibleTrees = mutableSetOf<Coord>()
            for (row in 0 until grid.size) {
                for (col in 0 until grid[row].size) {
                    val coord = Coord(col, row)
                    if (visibleFromTop(coord) || visibleFromLeft(coord) || visibleFromRight(coord) || visibleFromBottom(
                            coord
                        )
                    ) {
                        visibleTrees.add(coord)
                    }
                }
            }
            return visibleTrees
        }

        fun scenicScore(coord: Coord): Int {
            val toTop =
                (coord.y - 1 downTo 0).map { Coord(coord.x, it) }
                    .firstOrNull { grid[it.y][it.x] >= grid[coord.y][coord.x] } ?: Coord(coord.x, 0)
            val toBottom =
                (coord.y + 1 until height()).map { Coord(coord.x, it) }
                    .firstOrNull { grid[it.y][it.x] >= grid[coord.y][coord.x] } ?: Coord(coord.x, height() - 1)
            val toLeft =
                (coord.x - 1 downTo 0).map { Coord(it, coord.y) }
                    .firstOrNull { grid[it.y][it.x] >= grid[coord.y][coord.x] }
                    ?: Coord(0, coord.y)
            val toRight =
                (coord.x + 1 until width()).map { Coord(it, coord.y) }
                    .firstOrNull { grid[it.y][it.x] >= grid[coord.y][coord.x] } ?: Coord(width() - 1, coord.y)

            return (coord.y - toTop.y) * (toBottom.y - coord.y) * (coord.x - toLeft.x) * (toRight.x - coord.x)
        }

        private fun visibleFromTop(coord: Coord): Boolean {
            if (coord.y == 0) {
                return true
            }

            return (0 until coord.y).all { grid[it][coord.x] < grid[coord.y][coord.x] }
        }

        private fun visibleFromLeft(coord: Coord): Boolean {
            if (coord.x == 0) {
                return true
            }

            return (0 until coord.x).all { grid[coord.y][it] < grid[coord.y][coord.x] }
        }

        private fun visibleFromRight(coord: Coord): Boolean {
            if (coord.x == width() - 1) {
                return true
            }

            return (width() - 1 downTo coord.x + 1).all { grid[coord.y][it] < grid[coord.y][coord.x] }
        }

        private fun visibleFromBottom(coord: Coord): Boolean {
            if (coord.y == height() - 1) {
                return true
            }

            return (height() - 1 downTo coord.y + 1).all { grid[it][coord.x] < grid[coord.y][coord.x] }
        }

        fun height() = grid.size

        fun width() = grid[0].size
    }

    companion object {
        fun buildTreeGrid(rows: List<String>): TreeGrid {
            return TreeGrid(rows.map { row ->
                row.toCharArray().map(Char::digitToInt).toIntArray()
            }.toTypedArray())
        }
    }

}

fun main() {

    val treeGrid = Day8.buildTreeGrid(Utils.readLines("day8/input.txt"))

    // perf is slow, because I was lazy and went with a terribly quadratic solution
    // likely better choice here would have been to at least use DP to memoize results
    // which definitely cannot be visible
    val visibleTrees = treeGrid.visibleTrees()
    println(visibleTrees.size)

    val scenicScores = (0 until treeGrid.height()).map { IntArray(treeGrid.width()) }.toTypedArray()

    for (row in 0 until scenicScores.size) {
        for (col in 0 until scenicScores[row].size) {
            scenicScores[row][col] = treeGrid.scenicScore(Day8.Coord(col, row))
        }
    }

    val maxValue = scenicScores.maxOf { it.max() }
    println("Max Score: $maxValue")
}