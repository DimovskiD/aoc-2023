enum class Direction(val value: Char) { LEFT('L'), RIGHT('R') }
data class Vertex<T>(val index: Int, val data: T)
data class Edge<T>(val source: Vertex<T>, val destination: Vertex<T>, val direction: Direction)
class Graph(input: List<String>) {
    private val adjacencyMap = mutableMapOf<Vertex<String>, ArrayList<Edge<String>>>()
    private val directionsList = input[0].map { if (it == Direction.LEFT.value) Direction.LEFT else Direction.RIGHT }

    init {
        input.subList(2, input.size).forEach {
            val vertexInput = it.substringBefore("=").trim()
            val vertex = createVertex(vertexInput)
            val values = it.substringAfter("=").replace("(", "").replace(")", "").trim()
            val leftValue = values.substringBefore(",").trim()
            val rightValue = values.substringAfter(",").trim()
            addDirectedEdge(vertex, createVertex(leftValue), Direction.LEFT)
            addDirectedEdge(vertex, createVertex(rightValue), Direction.RIGHT)
        }
    }

    private fun createVertex(data: String): Vertex<String> {
        val index = adjacencyMap.keys.find { it.data == data }
        if (index != null) return index
        val vertex = Vertex(adjacencyMap.count(), data)
        adjacencyMap[vertex] = arrayListOf()
        return vertex
    }

    private fun addDirectedEdge(source: Vertex<String>, destination: Vertex<String>, direction: Direction) {
        val edge = Edge(source, destination, direction)
        adjacencyMap[source]?.add(edge)
    }

    override fun toString(): String {
        return buildString {
            adjacencyMap.forEach { (vertex, edges) ->
                val edgeString = edges.joinToString { it.destination.data }
                append("${vertex.data} -> [$edgeString]\n")
            }
        }
    }

    private fun go(start: Vertex<String>?, direction: Direction): Vertex<String>? {
        val vertex = adjacencyMap[start]!!
        return vertex.find { it.direction == direction }?.destination
    }

    fun getStartVertices(condition: (String) -> Boolean): List<Vertex<String>> =
        adjacencyMap.keys.filter { condition(it.data) }

    fun getStepsForVertex(vertex: Vertex<String>?, condition: (String) -> Boolean): Long {
        var previousVertex: Vertex<String>? = vertex
        var steps = 0L
        while (previousVertex == null || !condition(previousVertex.data)) {
            previousVertex = go(previousVertex, directionsList[(steps % directionsList.size).toInt()])
            steps++
        }
        return steps
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val graph = Graph(input)
        return graph.getStepsForVertex(graph.getStartVertices { it == "AAA" }[0]) { it == "ZZZ" }.toInt()
    }

    fun part2(input: List<String>): Long {
        val graph = Graph(input)

        val lst = graph.getStartVertices { it.endsWith("A") }.map { vertex ->
            graph.getStepsForVertex(vertex, condition = {
                it.endsWith("Z")
            })
        }
        return lst.reduce { acc, i -> lcm(acc, i) }
    }

    val testInput = readInput("day08")
    println(part2(testInput))

}
