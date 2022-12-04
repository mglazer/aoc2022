import java.nio.charset.StandardCharsets

class Day3 {
}

data class Rucksack(val left: Set<Char>, val right: Set<Char>) {

    fun priority(): Int {
        return left.intersect(right).sumOf { c -> priority(c) }
    }


    fun all(): Set<Char> {
        return left.union(right)
    }
}

private fun priority(char: Char): Int {
    if (char.isUpperCase()) {
        return char.code - 'A'.code + 27;
    } else {
        return char.code - 'a'.code + 1;
    }
}

data class ElfGroup(val rucksacks: List<Rucksack>) {
    fun priority(): Int {
        var common = rucksacks[0].all()
        for (i in 1 until rucksacks.size) {
            common = common.intersect(rucksacks[i].all())
        }

        return common.sumOf { c -> priority(c) }
    }
}

fun createRucksack(line: String): Rucksack {

    return Rucksack(
        line.substring(0, line.length / 2).toCharArray().toSet(),
        line.substring(line.length / 2).toCharArray().toSet()
    )
}

private const val Input = """
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
"""


fun main() {
    val rucksacks = ClassLoader.getSystemClassLoader().getResourceAsStream("day3/input1.txt")
        .readAllBytes()
        .toString(StandardCharsets.UTF_8)
        .split("\n").filter(String::isNotBlank).map(::createRucksack)

    println(rucksacks.sumOf(Rucksack::priority))

    val lines = ClassLoader.getSystemClassLoader().getResourceAsStream("day3/input1.txt")
        .readAllBytes()
        .toString(StandardCharsets.UTF_8)
        .split("\n")

    val elfGroups = mutableListOf<ElfGroup>()
    for (i in 0..lines.size - 2 step 3) {
        elfGroups.add(
            ElfGroup(
                listOf(
                    createRucksack(lines[i]),
                    createRucksack(lines[i + 1]),
                    createRucksack(lines[i + 2]),
                )
            )
        )
    }

    println(elfGroups.sumOf { e -> e.priority() })
}