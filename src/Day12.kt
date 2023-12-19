import helpers.memoize

fun processSequence(sequence: String, numbers: List<Int>): Long {
    return when {
        sequence.replace("?", "").replace(".", "").isEmpty() && numbers.isEmpty() -> 1
        numbers.size == 1 && sequence.length == numbers[0] && sequence.all { it == '?' } -> 1
        numbers.isEmpty() || sequence.length < numbers.size -> 0
        sequence.startsWith(".") -> return memoizedProcessSequence(sequence.substring(1), numbers)
        sequence.startsWith("#") && sequence.length >= numbers[0] &&
                (sequence.substring(0, numbers[0])
                    .all { it != '.' } &&
                        (sequence.length == numbers[0] || sequence[numbers[0]] == '.' || sequence[numbers[0]] == '?')) -> {
            memoizedProcessSequence(
                sequence.substring(if (sequence.length > numbers[0]) numbers[0] + 1 else numbers[0]),
                if (numbers.size - 1 == 0) emptyList() else numbers.subList(1, numbers.size)
            )
        }
        sequence.length > 1 && sequence[0] == '?' -> {
            val res1 = memoizedProcessSequence(sequence.replaceFirst('?', '.'), numbers)
            val res2 = memoizedProcessSequence(sequence.replaceFirst('?', '#'), numbers)

            res1 + res2
        }
        else -> 0
    }
}

val memoizedProcessSequence = { x: String, numbers: List<Int> -> processSequence(x, numbers) }.memoize()

fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf {
            val sequence = it.substringBefore(" ")
            val numbers = it.substringAfter(" ").split(",").map { it.toInt() }

            memoizedProcessSequence(sequence, numbers)
        }
    }

    fun part2(input: List<String>): Long {
       return 0L
    }

    val testInput = readInput("day12")
    println(part2(testInput))

}
