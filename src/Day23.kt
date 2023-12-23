import helpers.Coordinates
import helpers.MovementDirection
import java.util.*

class LongWalk(input: List<String>) {
    
    private val matrix: List<List<Char>> = input.map { row ->
        row.map { it }
    }
    val startCoordinates = Coordinates(input[0].indexOfFirst { it == '.' }, 0)

    fun findMinHeatCost(initialStates: List<StrollState>): Int {
        val dijkstraTravelCost = mutableMapOf<StrollState, Int>().withDefault { 0 }
        val toVisit = PriorityQueue<StateToCost>()

        for (state in initialStates) {
            dijkstraTravelCost[state] = 1
            toVisit.add(StateToCost(state, 1))
        }

        var current: StateToCost? = null
        while (toVisit.isNotEmpty()) {
            current = toVisit.poll()
            current.state.next(matrix, current.state.currentLocation)
                .filter { it.currentLocation.y in matrix.indices && it.currentLocation.x in matrix[0].indices }
                .forEach { next ->
                    val newCost = current.cost + 1
                    if (newCost > dijkstraTravelCost.getValue(next)) {
                        dijkstraTravelCost[next] = newCost
                        toVisit.add(StateToCost(next, newCost))
                    }
                }
        }
        return current?.cost ?: 0
    }

    data class StateToCost(val state: StrollState, val cost: Int) : Comparable<StateToCost> {
        override fun compareTo(other: StateToCost): Int {
            return cost compareTo other.cost
        }
    }
    data class StrollState(val currentLocation: Coordinates, val comingFrom: MovementDirection) {
        fun next(matrix: List<List<Char>>, symbolPosition: Coordinates) = buildList {

            MovementDirection.entries.mapNotNull {
                if (it == comingFrom) return@mapNotNull null
                else if (matrix[symbolPosition.y][symbolPosition.x] == '>' && it != MovementDirection.RIGHT) return@mapNotNull null
                else if (matrix[symbolPosition.y][symbolPosition.x] == '<' && it != MovementDirection.LEFT) return@mapNotNull null
                else if (matrix[symbolPosition.y][symbolPosition.x] == 'v' && it != MovementDirection.DOWN) return@mapNotNull null
                else if (matrix[symbolPosition.y][symbolPosition.x] == '^' && it != MovementDirection.UP) return@mapNotNull null
                else {
                    val next = symbolPosition.add(it.coordinates)
                    if (next.y in matrix.indices && next.x in matrix[0].indices && matrix[next.y][next.x] != '#') return@mapNotNull it
                    else null
                }
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
}

fun main() {
    fun part1(input: List<String>): Int {
        val matrix = LongWalk(input)

        return matrix.findMinHeatCost(listOf(
            LongWalk.StrollState(Coordinates(matrix.startCoordinates.x, matrix.startCoordinates.y + 1), MovementDirection.UP)
        ))
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day23")
    println(part1(testInput))
}