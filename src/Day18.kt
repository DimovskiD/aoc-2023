import helpers.Coordinates
import helpers.MovementDirection

data class DiggingStep(val direction: MovementDirection, val numberOfBlocks: Int, val color: String)

fun main() {

    fun hexToStep(hex: String): DiggingStep {
        val hexNumberOfBlocks = hex.substring(2, hex.length - 2)
        hexNumberOfBlocks.println()

        val direction = when (hex.takeLast(2).first()) {
            '0' -> MovementDirection.RIGHT
            '1' -> MovementDirection.DOWN
            '2' -> MovementDirection.LEFT
            '3' -> MovementDirection.UP
            else -> throw Exception("invalid direction!")
        }
        return DiggingStep(direction, hexNumberOfBlocks.toInt(16), hex)
    }

    fun perimeter(steps: List<DiggingStep>) = steps.sumOf { it.numberOfBlocks }

    fun mapCoordinates(steps: List<DiggingStep>): List<Coordinates> {
        var currentPosition = Coordinates(0, 0)
        return steps.map { diggingStep ->
            currentPosition = currentPosition.add(
                Coordinates(
                    diggingStep.direction.coordinates.x * diggingStep.numberOfBlocks,
                    diggingStep.direction.coordinates.y * diggingStep.numberOfBlocks
                )
            )
            currentPosition
        }
    }

    fun part1(input: List<String>): Int {
        val steps = input.map {
            val split = it.split(" ")
            DiggingStep(MovementDirection.directionFromLetter(split[0][0]), split[1].toInt(), split[2])
        }
        val coordinates = mapCoordinates(steps)
        return (shoelaceArea(coordinates) + (perimeter(steps) / 2) + 1).toInt()
    }

    fun part2(input: List<String>): Long {
        val steps = input.map {
            val split = it.split(" ")
            hexToStep(split[2])
        }
        val coordinates = mapCoordinates(steps)

        return (shoelaceArea(coordinates).toLong() + perimeter(steps) / 2 + 1)
    }

    val testInput = readInput("day18")
    println(part2(testInput))
}
