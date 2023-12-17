import helpers.Coordinates
import helpers.MovementDirection
import java.util.PriorityQueue

data class StateToCost(val state: HeatMap.State, val cost: Int) : Comparable<StateToCost> {
    override fun compareTo(other: StateToCost): Int {
        return cost compareTo other.cost
    }
}

class HeatMap(input: List<String>) {

    private val matrix = MutableList(input.size) { row ->
        MutableList(input[row].length) { column ->
            input[row][column].digitToInt()
        }
    }

    fun findMinHeatCost(initialStates: List<State>, minBlocks: Int, maxBlocks: Int): Int {
        val dijkstraTravelCost = mutableMapOf<State, Int>().withDefault { Int.MAX_VALUE }
        val toVisit = PriorityQueue<StateToCost>()

        for (state in initialStates) {
            dijkstraTravelCost[state] = 0
            toVisit.add(StateToCost(state, 0))
        }

        val end = Coordinates(matrix[0].size - 1, matrix.size - 1)
        while (toVisit.isNotEmpty()) {
            val current = toVisit.poll()
            if (current.state.currentLocation == end && current.state.blocks >= minBlocks) {
                return current.cost
            }

            current.state.next(minBlocks, maxBlocks)
                .filter { it.currentLocation.y in matrix.indices && it.currentLocation.x in matrix[0].indices }
                .forEach { next ->
                    val newCost = current.cost + matrix[next.currentLocation.y][next.currentLocation.x]
                    if (newCost < dijkstraTravelCost.getValue(next)) {
                        dijkstraTravelCost[next] = newCost
                        toVisit.add(StateToCost(next, newCost))
                    }
                }
        }
        return 0
    }

    data class State(val currentLocation: Coordinates, val directionCoordinates: Coordinates, val blocks: Int = 0) {
        fun next(minBlocks: Int, maxBlocks: Int) = buildList {
            if (blocks < minBlocks) {
                add(
                    copy(
                        currentLocation = currentLocation.add(directionCoordinates),
                        directionCoordinates = directionCoordinates,
                        blocks = blocks + 1
                    )
                )
            } else {
                val turnLeft = Coordinates(x = directionCoordinates.y, y = directionCoordinates.x)
                val turnRight = Coordinates(x = -directionCoordinates.y, y = -directionCoordinates.x)

                add(
                    copy(
                        currentLocation = currentLocation.add(turnLeft),
                        directionCoordinates = turnLeft,
                        blocks = 1
                    )
                )
                add(
                    copy(
                        currentLocation = currentLocation.add(turnRight),
                        directionCoordinates = turnRight,
                        blocks = 1
                    )
                )

                if (blocks < maxBlocks) {
                    add(
                        copy(
                            currentLocation = currentLocation.add(directionCoordinates),
                            directionCoordinates = directionCoordinates,
                            blocks = blocks + 1
                        )
                    )
                }
            }
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val heatMap = HeatMap(input)
        return heatMap.findMinHeatCost(
            listOf(
                HeatMap.State(
                    Coordinates(0, 0),
                    MovementDirection.RIGHT.coordinates,
                ),
                HeatMap.State(
                    Coordinates(0, 0),
                    MovementDirection.DOWN.coordinates,
                )
            ), 0, 3
        )
    }

    fun part2(input: List<String>): Int {
        val heatMap = HeatMap(input)
        return heatMap.findMinHeatCost(
            listOf(
                HeatMap.State(
                    Coordinates(0, 0),
                    MovementDirection.RIGHT.coordinates,
                ),
                HeatMap.State(
                    Coordinates(0, 0),
                    MovementDirection.DOWN.coordinates,
                )
            ), 4, 10
        )
    }

    val testInput = readInput("day17")
    println(part2(testInput))
}