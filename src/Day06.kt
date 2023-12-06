data class Race(val time: Long, val record: Long) {
    fun getNumberOfWaysToBeatRecord(): Int {
        var numberOfWays = 0
        (1..time).forEach {
            val boatSpeed = it
            val remainingTime = time - it
            val distance = remainingTime * boatSpeed
            if (distance > record) numberOfWays++
        }
        return numberOfWays
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val times = input[0].substringAfter(':').toListOfNumbers()
        val distances = input[1].substringAfter(":").toListOfNumbers()
        val races = times.zip(distances) { time, distance -> Race(time, distance) }
        return races.map { race ->
            race.getNumberOfWaysToBeatRecord()
        }.multiplyContent().toInt()
    }

    fun part2(input: List<String>): Int {
        val time = input[0].substringAfter(':').toNumber()
        val distance = input[1].substringAfter(":").toNumber()
        val race = Race(time, distance)
        return race.getNumberOfWaysToBeatRecord()
    }

    val testInput = readInput("day06")
    println(part2(testInput))

}
