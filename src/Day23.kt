import helpers.Coordinates
import helpers.MovementDirection
import java.util.*

class LongWalk(input: List<String>) {

    private val matrix: List<List<Char>> = input.map { row ->
        row.map { it }
    }
    private val visitedMatrix: List<MutableList<Boolean>> = matrix.map { row ->
        row.map { false }.toMutableList()
    }
    val startCoordinates = Coordinates(input[0].indexOfFirst { it == '.' }, 0)

    fun findMaxDistance(initialStates: List<StrollState>, useSlopes: Boolean = true): Int {
        val travelCost = mutableMapOf<StrollState, Int>().withDefault { 0 }

        val toVisit = PriorityQueue<StateToCost>()

        for (state in initialStates) {
            travelCost[state] = 1
            toVisit.add(StateToCost(state, 1, mutableListOf()))
        }

        val end = Coordinates(matrix.last().lastIndexOf('.'), matrix.size - 1)

        var current: StateToCost?
        val listOfLengths = mutableListOf<Int>()
        while (toVisit.isNotEmpty()) {
            current = toVisit.poll()
            current.visited.size.println()
            current.state.next(matrix, current.state.currentLocation)
                .filter { it.currentLocation.y in matrix.indices && it.currentLocation.x in matrix[0].indices }
                .forEach { next ->
                    if (next.currentLocation == end) {
                        listOfLengths += current.cost + 1
                    }
                    val newCost = current.cost + 1
                    if (newCost >= travelCost.getValue(next)) {
                        travelCost[next] = newCost
                        val curTmp = current.visited.toMutableList()
                        if (!curTmp.contains(next.currentLocation)) {
                            curTmp.add(next.currentLocation)
                            toVisit.add(StateToCost(next, newCost, curTmp))
                        }
                    }
                }
        }
        return listOfLengths.maxOf { it }
    }

    data class StateToCost(val state: StrollState, val cost: Int, val visited: MutableList<Coordinates>) :
        Comparable<StateToCost> {

        override fun compareTo(other: StateToCost): Int {
            return cost compareTo other.cost
        }
    }

    data class StrollState(
        val currentLocation: Coordinates,
        val comingFrom: MovementDirection,
        val useSlopes: Boolean = true
    ) {
        fun next(matrix: List<List<Char>>, symbolPosition: Coordinates) = buildList {

            MovementDirection.entries.mapNotNull {
                if (it == comingFrom) return@mapNotNull null
                else if (useSlopes) {
                    if (matrix[symbolPosition.y][symbolPosition.x] == '>' && it != MovementDirection.RIGHT) return@mapNotNull null
                    else if (matrix[symbolPosition.y][symbolPosition.x] == '<' && it != MovementDirection.LEFT) return@mapNotNull null
                    else if (matrix[symbolPosition.y][symbolPosition.x] == 'v' && it != MovementDirection.DOWN) return@mapNotNull null
                    else if (matrix[symbolPosition.y][symbolPosition.x] == '^' && it != MovementDirection.UP) return@mapNotNull null
                }
                val next = symbolPosition.add(it.coordinates)
                if (next.y in matrix.indices && next.x in matrix[0].indices && matrix[next.y][next.x] != '#') return@mapNotNull it
                else null

            }.forEach {
                add(
                    copy(
                        currentLocation = currentLocation.add(it.coordinates),
                        comingFrom = it.opposite()
                    )
                )
            }
        }
    }

    //initially used a dirty brute force
    //TODO convert to graph implementation + DFS
    fun findLongestPath(coordinates: Coordinates, distance: Int, comingFrom: MovementDirection) {
        visitedMatrix[coordinates.y][coordinates.x] = true

        val end = Coordinates(matrix.last().lastIndexOf('.'), matrix.size - 1)
        if (coordinates == end) {
            println(distance)
            if (distance > longestPath)
                longestPath = distance
            visitedMatrix[coordinates.y][coordinates.x] = false
            return
        }
        MovementDirection.entries.forEach {
            if (comingFrom != it) {
                val next = coordinates.add(it.coordinates)
                if (next != coordinates) {
                    if (next.y in matrix.indices
                        && next.x in matrix[0].indices
                        && !visitedMatrix[next.y][next.x] && matrix[next.y][next.x] != '#'
                    ) findLongestPath(next, distance + 1, it.opposite())
                }
            }
        }
        visitedMatrix[coordinates.y][coordinates.x] = false
    }
}

var longestPath = 0

fun main() {
    fun part1(input: List<String>): Int {
        val matrix = LongWalk(input)

        return matrix.findMaxDistance(
            listOf(
                LongWalk.StrollState(
                    Coordinates(matrix.startCoordinates.x, matrix.startCoordinates.y + 1),
                    MovementDirection.UP
                )
            )
        )
    }

    fun part2(input: List<String>): Int {
        val matrix = LongWalk(input)
        matrix.findLongestPath(
            Coordinates(
                x = matrix.startCoordinates.x,
                y = matrix.startCoordinates.y + 1
            ),
            distance = 1,
            comingFrom = MovementDirection.UP
        )
        return longestPath
    }

    val testInput = readInput("day23")
    println(part2(testInput))
}