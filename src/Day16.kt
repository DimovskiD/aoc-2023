/*
import MazeDirection.Companion.symbolToDirection
import MazeDirection.Companion.comingFromBottom
import MazeDirection.Companion.comingFromLeft
import MazeDirection.Companion.comingFromRight
import MazeDirection.Companion.comingFromTop
import kotlin.math.max

fun symbolToDirections(
    symbol: Char,
    currentLocation: Coordinates,
    previousLocation: Coordinates,
): List<MazeDirection>? {

    return when (symbol) {
        '|' -> if (comingFromTop(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.DOWN) else if (comingFromBottom(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.UP) else if (comingFromLeft(currentLocation, previousLocation) || comingFromRight(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.UP, MazeDirection.DOWN) else null

        '-' -> if (comingFromLeft(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.RIGHT) else if (comingFromRight(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.LEFT) else if (comingFromTop(currentLocation, previousLocation) || comingFromBottom(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.LEFT, MazeDirection.RIGHT) else null

        '\\' ->
            if (comingFromLeft(
                    currentLocation,
                    previousLocation
                )
            ) listOf(MazeDirection.DOWN)
            else if (comingFromRight(currentLocation, previousLocation)) listOf(
                MazeDirection.UP
            ) else if (comingFromTop(
                    currentLocation,
                    previousLocation
                )
            ) listOf(MazeDirection.RIGHT) else if (comingFromBottom(
                    currentLocation,
                    previousLocation
                )
            ) listOf(MazeDirection.LEFT) else null

        '/' -> if (comingFromLeft(
                currentLocation,
                previousLocation
            )
        ) return listOf(MazeDirection.UP)
        else if (comingFromRight(currentLocation, previousLocation)) listOf(
            MazeDirection.DOWN
        ) else if (comingFromTop(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.LEFT) else if (comingFromBottom(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.RIGHT) else null

        else -> if (comingFromRight(currentLocation, previousLocation)) listOf(
            MazeDirection.LEFT
        ) else if (comingFromTop(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.DOWN) else if (comingFromBottom(
                currentLocation,
                previousLocation
            )
        ) listOf(MazeDirection.UP) else listOf(MazeDirection.RIGHT)
    }
}

fun symbolToDirectionsFrom(
    symbol: Char,
    comingFrom: MazeDirection,
): List<MazeDirection>? {

    return when (symbol) {
        '|' -> when (comingFrom) {
            MazeDirection.UP -> listOf(MazeDirection.DOWN)
            MazeDirection.DOWN -> listOf(MazeDirection.UP)
            MazeDirection.LEFT, MazeDirection.RIGHT -> listOf(
                MazeDirection.UP,
                MazeDirection.DOWN
            )
        }

        '-' -> when (comingFrom) {
            MazeDirection.LEFT -> listOf(MazeDirection.RIGHT)
            MazeDirection.RIGHT -> listOf(
                MazeDirection.LEFT
            )

            MazeDirection.UP, MazeDirection.DOWN -> listOf(MazeDirection.LEFT, MazeDirection.RIGHT)
        }

        '\\' ->
            when (comingFrom) {
                MazeDirection.LEFT -> listOf(MazeDirection.DOWN)
                MazeDirection.RIGHT -> listOf(
                    MazeDirection.UP
                )

                MazeDirection.UP -> listOf(MazeDirection.RIGHT)
                MazeDirection.DOWN -> listOf(MazeDirection.LEFT)
            }

        '/' -> when (comingFrom) {
            MazeDirection.LEFT -> return listOf(MazeDirection.UP)
            MazeDirection.RIGHT -> listOf(
                MazeDirection.DOWN
            )

            MazeDirection.UP -> listOf(MazeDirection.LEFT)
            MazeDirection.DOWN -> listOf(MazeDirection.RIGHT)
        }

        else -> when (comingFrom) {
            MazeDirection.RIGHT -> listOf(
                MazeDirection.LEFT
            )

            MazeDirection.UP -> listOf(MazeDirection.DOWN)
            MazeDirection.DOWN -> listOf(MazeDirection.UP)
            MazeDirection.LEFT -> listOf(MazeDirection.RIGHT)
        }
    }
}

class MirrorMaze(schema: List<String>) {

    val matrix: Array<Array<Char>>

    init {
        matrix = Array(schema.size) { row ->
            val length = schema[row].length
            val arr = Array(length) { column ->
                val char = schema[row][column]
                print(char)
                char
            }
            println(" ")
            arr
        }
    }

    private fun getSymbolAtCoordinates(coordinates: Coordinates): Char? {
        if (coordinates.y < 0 || coordinates.y > matrix.size - 1 || coordinates.x < 0 || coordinates.x > matrix[0].size - 1) return null
        return matrix[coordinates.y][coordinates.x]
    }

    fun getTraversalMatrix(
        trav: Array<Array<Char?>>? = null,
        currentLocation: Coordinates,
        previousLocation: Coordinates,
        map: HashMap<Coordinates, MutableList<Coordinates>>,
    ): Array<Array<Char?>> {
        var traversal: Array<Array<Char?>> = trav ?: Array(matrix.size) { arrayOfNulls(matrix[0].size) }
        traversal[previousLocation.y][previousLocation.x] = '#'
        val symbol = getSymbolAtCoordinates(currentLocation) ?: return traversal
        val directions = symbolToDirections(symbol, currentLocation, previousLocation) ?: return traversal
        traversal[currentLocation.y][currentLocation.x] = '#'
        directions.forEach {
            val nextLocation = currentLocation.add(it.coordinates)
            val visitedLocations = map[currentLocation] ?: mutableListOf()
            if (visitedLocations.contains(nextLocation)) return traversal
            else {
                visitedLocations.add(nextLocation)
                map[currentLocation] = visitedLocations
            }
            if (nextLocation.isValid(matrix[0].size, matrix.size)) {
                kotlin.io.println("GO $it")
                getTraversalMatrix(traversal, nextLocation, currentLocation, map)
            }
        }
        return traversal
    }
}

fun main() {

    fun calculateSum(maze: MirrorMaze, previousLocation: Coordinates, direction: MazeDirection): Int {
        val map = hashMapOf<Coordinates, MutableList<Coordinates>>()

        val directions = symbolToDirectionsFrom(maze.matrix[previousLocation.y][previousLocation.x], direction)
        val totalSum = directions?.sumOf {
            val currentLocation = previousLocation.add(it.coordinates)
            val trav = maze.getTraversalMatrix(null, currentLocation, previousLocation, map)
            val sum = trav.sumOf { row ->
                row.forEach { char ->
                    print("${char ?: '.'} ")
                }
                println()
                row.count { it == '#' }
            }
            sum
        }
        return totalSum ?: 0
    }

    fun part1(input: List<String>): Int {
        val maze = MirrorMaze(input)
        val previousLocation = Coordinates(3, 0)
        return calculateSum(maze, previousLocation, MazeDirection.UP)
    }

    fun part2(input: List<String>): Int {
        val maze = MirrorMaze(input)
        val maxRows = maze.matrix.indices.maxOf { index ->
            val previousLocationLeft = Coordinates(0, index)
            val sumLeft = calculateSum(maze, previousLocationLeft, MazeDirection.LEFT)
            val previousLocationRight = Coordinates(maze.matrix[0].size - 1, index)
            val sumRight = calculateSum(maze, previousLocationRight, MazeDirection.RIGHT)

            maxOf(sumLeft, sumRight)
        }
        val maxColumns = maze.matrix[0].indices.maxOf { index ->

            val previousLocationTop = Coordinates(index, 0)
            val sumTop = calculateSum(maze, previousLocationTop, MazeDirection.UP)
            val previousLocationBottom = Coordinates(index, maze.matrix.size - 1)
            val sumBottom = calculateSum(maze, previousLocationBottom, MazeDirection.DOWN)

            maxOf(sumTop, sumBottom)
        }
        return maxOf(maxColumns, maxRows)
    }

    val testInput = readInput("day16")
    println(part2(testInput))
}*/
