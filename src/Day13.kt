data class Matrix<T>(val rows: List<List<T>>) {

    val columns: List<List<T>> = rows[0].indices.map { column ->
        rows.map { it[column] }
    }

    private fun isReflection(first: List<List<T>>, second: List<List<T>>): Boolean {
        first.forEachIndexed { index, element ->
            if (element != second[second.size - 1 -index]) return false
        }
        return true
    }

    fun findSafePathCount(list: List<List<T>>, index: Int, count: Int): Int {
        if (index >= list.size) return findSafePathCount(list,0, list.size/2)

        val first = list.subList(index, index + count)
        val second = list.subList(index + count, index + count * 2)

        return if (isReflection(first, second) && (index ==0 || index + count * 2 == list.size)) {
            index + first.size
        }
        else if (index + count * 2 < list.size) {
            return findSafePathCount(list,index + 1, count)
        }
        else if (first.size > 1 && second.size > 1) findSafePathCount(list, 0, count - 1)
        else if ((index + 1 + count * 2) < list.size ) findSafePathCount(list,index + 1, list.size / 2)
        else 0
    }

    override fun toString(): String {
        return buildString {
            rows.forEach {
                it.forEach {
                    append(it.toString() + " ")
                }
                append("\n")
            }
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {

        val matrices = mutableListOf<Matrix<Char>>()
        var rows = mutableListOf<List<Char>>()
        input.forEach { row ->
            val lst = row.map { it }
            if (lst.isEmpty()) {
                matrices.add(Matrix(rows))
                rows = mutableListOf()
            } else {
                rows.add(lst)
            }
        }
        matrices.add(Matrix(rows))

        return matrices.sumOf {
            val rowMirror = it.findSafePathCount(it.rows,0, it.rows.size / 2)
            val columMirror = it.findSafePathCount(it.columns, 0, it.columns.size / 2)
            if (rowMirror > columMirror) rowMirror * 100 else columMirror
        }
    }

    fun part2(input: List<String>): Int {
       return 0
    }

    val testInput = readInput("day13")
    println(part1(testInput))

}