private class EngineSchematic(
    input: List<String>
) {
    private val matrixNumbers: Array<Array<Int?>>
    private val validMatrix: Array<Array<Boolean>>
    val matrixSymbols: Array<Array<Char?>>

    init {
        matrixNumbers = Array(input.size) { row ->
            val length = input[row].length
            Array(length) { column ->
                val char = input[row][column]
                if (char.isDigit()) char.digitToInt()
                else null
            }
        }

        matrixSymbols = Array(input.size) { row ->
            val length = input[row].length
            Array(length) { column ->
                val char = input[row][column]
                if (!char.isDigit() && char != '.') char
                else null
            }
        }

        validMatrix = Array(input.size) { row ->
            val length = input[row].length
            Array(length) { _ ->
                false
            }
        }
    }

    fun checkValidNumbers(rowIndex: Int, symbolIndex: Int) {
        checkValidNumbersPerRow(rowIndex, symbolIndex)
        if (rowIndex - 1 >= 0) {
            checkValidNumbersPerRow(rowIndex - 1, symbolIndex)
        }
        if (rowIndex + 1 <= matrixNumbers.size - 1) {
            checkValidNumbersPerRow(rowIndex + 1, symbolIndex)
        }
    }

    private fun checkValidNumbersPerRow(rowIndex: Int, symbolIndex: Int) {
        if (matrixNumbers[rowIndex][symbolIndex] != null) {
            validMatrix[rowIndex][symbolIndex] = true
        }
        if (symbolIndex - 1 > -1 && matrixNumbers[rowIndex][symbolIndex - 1] != null) {
            validMatrix[rowIndex][symbolIndex - 1] = true
        }
        if (symbolIndex + 1 < matrixNumbers[rowIndex].size && matrixNumbers[rowIndex][symbolIndex + 1] != null) {
            validMatrix[rowIndex][symbolIndex + 1] = true
        }
    }

    fun calculateSumOfEngineNumbers(): Int {
        val validNumbers = getValidNumbers { rowIndex, columnIndex -> validMatrix[rowIndex][columnIndex] }
        return validNumbers.sum()
    }

    fun calculateSumOfEngineGears(): Int {
        val gearRatios = mutableListOf<Int>()
        matrixSymbols.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, symbol ->
                if (symbol == '*') {
                    gearRatios.add(calculateGearRatio(rowIndex, columnIndex))
                }
            }
        }
        return gearRatios.sum()
    }

    private fun calculateGearRatio(rowIndex: Int, symbolIndex: Int): Int {
        val validNumbers = mutableListOf<Int>()
        validNumbers.addAll(getValidNumbersPerRow(rowIndex, symbolIndex))
        if (rowIndex - 1 >= 0) validNumbers.addAll(getValidNumbersPerRow(rowIndex - 1, symbolIndex))
        if (rowIndex + 1 < matrixNumbers[rowIndex].size) validNumbers.addAll(
            getValidNumbersPerRow(
                rowIndex + 1,
                symbolIndex
            )
        )
        return if (validNumbers.size == 2) validNumbers[0] * validNumbers[1]
        else 0
    }

    private fun getValidNumbersPerRow(rowIndex: Int, symbolIndex: Int): List<Int> {
        return getValidNumbers(rowIndex) { _, columnIndex ->
            symbolIndex == columnIndex || symbolIndex == columnIndex - 1 || symbolIndex == columnIndex + 1
        }
    }
    private fun getValidNumbers(rowIndexToCheck: Int? = null, validityCheck: (Int, Int) -> Boolean): List<Int> {
        val validNumbers = mutableListOf<Int>()
        matrixNumbers.forEachIndexed { rowIndex, numbersRow ->
            if (rowIndexToCheck == null || rowIndexToCheck == rowIndex) {
                var integer = ""
                var isValid = false
                numbersRow.forEachIndexed { columnIndex, number ->
                    if (number != null) {
                        integer += number
                        if (validityCheck(rowIndex, columnIndex)) isValid = true
                    } else {
                        if (isValid) {
                            validNumbers += integer.toInt()
                        }
                        isValid = false
                        integer = ""
                    }
                }
                if (isValid) validNumbers.add(integer.toInt())
            }
        }
        return validNumbers
    }

}

fun main() {

    fun part1(input: List<String>): Int {
        val engineSchematic = EngineSchematic(input)
        engineSchematic.matrixSymbols.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { symbolIndex, symbol ->
                print(symbol.toString() + ".")
                symbol?.let {
                    engineSchematic.checkValidNumbers(rowIndex, symbolIndex)
                }
            }
            println()
        }

        return engineSchematic.calculateSumOfEngineNumbers()
    }

    fun part2(input: List<String>): Int {
        val engineSchematic = EngineSchematic(input)
        engineSchematic.matrixSymbols.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { symbolIndex, symbol ->
                symbol?.let {
                    if (it == '*') engineSchematic.checkValidNumbers(rowIndex, symbolIndex)
                }
            }
        }
        return engineSchematic.calculateSumOfEngineGears()
    }

    val testInput = readInput("day03")
    println(part2(testInput))

}
