class Day11 {
    data class Monkey(
        val items: MutableList<Long>,
        val operation: (old: Long) -> Long,
        val test: (toTest: Long) -> Int,
        val divisor: Int,
    )

    private val monkeys = listOf(
        Monkey(
            items = mutableListOf(59, 65, 86, 56, 74, 57, 56),
            operation = { old -> old * 17 },
            test = { if (it % 3 == 0L) 3 else 6 },
            divisor = 3
        ),
        Monkey(
            items = mutableListOf(63, 83, 50, 63, 56),
            operation = { old -> old + 2 },
            test = { if (it % 13 == 0L) 3 else 0 },
            divisor = 13,
        ),
        Monkey(
            items = mutableListOf(93, 79, 74, 55),
            operation = { old -> old + 1 },
            test = { if (it % 2 == 0L) 0 else 1 },
            divisor = 2,
        ),
        Monkey(
            items = mutableListOf(85, 61, 67, 88, 94, 69, 56, 91),
            operation = { old -> old + 7 },
            test = { if (it % 11 == 0L) 6 else 7 },
            divisor = 11,
        ),
        Monkey(
            items = mutableListOf(76, 50, 51),
            operation = { old -> old * old },
            test = { if (it % 19 == 0L) 2 else 5 },
            divisor = 19,
        ),
        Monkey(
            items = mutableListOf(77, 76),
            operation = { old -> old + 8 },
            test = { if (it % 17 == 0L) 2 else 1 },
            divisor = 17,
        ),
        Monkey(
            items = mutableListOf(74),
            operation = { old -> old * 2 },
            test = { if (it % 5 == 0L) 4 else 7 },
            divisor = 5,
        ),
        Monkey(
            items = mutableListOf(86, 85, 52, 86, 91, 95),
            operation = { old -> old + 6 },
            test = { if (it % 7 == 0L) 4 else 5 },
            divisor = 7,
        )
    )

    val testMonkeys = listOf(
        Monkey(
            items = mutableListOf(79, 98),
            operation = { it * 19 },
            test = { if (it % 23 == 0L) 2 else 3 },
            divisor = 23,
        ),
        Monkey(
            items = mutableListOf(54, 65, 75, 74),
            operation = { it + 6 },
            test = { if (it % 19 == 0L) 2 else 0 },
            divisor = 19,
        ),
        Monkey(
            items = mutableListOf(79, 60, 97),
            operation = { it * it },
            test = { if (it % 13 == 0L) 1 else 3 },
            divisor = 13,
        ),
        Monkey(
            items = mutableListOf(74),
            operation = { it + 3 },
            test = { if (it % 17 == 0L) 0 else 1 },
            divisor = 17,
        )
    )

    fun run(rounds: Int, runner: () -> Map<Int, Long>): Map<Int, Long> {
        val inspections = mutableMapOf<Int, Long>().withDefault { 0 }
        for (i in 1..rounds) {
            val newRound = runner()
            newRound.forEach { (monkey, count) ->
                inspections[monkey] = inspections.getValue(monkey) + count
            }
        }

        return inspections
    }

    fun runRound(): Map<Int, Long> {
        val inspections = mutableMapOf<Int, Long>().withDefault { 0 }
        monkeys.forEachIndexed { index, monkey ->
            while (true) {
                val currentItem = monkey.items.removeFirstOrNull() ?: break
                inspections[index] = inspections.getValue(index) + 1
                val worryLevel = monkey.operation(currentItem)
                val boredomeLevel = worryLevel / 3
                monkeys[monkey.test(boredomeLevel)].items.add(boredomeLevel)
            }
        }

        return inspections
    }

    fun runRoundPart2(): Map<Int, Long> {
        val inspections = mutableMapOf<Int, Long>().withDefault { 0 }
        val modulo = monkeys.map(Monkey::divisor).fold(1L) { x, y -> x * y }
//        val modulo = testMonkeys.map(Monkey::divisor).reduce { x, y -> x * y }
        monkeys.forEachIndexed { index, monkey ->
            while (true) {
                val currentItem = monkey.items.removeFirstOrNull() ?: break
                inspections[index] = inspections.getValue(index) + 1
                val worryLevel = monkey.operation(currentItem) % modulo
                monkeys[monkey.test(worryLevel)].items.add(worryLevel)
            }
        }

        return inspections
    }
}

fun main() {
    val day11 = Day11()

    println(day11.run(20) { day11.runRound() }.values.sorted().reversed().subList(0, 2).reduce { x, y -> x * y })
    // 13912549875 -- but something is wrong
    println(
        day11.run(10000) { day11.runRoundPart2() }.values.sorted().reversed().subList(0, 2).fold(1L) { x, y -> x * y })
}
