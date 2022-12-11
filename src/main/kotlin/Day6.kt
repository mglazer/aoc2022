import Day6.Companion.findStart

class Day6 {

    companion object {
        fun findStart(message: String, length: Int = 4): Int {
            for (i in 0..message.length - length) {
                if (message.substring(i, i + length).toSet().size == length) {
                    return i + length
                }
            }

            return -1;
        }
    }
}

fun main() {
    println(findStart("bvwbjplbgvbhsrlpgdmjqwftvncz"));
    println(findStart("nppdvjthqldpwncqszvftbrmjlhg"))
    println(findStart("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
    println(findStart("bvwbjplbgvbhsrlpgdmjqwftvncz", 14))
    println(findStart("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14))

    println(findStart(Utils.read("day6/input.txt")))
    println(findStart(Utils.read("day6/input.txt"), 14))
}