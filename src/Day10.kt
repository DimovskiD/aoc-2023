import helpers.Coordinates
import helpers.MovementDirection
import helpers.MovementDirection.*
import helpers.MovementMatrix

class Maze(schema: List<String>): MovementMatrix(schema) {

    private lateinit var startingCoordinates: Coordinates
    private var checkedStartingDirections: Int = 0
    private val validStartingDirections = mutableListOf<MovementDirection>()

    init {
        matrix.forEachIndexed { index, row ->
            row.forEachIndexed { colIndex, char ->
                if (char == 'S') startingCoordinates = Coordinates(colIndex, index)
            }
        }
    }
    override fun symbolToDirections(symbolPosition: Coordinates, comingFrom: MovementDirection): List<MovementDirection>? {
        return listOfNotNull(when (getSymbolAtCoordinates(symbolPosition)) {
            '|' -> if (comingFrom == UP) DOWN else if (comingFrom == DOWN) UP else null
            '-' -> if (comingFrom == LEFT) RIGHT else if (comingFrom == RIGHT) LEFT else null
            'L' -> if (comingFrom == UP) RIGHT else if (comingFrom == RIGHT) UP else null
            'J' -> if (comingFrom == UP) LEFT else if (comingFrom == LEFT) UP else null
            '7' -> if (comingFrom == DOWN) LEFT else if (comingFrom == LEFT) DOWN else null
            'F' -> if (comingFrom == DOWN) RIGHT else if (comingFrom == RIGHT) DOWN else null
            else -> null
        })

    }
    private fun getNextValidStartingDirection(): MovementDirection {
        checkedStartingDirections++
        if (validStartingDirections.isEmpty()) {
            validStartingDirections.addAll(MovementDirection.entries.mapNotNull { direction ->
                if (getNextDirection(
                        startingCoordinates.add(direction.coordinates),
                        startingCoordinates
                    ) != null
                ) direction else null
            })
        }
        return validStartingDirections[checkedStartingDirections]
    }

    fun isUShape(lastCorner: Char, pipe: Char): Boolean =
        (pipe == 'J' && lastCorner == 'L') || (pipe == '7' && lastCorner == 'F')

    fun isCorner(pipe: Char?): Boolean {
        return pipe == 'L' || pipe == 'F' || pipe == 'J' || pipe == '7'
    }

    fun isBarrier(pipe: Char?): Boolean {
        return pipe == '|' || pipe == 'S'
    }

    fun getTraversalMatrix(): Array<Array<Char?>> {
        var found = false
        var traversal: Array<Array<Char?>> = Array(matrix.size) { arrayOfNulls(matrix[0].size) }
        while (!found) {
            var previousLocation = startingCoordinates
            val initialDirection = getNextValidStartingDirection()
            var currentLocation = startingCoordinates.add(initialDirection.coordinates)
            traversal = Array(matrix.size) { arrayOfNulls(matrix[0].size) }

            traversal[previousLocation.y][previousLocation.x] = 'S'
            while (getSymbolAtCoordinates(coordinates = currentLocation) != 'S') {
                val tmpCurrent = currentLocation

                val comingFrom = getDirectionOfMovement(previousLocation, currentLocation)
                val direction =
                    symbolToDirections(
                        currentLocation,
                        comingFrom!!
                    )
                if (direction == null) {
                    traversal = Array(matrix.size) { arrayOfNulls(matrix[0].size) }
                    break
                } else {
                    traversal[currentLocation.y][currentLocation.x] = matrix[currentLocation.y][currentLocation.x]
                }
                currentLocation = currentLocation.add(direction[0].coordinates)

                previousLocation = tmpCurrent
            }
            if (getSymbolAtCoordinates(coordinates = currentLocation) == 'S') {
                found = true
            }
        }
        return traversal
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return Maze(input).getTraversalMatrix().sumOf { row ->
            row.count { it != null }
        } / 2
    }

    fun part2(input: List<String>): Int {
        val maze = Maze(input)
        var count = 0

        maze.getTraversalMatrix().forEach {
            var inRegion = false
            var lastCorner: Char? = null

            it.forEach { pipe ->
                if (maze.isBarrier(pipe)) inRegion = !inRegion
                else if (pipe == null && inRegion) {
                    count++
                } else if (maze.isCorner(pipe)) {
                    if (lastCorner == null) lastCorner = pipe
                    else if (!maze.isUShape(lastCorner!!, pipe!!)) {
                        inRegion = !inRegion
                        lastCorner = null
                    } else lastCorner = null
                }
            }
        }
        return count
    }

    val testInput = readInput("day10")

    println(part2(testInput))
}
