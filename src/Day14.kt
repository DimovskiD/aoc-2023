class RockMatrix(input: List<String>) {

    private val matrix = input.map { row ->
        row.map {
            it
        }.toMutableList()
    }

    fun moveRocks(): List<List<Char>> {
        val reversed = matrix.reversed()
        val newMatrix = reversed.toMutableList()
        reversed.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, char ->
                if (newMatrix[rowIndex][colIndex] == 'O') {
                    moveRock(newMatrix, rowIndex, colIndex)
                }

            }
        }
        return newMatrix.reversed()
    }

    private fun moveRock(
        newMatrix: MutableList<MutableList<Char>>,
        rowIndex: Int,
        colIndex: Int,
    ): MutableList<MutableList<Char>> {
        var matrix = newMatrix
        if (rowIndex + 1 < newMatrix.size) {
            if (matrix[rowIndex + 1][colIndex] == 'O') {
                matrix = moveRock(matrix, rowIndex + 1, colIndex)
            }
            if (matrix[rowIndex + 1][colIndex] == '.') {
                matrix[rowIndex][colIndex] = '.'
                matrix[rowIndex + 1][colIndex] = 'O'
            }
        }
        return matrix
    }

    override fun toString(): String {
        return buildString {
            matrix.forEach { row ->
                row.forEach {
                    append("$it ")
                }
                append("\n")
            }
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val matrix = RockMatrix(input)
        val moved = matrix.moveRocks()
        buildString {
            moved.forEach { row ->
                row.forEach {
                    append("$it ")
                }
                append("\n")
            }
        }.println()
        return moved.mapIndexed { index, row ->
            row.sumOf { if (it == 'O') moved.size - index else 0 }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day14")
    println(part1(testInput))

}
