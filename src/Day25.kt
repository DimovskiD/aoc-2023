import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() {
    fun part1(input: List<String>): Int {
        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        input.map { line ->
            val split = line.split(":")
            val key = split[0].trim()
            graph.addVertex(key)
            val values = split[1].trim()
            values.split(" ").forEach {
                graph.addVertex(it)
                graph.addEdge(key, it)
            }
        }
        val firstSide = StoerWagnerMinimumCut(graph).minCut()
        val secondSide = graph.vertexSet() - firstSide

        return firstSide.size * secondSide.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day25")
    println(part1(testInput))
}