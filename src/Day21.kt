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
            if (isCoordinateVAlid(symbolPosition.add(it.coordinates))) {
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
            if (!allowedChars.contains(matrix[next.coordinates.y][next.coordinates.x])) continue

            matrix[next.coordinates.y][next.coordinates.x] = 'x'
            if (next.steps == 0) continue

            symbolToDirections(next.coordinates).forEach { direction ->
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

    fun part1(input: List<String>): Int {
        val stepsMatrix = StepsMatrix(input)
        val maxSteps = 64

        stepsMatrix.floodFill(maxSteps)

        val validCoordinates = stepsMatrix.matrix.flatMapIndexed { index, row ->
            row.mapIndexed { colIndex, char ->
                if (char == 'x') {
                    CharAtPosition(char, Coordinates(colIndex, index))
                } else null
            }
        }.filterNotNull().filter {
            val distance = abs(it.position.y - stepsMatrix.start.y) + abs(it.position.x - stepsMatrix.start.x)
            (distance % 2) == (maxSteps % 2)
        }
        return validCoordinates.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day21")
    println(part1(testInput))
}