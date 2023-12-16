import helpers.Coordinates
import helpers.MovementDirection
import helpers.MovementMatrix

class MirrorLayout(schema: List<String>) : MovementMatrix(schema) {

    private fun getTraversalMatrix(
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

    override fun symbolToDirections(
        symbolPosition: Coordinates,
        comingFrom: MovementDirection
    ): List<MovementDirection> {
        return when (getSymbolAtCoordinates(symbolPosition)) {
            '|' -> when (comingFrom) {
                MovementDirection.UP -> listOf(MovementDirection.DOWN)
                MovementDirection.DOWN -> listOf(MovementDirection.UP)
                MovementDirection.LEFT, MovementDirection.RIGHT -> listOf(
                    MovementDirection.UP,
                    MovementDirection.DOWN
                )
            }
            '-' -> when (comingFrom) {
                MovementDirection.LEFT -> listOf(MovementDirection.RIGHT)
                MovementDirection.RIGHT -> listOf(MovementDirection.LEFT)
                MovementDirection.UP, MovementDirection.DOWN -> listOf(MovementDirection.LEFT, MovementDirection.RIGHT)
            }
            '\\' -> listOf(
                when (comingFrom) {
                    MovementDirection.LEFT -> MovementDirection.DOWN
                    MovementDirection.RIGHT -> MovementDirection.UP
                    MovementDirection.UP -> MovementDirection.RIGHT
                    MovementDirection.DOWN -> MovementDirection.LEFT
                }
            )
            '/' -> listOf(
                when (comingFrom) {
                    MovementDirection.LEFT -> MovementDirection.UP
                    MovementDirection.RIGHT -> MovementDirection.DOWN
                    MovementDirection.UP -> MovementDirection.LEFT
                    MovementDirection.DOWN -> MovementDirection.RIGHT
                }
            )
            else -> listOf(
                when (comingFrom) {
                    MovementDirection.RIGHT -> MovementDirection.LEFT
                    MovementDirection.UP -> MovementDirection.DOWN
                    MovementDirection.DOWN -> MovementDirection.UP
                    MovementDirection.LEFT -> MovementDirection.RIGHT
                }
            )
        }
    }

    fun calculateSum(previousLocation: Coordinates, direction: MovementDirection): Int {
        val map = hashMapOf<Coordinates, MutableList<Coordinates>>()

        val directions = symbolToDirections(previousLocation, direction)
        val totalSum = directions.sumOf {
            val currentLocation = previousLocation.add(it.coordinates)
            val traversal = getTraversalMatrix(null, currentLocation, previousLocation, map)
            val sum = traversal.sumOf { row ->
                row.count { char -> char == '#' }
            }
            sum
        }
        return totalSum
    }

}

fun main() {

    fun part1(input: List<String>): Int {
        val mirrorLayout = MirrorLayout(input)
        val previousLocation = Coordinates(3, 0)
        return mirrorLayout.calculateSum(previousLocation, MovementDirection.UP)
    }

    fun part2(input: List<String>): Int {
        val mirrorLayout = MirrorLayout(input)
        val maxRows = mirrorLayout.matrix.indices.maxOf { index ->
            val previousLocationLeft = Coordinates(0, index)
            val sumLeft = mirrorLayout.calculateSum(previousLocationLeft, MovementDirection.LEFT)
            val previousLocationRight = Coordinates(mirrorLayout.matrix[0].size - 1, index)
            val sumRight = mirrorLayout.calculateSum(previousLocationRight, MovementDirection.RIGHT)

            maxOf(sumLeft, sumRight)
        }
        val maxColumns = mirrorLayout.matrix[0].indices.maxOf { index ->

            val previousLocationTop = Coordinates(index, 0)
            val sumTop = mirrorLayout.calculateSum(previousLocationTop, MovementDirection.UP)
            val previousLocationBottom = Coordinates(index, mirrorLayout.matrix.size - 1)
            val sumBottom = mirrorLayout.calculateSum(previousLocationBottom, MovementDirection.DOWN)

            maxOf(sumTop, sumBottom)
        }
        return maxOf(maxColumns, maxRows)
    }

    val testInput = readInput("day16")
    println(part2(testInput))
}
