import RollDirection.Companion.next

enum class RollDirection(val x: Int, val y: Int, val shouldReverse: Boolean) {
    UP(0, 1, true), DOWN(0, 1, false), LEFT(1, 0, true), RIGHT(1, 0, false);

    companion object {
        fun next(current: RollDirection): RollDirection {
            return when (current) {
                UP -> LEFT
                LEFT -> DOWN
                DOWN -> RIGHT
                RIGHT -> UP
            }
        }
    }
}

object Sisyphus {

    fun moveRocks(matrix: MutableList<MutableList<Char>>, direction: RollDirection): MutableList<MutableList<Char>> {

        val reverseAction: (MutableList<MutableList<Char>>) -> MutableList<MutableList<Char>> =
            when {
                !direction.shouldReverse -> { { it } }
                direction.y > 0 -> { { it.asReversed() } }
                else -> { { it.reversedColumns() } }
            }

        val reversed = reverseAction(matrix)

        val newMatrix = reversed.toMutableList()
        reversed.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                if (newMatrix[rowIndex][colIndex] == 'O') {
                    moveRock(
                        newMatrix,
                        rowIndex,
                        colIndex,
                        direction,
                    )
                }
            }
        }
        return reverseAction(newMatrix)
    }

    private fun moveRock(
        newMatrix: MutableList<MutableList<Char>>,
        rowIndex: Int,
        colIndex: Int,
        direction: RollDirection
    ): MutableList<MutableList<Char>> {
        var matrix = newMatrix
        if (rowIndex + direction.y < newMatrix.size && rowIndex + direction.y > -1 &&
            colIndex + direction.x < newMatrix[0].size && colIndex + direction.x > -1
        ) {
            if (matrix[rowIndex + direction.y][colIndex + direction.x] == 'O') {
                matrix = moveRock(matrix, rowIndex + direction.y, colIndex + direction.x, direction)
            }
            if (matrix[rowIndex + direction.y][colIndex + direction.x] == '.') {
                matrix[rowIndex][colIndex] = '.'
                matrix[rowIndex + direction.y][colIndex + direction.x] = 'O'
            }
        }
        return matrix
    }

    fun spinCycle(matrix: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> {
        var direction: RollDirection? = null
        var newMatrix: MutableList<MutableList<Char>> = matrix
        while (direction != RollDirection.UP) {
            if (direction == null) direction = RollDirection.UP
            newMatrix = moveRocks(newMatrix, direction)
            direction = next(direction)
        }
        return newMatrix
    }
}

fun main() {

    fun calculate(matrix: MutableList<MutableList<Char>>) = matrix.mapIndexed { index, row ->
        row.sumOf { if (it == 'O') matrix.size - index else 0 }
    }.sum()

    fun part1(matrix: MutableList<MutableList<Char>>): Int {
        Sisyphus.moveRocks(matrix, RollDirection.UP)
        return calculate(matrix)
    }

    fun part2(matrix: MutableList<MutableList<Char>>): Int {
        var newMatrix = matrix

        val lst = mutableListOf<Int>()
        val reasonablyHighNumberToDeterminePattern = 3000

        repeat(reasonablyHighNumberToDeterminePattern) {
            newMatrix = Sisyphus.spinCycle(newMatrix)
            lst.add(calculate(newMatrix))
        }

        val sequence = longestRepeatingSubsequence(lst, lst.dropLast(1))
        return lst[1000000000 % sequence.size]
    }

    val testInput = readInput("day14")
    val matrix = testInput.map { row ->
        row.map {
            it
        }.toMutableList()
    }.toMutableList()

    println(part2(matrix))

}
