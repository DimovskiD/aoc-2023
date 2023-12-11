import kotlin.math.abs

data class Galaxy(val x: Long, val y: Long)
class GalaxyMap(input: List<String>) {

    private val matrix: List<List<Char>>
    private var galaxies: List<Galaxy>

    init {
        matrix = input.map { row ->
            row.map { charAtColumn ->
                charAtColumn
            }.toMutableList()
        }

        galaxies = matrix.mapIndexed { y, row ->
            row.mapIndexed { x, char -> if (char == '#') Galaxy(x.toLong(), y.toLong()) else null }.filterNotNull()
        }.flatten()

    }

    fun expand(expansionFactor: Long) {
        //if expansion factor is two, we expand with one line (+ the existing one = two)
        val expansion = expansionFactor - 1

        var previouslyAddedColumns = 0L
        matrix[0].forEachIndexed { index, _ ->
            if (galaxies.none { it.y == index + previouslyAddedColumns }) {
                val y = index + previouslyAddedColumns
                previouslyAddedColumns += expansion
                galaxies = galaxies.map { galaxy ->
                    if (galaxy.y > y) Galaxy(galaxy.x, galaxy.y + expansion) else galaxy
                }
            }
        }

        var previouslyAddedRows = 0L
        matrix.forEachIndexed { index, _ ->
            if (galaxies.none { it.x == index + previouslyAddedRows }) {
                val x = index + previouslyAddedRows
                previouslyAddedRows += expansion
                galaxies = galaxies.map { galaxy ->
                    if (galaxy.x > x) Galaxy(galaxy.x + expansion, galaxy.y) else galaxy
                }
            }
        }
        galaxies.toString()

    }

    fun getSumOfShortestDistances(): Long {
        return galaxies.indices.sumOf { index ->
            val galaxy = galaxies[index]
            galaxies.subList(index + 1, galaxies.size).sumOf { other ->
                abs(galaxy.x - other.x) + abs(galaxy.y - other.y)
            }
        }
    }

    override fun toString(): String {
        return buildString {
            matrix.forEach { it ->
                it.forEach {
                    append(it)
                }
                append("\n")
            }
        }
    }

}

fun main() {

    fun part1(input: List<String>): Long {
        val galaxyMap = GalaxyMap(input)
        galaxyMap.expand(2)
        return galaxyMap.getSumOfShortestDistances()
    }

    fun part2(input: List<String>): Long {
        val galaxyMap = GalaxyMap(input)
        galaxyMap.expand(1000000)
        return galaxyMap.getSumOfShortestDistances()
    }

    val testInput = readInput("day11")
    println(part2(testInput))

}
