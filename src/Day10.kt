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
    lateinit var startingCoordinates: Coordinates
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

    fun getNextValidStartingDirection(): MazeDirection {
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


    fun getSymbolAtCoordinates(coordinates: Coordinates): Char {
        return matrix[coordinates.y][coordinates.x]
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val maze = Maze(input)

        var count = 1
        var found = false

        while (!found) {
            var previousLocation = maze.startingCoordinates
            val initialDirection = maze.getNextValidStartingDirection()
            var currentLocation = maze.startingCoordinates.add(initialDirection.coordinates)

            println("PREVIOUS_LOCATION ${maze.getSymbolAtCoordinates(previousLocation)} ${previousLocation}")
            println("INITIAL_DIRECTION $initialDirection ${initialDirection.coordinates}")
            println("INITIAL_LOCATION ${maze.getSymbolAtCoordinates(currentLocation)} ${currentLocation}")

            while (maze.getSymbolAtCoordinates(coordinates = currentLocation) != 'S') {
                val tmpCurrent = currentLocation

                println("CURRENT_LOCATION ${maze.getSymbolAtCoordinates(currentLocation)}")
                val direction =
                    symbolToDirection(
                        maze.getSymbolAtCoordinates(currentLocation),
                        currentLocation,
                        previousLocation
                    ) ?: break
                println("GO_TO_DIRECTION $direction")
                currentLocation = currentLocation.add(direction.coordinates)
                println("NEXT_LOCATION $currentLocation")

                previousLocation = tmpCurrent
                count++
            }
            if (maze.getSymbolAtCoordinates(coordinates = currentLocation) == 'S') {
                found = true
            } else {
                count = 0
            }
        }
        return count / 2
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    val testInput = readInput("day10")

    println(part1(testInput))
}
