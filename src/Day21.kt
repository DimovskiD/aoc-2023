import helpers.Coordinates
import helpers.MovementDirection
import helpers.MovementMatrix
import kotlin.math.abs

class StepsMatrix(input: List<String>) : MovementMatrix(input) {

    var start: Coordinates = Coordinates(0, 0)

    init {
        matrix.forEachIndexed { index, row ->
            row.forEachIndexed { colIndex, char ->
                if (char == 'S') start = Coordinates(colIndex, index)
            }
        }
    }

    override fun symbolToDirections(
        symbolPosition: Coordinates,
        comingFrom: MovementDirection?
    ): List<MovementDirection> {
        return MovementDirection.entries.mapNotNull {
            if (isCoordinateValid(symbolPosition.add(it.coordinates))) {
                it
            } else null
        }
    }

    fun floodFill(stepsLeft: Int) {
        val allowedChars = listOf('.', 'S')

        val queue = mutableListOf<CoordinatesWithSteps>()
        queue.add(CoordinatesWithSteps(start, stepsLeft))

        while (queue.isNotEmpty()) {
            val next = queue.shift()
            if (next.coordinates.x >= matrix[0].size || next.coordinates.y >= matrix.size || next.coordinates.x < 0 || next.coordinates.y < 0 || !allowedChars.contains(
                    matrix[next.coordinates.y][next.coordinates.x]
                )
            ) continue

            matrix[next.coordinates.y][next.coordinates.x] = 'x'
            if (next.steps == 0) continue

            MovementDirection.entries.forEach { direction ->
                queue.add(
                    next.copy(
                        coordinates = next.coordinates.add(direction.coordinates),
                        steps = next.steps - 1
                    )
                )
            }
        }
    }
}

data class CoordinatesWithSteps(val coordinates: Coordinates, val steps: Int)
data class CharAtPosition(val element: Char, val position: Coordinates)

fun main() {

    fun getVisitedCoordinates(stepsMatrix: StepsMatrix) =
        stepsMatrix.matrix.flatMapIndexed { index, row ->
            row.mapIndexed { colIndex, char ->
                if (char == 'x') {
                    CharAtPosition(char, Coordinates(colIndex, index))
                } else null
            }
        }.filterNotNull()

    fun part1(input: List<String>): Int {
        val stepsMatrix = StepsMatrix(input)
        val maxSteps = 64

        stepsMatrix.floodFill(maxSteps)

        val validCoordinates = getVisitedCoordinates(stepsMatrix).filter {
            val distance = manhattanDistance(it.position, stepsMatrix.start)
            (distance % 2) == (maxSteps % 2)
        }
        return validCoordinates.size
    }


    // big thank you to this great guide for part 2, couldn't've figured it out without it
    // https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
    fun part2(input: List<String>): Long {
        val stepsMatrix = StepsMatrix(input)
        val targetSteps = 26501365L

        stepsMatrix.floodFill(targetSteps.toInt())

        val visitedCoordinates = getVisitedCoordinates(stepsMatrix).map {
            manhattanDistance(it.position, stepsMatrix.start)
        }

        val evenCorners = visitedCoordinates.count { coordinates -> coordinates % 2 == 0 && coordinates > 65 }
        val oddCorners = visitedCoordinates.count { coordinates -> coordinates % 2 == 1 && coordinates > 65 }

        val evenFull = visitedCoordinates.count { it % 2 == 0 }
        val oddFull = visitedCoordinates.count { it % 2 == 1 }

        val n = (targetSteps - (stepsMatrix.matrix[0].size / 2)) / stepsMatrix.matrix[0].size
        val even = n * n
        val odd = (n + 1) * (n + 1)

        return odd * oddFull + even * evenFull - ((n + 1) * oddCorners) + (n * evenCorners)
    }

    val testInput = readInput("day21")
    println(part2(testInput))
}