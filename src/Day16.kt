import helpers.Coordinates
import helpers.MazeDirection
import helpers.MovementMatrix

class MirrorMaze(schema: List<String>) : MovementMatrix(schema) {

    fun getTraversalMatrix(
        traversalMatrix: Array<Array<Char?>>? = null,
        currentLocation: Coordinates,
        previousLocation: Coordinates,
        map: HashMap<Coordinates, MutableList<Coordinates>>,
    ): Array<Array<Char?>> {
        val traversal: Array<Array<Char?>> = traversalMatrix ?: Array(matrix.size) { arrayOfNulls(matrix[0].size) }
        traversal[previousLocation.y][previousLocation.x] = '#'

        val directionOfMovement = getDirectionOfMovement(previousLocation, currentLocation) ?: return traversal
        val directions = symbolToDirections(currentLocation, directionOfMovement)
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
                getTraversalMatrix(traversal, nextLocation, currentLocation, map)
            }
        }
        return traversal
    }

    override fun symbolToDirections(symbolPosition: Coordinates, comingFrom: MazeDirection): List<MazeDirection> {
        return when (getSymbolAtCoordinates(symbolPosition)) {
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
                MazeDirection.RIGHT -> listOf(MazeDirection.LEFT)
                MazeDirection.UP, MazeDirection.DOWN -> listOf(MazeDirection.LEFT, MazeDirection.RIGHT)
            }

            '\\' -> listOf(
                when (comingFrom) {
                    MazeDirection.LEFT -> MazeDirection.DOWN
                    MazeDirection.RIGHT -> MazeDirection.UP
                    MazeDirection.UP -> MazeDirection.RIGHT
                    MazeDirection.DOWN -> MazeDirection.LEFT
                }
            )

            '/' -> listOf(
                when (comingFrom) {
                    MazeDirection.LEFT -> MazeDirection.UP
                    MazeDirection.RIGHT -> MazeDirection.DOWN
                    MazeDirection.UP -> MazeDirection.LEFT
                    MazeDirection.DOWN -> MazeDirection.RIGHT
                }
            )

            else -> listOf(
                when (comingFrom) {
                    MazeDirection.RIGHT -> MazeDirection.LEFT
                    MazeDirection.UP -> MazeDirection.DOWN
                    MazeDirection.DOWN -> MazeDirection.UP
                    MazeDirection.LEFT -> MazeDirection.RIGHT
                }
            )
        }
    }
}

    fun main() {

        fun calculateSum(maze: MirrorMaze, previousLocation: Coordinates, direction: MazeDirection): Int {
            val map = hashMapOf<Coordinates, MutableList<Coordinates>>()

            val directions = maze.symbolToDirections(previousLocation, direction)
            val totalSum = directions.sumOf {
                val currentLocation = previousLocation.add(it.coordinates)
                val traversal = maze.getTraversalMatrix(null, currentLocation, previousLocation, map)
                val sum = traversal.sumOf { row ->
                    row.count { char -> char == '#' }
                }
                sum
            }
            return totalSum
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
    }
