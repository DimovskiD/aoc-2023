package helpers

data class Coordinates(val x: Int, val y: Int) {

    fun add(coordinates: Coordinates): Coordinates {
        return Coordinates(x + coordinates.x, y + coordinates.y)
    }

    fun isValid(maxX: Int, maxY: Int): Boolean {
        return x > -1 && x < maxX && y > -1 &&  y < maxY
    }
}

enum class MovementDirection(val coordinates: Coordinates) {
    UP(Coordinates(0, -1)), DOWN(Coordinates(0, 1)), LEFT(Coordinates(-1, 0)), RIGHT(Coordinates(1, 0));

    fun opposite(): MovementDirection = when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP -> DOWN
            DOWN -> UP
        }

    companion object {
        fun directionFromLetter(char: Char): MovementDirection {
            return when (char) {
                'R' -> RIGHT
                'U' -> UP
                'D' -> DOWN
                else -> LEFT
            }
        }
     }
}
abstract class MovementMatrix(schema: List<String>) {

    val matrix: Array<Array<Char>>

    init {
        matrix = Array(schema.size) { row ->
            Array(schema[row].length) { column ->
                schema[row][column]
            }
        }
    }

    open fun isCoordinateValid(coordinates: Coordinates): Boolean {
        return coordinates.y in matrix.indices && coordinates.x in matrix[0].indices
    }

    abstract fun symbolToDirections(
        symbolPosition: Coordinates,
        comingFrom: MovementDirection? = null
    ): List<MovementDirection>?

    protected fun getDirectionOfMovement(previousLocation: Coordinates, currentLocation: Coordinates): MovementDirection? {
        val direction = MovementDirection.entries.find {
            currentLocation.add(it.coordinates) == previousLocation
        }
        return direction
    }
    protected fun getNextDirection(currentLocation: Coordinates, previousLocation: Coordinates): MovementDirection? {
        if (!currentLocation.isValid(matrix[0].size, matrix.size)) return null
        val directionOfMovement = getDirectionOfMovement(previousLocation, currentLocation) ?: return null
        return symbolToDirections(currentLocation, directionOfMovement)?.getOrNull(0)
    }

    protected fun getSymbolAtCoordinates(coordinates: Coordinates): Char {
        return matrix[coordinates.y][coordinates.x]
    }

    override fun toString(): String {
        return buildString {
            matrix.forEach { row ->
                row.forEach { char ->
                    append("$char ")
                }
                append("\n")
            }
        }
    }
}