import MazeDirection.Companion.symbolToDirection

data class Coordinates(val x: Int, val y: Int) {

    fun add(coordinates: Coordinates): Coordinates {
        return Coordinates(x + coordinates.x, y + coordinates.y)
    }
}

enum class MazeDirection(val coordinates: Coordinates) {
    UP(Coordinates(0, -1)), DOWN(Coordinates(0, 1)), LEFT(Coordinates(-1, 0)), RIGHT(Coordinates(1, 0));

    companion object {

        private fun comingFromRight(currentLocation: Coordinates, previousLocation: Coordinates): Boolean {
            return currentLocation.add(RIGHT.coordinates) == previousLocation
        }

        private fun comingFromLeft(currentLocation: Coordinates, previousLocation: Coordinates): Boolean {
            return currentLocation.add(LEFT.coordinates) == previousLocation
        }

        private fun comingFromTop(currentLocation: Coordinates, previousLocation: Coordinates): Boolean {
            return currentLocation.add(UP.coordinates) == previousLocation
        }

        private fun comingFromBottom(currentLocation: Coordinates, previousLocation: Coordinates): Boolean {
            return currentLocation.add(DOWN.coordinates) == previousLocation
        }

        fun symbolToDirection(
            symbol: Char,
            currentLocation: Coordinates,
            previousLocation: Coordinates,
        ): MazeDirection? {
            return when (symbol) {
                '|' -> if (comingFromTop(currentLocation, previousLocation)) DOWN else if (comingFromBottom(
                        currentLocation,
                        previousLocation
                    )
                ) UP else null

                '-' -> if (comingFromLeft(currentLocation, previousLocation)) RIGHT else if (comingFromRight(
                        currentLocation,
                        previousLocation
                    )
                ) LEFT else null

                'L' -> if (comingFromTop(currentLocation, previousLocation)) RIGHT else if (comingFromRight(
                        currentLocation,
                        previousLocation
                    )
                ) UP else null

                'J' -> if (comingFromTop(currentLocation, previousLocation)) LEFT else if (comingFromLeft(
                        currentLocation,
                        previousLocation
                    )
                ) UP else null

                '7' -> if (comingFromBottom(currentLocation, previousLocation)) LEFT else if (comingFromLeft(
                        currentLocation,
                        previousLocation
                    )
                ) DOWN else null

                'F' -> if (comingFromBottom(currentLocation, previousLocation)) RIGHT else if (comingFromRight(
                        currentLocation,
                        previousLocation
                    )
                ) DOWN else null

                else -> null
            }
        }
    }
}

class Maze(schema: List<String>) {

    private val matrix: Array<Array<Char>>
    private lateinit var startingCoordinates: Coordinates
    private var checkedStartingDirections: Int = 0
    private val validStartingDirections = mutableListOf<MazeDirection>()

    init {
        matrix = Array(schema.size) { row ->
            val length = schema[row].length
            val arr = Array(length) { column ->
                val char = schema[row][column]
                if (char == 'S') startingCoordinates = Coordinates(column, row)
                print(char)
                char
            }
            println(" ")
            arr
        }
    }

    private fun getNextDirection(currentLocation: Coordinates, previousLocation: Coordinates): MazeDirection? {
        if (currentLocation.x < 0 || currentLocation.y < 0 || currentLocation.x >= matrix[0].size || currentLocation.y >= matrix.size) return null
        val ret = symbolToDirection(matrix[currentLocation.y][currentLocation.x], currentLocation, previousLocation)
        return ret
    }

    private fun getNextValidStartingDirection(): MazeDirection {
        checkedStartingDirections++
        if (validStartingDirections.isEmpty()) {
            validStartingDirections.addAll(MazeDirection.entries.mapNotNull { direction ->
                if (getNextDirection(
                        startingCoordinates.add(direction.coordinates),
                        startingCoordinates
                    ) != null
                ) direction else null
            })
        }
        return validStartingDirections[checkedStartingDirections]
    }


    private fun getSymbolAtCoordinates(coordinates: Coordinates): Char {
        return matrix[coordinates.y][coordinates.x]
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

                val direction =
                    symbolToDirection(
                        getSymbolAtCoordinates(currentLocation),
                        currentLocation,
                        previousLocation
                    )
                if (direction == null) {
                    traversal = Array(matrix.size) { arrayOfNulls(matrix[0].size) }
                    break
                } else {
                    traversal[currentLocation.y][currentLocation.x] = matrix[currentLocation.y][currentLocation.x]
                }
                currentLocation = currentLocation.add(direction.coordinates)

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
