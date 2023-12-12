class Memoize<in T, in X, out R>(val f: (T, List<X>) -> R) : (T, List<X>) -> R {
    private val values = mutableMapOf<Pair<T, List<X>>, R>()
    override fun invoke(x: T, list: List<X>): R {
        return values.getOrPut(x to list) { f(x, list) }
    }
}

fun <T, X, R> ((T, List<X>) -> R).memoize(): (T, List<X>) -> R = Memoize(this)

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
        return input.sumOf { line ->
            val sequence = line.substringBefore(" ")
            val numbers = line.substringAfter(" ").split(",").map { it.toInt() }

            val sequences = buildString {
                repeat(5) { repeat ->
                    append(sequence)
                    if (repeat != 4) append("?")
                }
            }
            val expandedNumbers = buildList { repeat(5) { add(numbers) } }
            memoizedProcessSequence(sequences, expandedNumbers.flatten())
        }
    }

    val testInput = readInput("day12")
    println(part2(testInput))

}
