class Day10 {

    companion object {
        fun parse(lines: List<String>): List<CPU.Instruction> {
            return lines.map {
                if (it.startsWith("noop")) {
                    CPU.Noop;
                } else {
                    CPU.Add(it.split(" ")[1].toInt())
                }
            }
        }
    }

}

class CPU {
    private var x = 1;
    private val screenWidth = 40

    private val measurementPoints = setOf<Int>(20, 60, 100, 140, 180, 220)

    sealed interface Instruction {
        fun length(): Int
    }

    object Noop : Instruction {
        override fun length(): Int = 1
    }

    data class Add(val value: Int) : Instruction {
        override fun length(): Int = 2
    }

    fun run(instructions: List<Instruction>): Pair<Map<Int, Int>, List<Boolean>> {
        var currentTick = 0
        val measurements = mutableMapOf<Int, Int>()
        val pixels = mutableListOf<Boolean>()
        for (instruction in instructions) {
            for (count in 1..instruction.length()) {
                currentTick += 1

                if (currentTick in measurementPoints) {
                    measurements[currentTick] = x
                }

                if (count == instruction.length()) {
                    when (instruction) {
                        is Noop -> {}
                        is Add -> x += instruction.value
                    }
                }

                val horizontalPos = (currentTick % 40)
                pixels.add(horizontalPos - 1 == x || horizontalPos == x || horizontalPos + 1 == x)

            }
        }

        return Pair(measurements, pixels)
    }
}

fun main() {
    val instructions = Day10.parse(Utils.readLines("day10/input.txt"))
    val cpu = CPU()
    val (measurements, pixels) = cpu.run(instructions)

    val signalStrength = measurements.map { (point, value) -> point * value }.sum()
    println(signalStrength)

    var pixelIdx: Int = 0
    var screenWidth = 40
    for (pixel in pixels) {
        if (pixel) {
            print("#")
        } else {
            print(".")
        }

        pixelIdx += 1
        if (pixelIdx % screenWidth == 0) {
            println()
        }
    }

}