import helpers.Coordinates
import helpers.MovementDirection
import helpers.MovementMatrix

data class DiggingStep(val direction: MovementDirection, val numberOfBlocks: Int, val color: String)

fun main() {

    fun getCornerSymbol(prevDirection: MovementDirection?, direction: MovementDirection): Char =
        if (prevDirection == MovementDirection.RIGHT && direction == MovementDirection.DOWN) 'F'
        else if (prevDirection == MovementDirection.RIGHT && direction == MovementDirection.UP) 'L'
        else if (prevDirection == MovementDirection.LEFT && direction == MovementDirection.UP) 'J'
        else if (prevDirection == MovementDirection.LEFT && direction == MovementDirection.DOWN) '7'
        else '#'


    fun part1(input: List<String>): Int {
        val steps = input.map {
            val split = it.split(" ")
            DiggingStep(MovementDirection.directionFromLetter(split[0][0]), split[1].toInt(), split[2])
        }

        val matrix = mutableListOf(mutableListOf('#'))
        var startingPoint = Coordinates(0, 0)
        var prevDirection: MovementDirection? = null
        steps.forEach {
            var addedBlocks = 0
            when (it.direction) {
                MovementDirection.RIGHT -> {
                    var addedCols = 0
                    for (x in startingPoint.x..startingPoint.x + it.numberOfBlocks) {
                        if (x >= matrix[startingPoint.y].size) {
                            addedCols++
                            matrix.forEachIndexed { index, row ->
                                if (index == startingPoint.y) {
                                    matrix[index].add('#')
                                }
                                else
                                    matrix[index].add('.')
                            }
                        } else matrix[startingPoint.y][x + addedCols] = '#'
                        addedBlocks++
                    }
                    prevDirection = MovementDirection.RIGHT
                    startingPoint = Coordinates(startingPoint.x + it.numberOfBlocks, startingPoint.y)
                }
                MovementDirection.DOWN -> {
                    for (y in startingPoint.y..startingPoint.y + it.numberOfBlocks) {
                        if (y >= matrix.size) {
                            matrix.add(MutableList(matrix[startingPoint.y].size) { '.' })
                            matrix[y][startingPoint.x] = '#'
                        } else {
                            matrix[y][startingPoint.x] = '#'
                        }
                    }
                    startingPoint = Coordinates(startingPoint.x, startingPoint.y + it.numberOfBlocks)
                }
                MovementDirection.LEFT -> {
                    for (x in startingPoint.x - it.numberOfBlocks..startingPoint.x) {
                        if (x < 0) {
                            matrix.forEachIndexed() { index, row ->
                                if (index == startingPoint.y) row.add(0, '#')
                                else row.add(0, '.')
                            }
                        } else {
                            matrix[startingPoint.y][x] = '#'
                        }
                    }
                    startingPoint = Coordinates(
                        if (startingPoint.x - it.numberOfBlocks < 0) 0 else startingPoint.x - it.numberOfBlocks,
                        startingPoint.y
                    )

                }
                MovementDirection.UP -> {
                    var addedRows = 0
                    for (y in startingPoint.y - it.numberOfBlocks until startingPoint.y) {
                        if (y < 0) {
                            matrix.add(0, MutableList(matrix[0].size) { '.' })
                            addedRows++
                            matrix[0][startingPoint.x] = '#'
                        } else {
                            matrix[y + addedRows][startingPoint.x] = '#'
                        }
                    }
                    startingPoint = Coordinates(
                        startingPoint.x,
                        if (startingPoint.y - it.numberOfBlocks < 0) 0 else startingPoint.y - it.numberOfBlocks
                    )
                }
            }
        }

        matrix.mapIndexed() { rowIndex, row ->
            var inRange = false
            var prev: MovementDirection? = null
            var count = 0

            row.forEachIndexed { index, char ->

                if (inRange && char != '#') {
                    print("$ ")
                } else
                if (char == '#') print("# ")
                else print(". ")

            }
            println()
            count
        }
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day18")
    println(part1(testInput))
}