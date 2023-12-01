fun main() {
    val customNums = mapOf(
        "one" to "on1ne",
        "two" to "tw2wo",
        "three" to "thre3hree",
        "four" to "fou4our",
        "five" to "fiv5ive",
        "six" to "si6ix",
        "seven" to "seve7even",
        "eight" to "eigh8ight",
        "nine" to "nin9ine",
    )

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val digits = line.filter { it.isDigit() }
            "${digits[0]}${digits[digits.length - 1]}".toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val parsedInput = input.map {
            it.replaceCustom(customNums)
        }
        println(parsedInput)
        return part1(parsedInput)
    }

    val testInput = readInput("day01")
    println(part2(testInput))

}
