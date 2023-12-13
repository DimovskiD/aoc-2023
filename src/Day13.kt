data class AshRockMatrix<T>(val rows: List<List<T>>) {

    val columns: List<List<T>> = rows[0].indices.map { column ->
        rows.map { it[column] }.toMutableList()
    }

    private fun isReflection(first: List<List<T>>, second: List<List<T>>, lookingForSmudges: Boolean = false): Boolean {
        var totalDifferences = 0
        first.forEachIndexed { index, element ->
            if (!lookingForSmudges && element != second[second.size - 1 - index]) return false
            else if (lookingForSmudges) {
                element.forEachIndexed { charIndex, char ->
                    if (char != second[second.size - 1 - index][charIndex] && totalDifferences++ > 1) return false
                }
            }
        }
        return if (lookingForSmudges) totalDifferences == 1 else true
    }

    fun findSafePathCount(list: List<List<T>>, index: Int, count: Int, lookingForSmudges: Boolean = false): Int {
        if (index >= list.size) return findSafePathCount(list, 0, list.size / 2, lookingForSmudges)

        val first = list.subList(index, index + count)
        val second = list.subList(index + count, index + count * 2)

        return when {
            isReflection(first, second, lookingForSmudges) && (index == 0 || index + count * 2 == list.size) -> {
                index + first.size
            }

            index + count * 2 < list.size -> {
                findSafePathCount(list, index + 1, count, lookingForSmudges)
            }

            first.size > 1 && second.size > 1 -> findSafePathCount(list, 0, count - 1, lookingForSmudges)
            (index + 1 + count * 2) < list.size -> findSafePathCount(
                list,
                index + 1,
                list.size / 2,
                lookingForSmudges
            )

            else -> 0
        }
    }

    override fun toString(): String {
        return buildString {
            rows.forEach { row ->
                row.forEach {
                    append("$it ")
                }
                append("\n")
            }
        }
    }
}

fun main() {

    fun getSafePathCount(matrices: List<AshRockMatrix<Char>>, lookingForSmudges: Boolean) = matrices.sumOf {
        val rowMirror = it.findSafePathCount(it.rows, 0, it.rows.size / 2, lookingForSmudges = lookingForSmudges)
        val columMirror = it.findSafePathCount(it.columns, 0, it.columns.size / 2, lookingForSmudges = lookingForSmudges)
        if (rowMirror > columMirror) rowMirror * 100 else columMirror
    }

    fun part1(matrices: List<AshRockMatrix<Char>>) = getSafePathCount(matrices, false)

    fun part2(matrices: List<AshRockMatrix<Char>>) = getSafePathCount(matrices, true)

    val matrices = mutableListOf<AshRockMatrix<Char>>()
    var rows = mutableListOf<List<Char>>()

    val testInput = readInput("day13")
    testInput.forEach { row ->
        val lst = row.map { it }
        if (lst.isEmpty()) {
            matrices.add(AshRockMatrix(rows))
            rows = mutableListOf()
        } else {
            rows.add(lst)
        }
    }
    matrices.add(AshRockMatrix(rows))

    println(part2(matrices))

}