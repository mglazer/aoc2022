import java.nio.charset.StandardCharsets

class Day4 {
    data class Elf(val range: IntRange) {
        fun contains(other: Elf): Boolean = range.contains(other.range.first) && range.contains(other.range.last)

        fun overlaps(other: Elf): Boolean {
            return range.contains(other.range.first) || range.contains(other.range.last)
        }
    }

    data class ElfPair(val elf1: Elf, val elf2: Elf) {
        fun hasContainment(): Boolean {
            return elf1.contains(elf2) || elf2.contains(elf1)
        }

        fun overlaps(): Boolean {
            return elf1.overlaps(elf2) || elf2.overlaps(elf1)
        }
    }


    companion object {
        fun createElfPairs(input: String): List<ElfPair> {
            return input.split("\n").filter(String::isNotBlank).map {
                val pairs = it.split(",").map(String::trim)
                ElfPair(createElf(pairs[0]), createElf(pairs[1]))
            }.toList()
        }

        private fun createElf(range: String): Elf {
            val parts = range.split("-")
            return Elf(parts[0].toInt()..parts[1].toInt())
        }
    }
}

private const val Input = """
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8    
"""

fun main() {
    val elfPairs = Day4.createElfPairs(
        ClassLoader.getSystemClassLoader().getResourceAsStream("day4/input.txt").readAllBytes()
            .toString(StandardCharsets.UTF_8)
    )
//    val elfPairs = Day4.createElfPairs(Input)
    println(elfPairs.filter(Day4.ElfPair::hasContainment).size)

    println(elfPairs.filter(Day4.ElfPair::overlaps).size)
}