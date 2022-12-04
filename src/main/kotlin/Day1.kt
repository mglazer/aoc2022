import java.nio.charset.StandardCharsets

private class Day1 {
    fun solve(input: String, count: Int = 1): List<Elf> {
        val elves = input.split("\n\n")
            .mapIndexed { index, calories ->
                Elf(
                    calories.split("\n").filter(String::isNotBlank).map(String::toInt),
                    index + 1
                )
            }

        return elves.sortedByDescending(Elf::totalCalories).subList(0, count)
    }

}

private const val Input1 = """
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
"""

const val Input2 = """
    
"""

private data class Elf(val carrying: List<Int>, val index: Int) {
    fun totalCalories() = carrying.sum()
}

fun main(args: Array<String>) {
    println(Day1().solve(Input1))
    println(
        Day1().solve(
            ClassLoader.getSystemClassLoader().getResourceAsStream("day1/input1.txt").readAllBytes()
                .toString(StandardCharsets.UTF_8)
        ).first().totalCalories()
    )
    println(
        Day1().solve(
            ClassLoader.getSystemClassLoader().getResourceAsStream("day1/input1.txt").readAllBytes()
                .toString(StandardCharsets.UTF_8),
            3
        ).sumOf(Elf::totalCalories)
    )

}
