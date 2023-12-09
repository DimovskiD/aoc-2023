fun main() {

    fun getListOfDifferences(input: List<Long>): Long {
        val listOfDifferences = input.mapIndexedNotNull { index, number ->
            return@mapIndexedNotNull if (index + 1 >= input.size) null else input[index + 1] - number
        }.toMutableList()
        val result = if (listOfDifferences.all { it == 0L }) 0L else getListOfDifferences(listOfDifferences)
        return input.last() + result
    }

    fun part1(input: List<List<Long>>): Long {
        return input.sumOf {
            getListOfDifferences(it)
        }
    }

    fun part2(input: List<List<Long>>): Long {
        val result = input.map {
            it.reversed()
        }.sumOf { getListOfDifferences(it) }
        return result
    }

    val testInput = readInput("day09")
    val sequenceOfNumbers = testInput.map { it.split(" ").map { i -> i.toLong() } }

    println(part2(sequenceOfNumbers))
}
