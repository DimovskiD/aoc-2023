private class EngineSchematic(
    input: List<String>
) {

    private val matrixNumbers: Array<Array<Int?>>
    val matrixSymbols: Array<Array<Char?>>
    val validMatrix: Array<Array<Boolean>>

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
            Array(length) { column ->
                false
            }
        }
    }


    fun checkValidNumbers(rowIndex: Int, symbolIndex: Int) {
        if (matrixNumbers[rowIndex][symbolIndex] !=null){
            validMatrix[rowIndex][symbolIndex] = true
        }
        if (symbolIndex -1 > -1 && matrixNumbers[rowIndex][symbolIndex -1] != null){
            validMatrix[rowIndex][symbolIndex-1] = true
        }
        if (symbolIndex+1 < matrixNumbers[rowIndex].size && matrixNumbers[rowIndex][symbolIndex + 1] != null) {
            validMatrix[rowIndex][symbolIndex+1] = true
        }
        if (rowIndex - 1 >= 0) {
            if (matrixNumbers[rowIndex -1 ][symbolIndex] !=null){
                validMatrix[rowIndex - 1][symbolIndex] = true
            }
            if (symbolIndex -1 > -1 && matrixNumbers[rowIndex -1][symbolIndex -1] != null){
                validMatrix[rowIndex - 1][symbolIndex-1] = true
            }
            if (symbolIndex+1 < matrixNumbers[rowIndex- 1].size && matrixNumbers[rowIndex-1][symbolIndex + 1] != null) {
                validMatrix[rowIndex - 1][symbolIndex+1] = true
            }
        }
        if (rowIndex + 1 <= matrixNumbers.size - 1) {
            if (matrixNumbers[rowIndex +1 ][symbolIndex] !=null){
                validMatrix[rowIndex + 1][symbolIndex] = true
            }
            if (symbolIndex -1 > -1 && matrixNumbers[rowIndex +1][symbolIndex -1] != null){
                validMatrix[rowIndex + 1][symbolIndex-1] = true
            }
            if (symbolIndex+1 < matrixNumbers[rowIndex+1].size && matrixNumbers[rowIndex+1][symbolIndex + 1] != null) {
                validMatrix[rowIndex + 1][symbolIndex+1] = true
            }
        }
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


    private fun getValidNumbers(rowIndex: Int, symbolIndex: Int) :List<Int> {
        val validNumbers = mutableListOf<Int>()
        var integer = ""
        var valid = false
        matrixNumbers[rowIndex].forEachIndexed() { columnIndex, char ->
            if (char != null) {
                integer += char
                if (symbolIndex == columnIndex || symbolIndex == columnIndex -1 || symbolIndex == columnIndex +1)
                    valid = true
            } else {
                if (valid) validNumbers.add(integer.toInt())
                valid = false
                integer = ""
            }
        }
        if (valid) validNumbers.add(integer.toInt())
        validNumbers.forEach {
            kotlin.io.println()
        }
        return validNumbers
    }
    private fun calculateGearRatio(rowIndex: Int, symbolIndex: Int): Int {
        val validNumbers = mutableListOf<Int>()
        validNumbers.addAll(getValidNumbers(rowIndex,symbolIndex))
        if (rowIndex -1 >= 0) validNumbers.addAll(getValidNumbers(rowIndex - 1, symbolIndex))
        if (rowIndex + 1 < matrixNumbers[rowIndex].size) validNumbers.addAll(getValidNumbers(rowIndex + 1, symbolIndex))

        return if (validNumbers.size == 2) validNumbers[0] * validNumbers[1]
        else 0
    }

    fun calculateSumOfEngineNumbers(): Int {
        val validNumbers = mutableListOf<Int>()
        matrixNumbers.forEachIndexed { rowIndex, numbersRow ->
            var integer = ""
            var isValid = false
            numbersRow.forEachIndexed { columnIndex, number ->
                if (number != null) {
                    print(number)
                    integer += number
                    if (validMatrix[rowIndex][columnIndex]) isValid = true
                } else {
                    print(".")
                    if (isValid) {
                        validNumbers += integer.toInt()
                    }
                    isValid = false
                    integer = ""
                }
            }
            if (isValid) validNumbers.add(integer.toInt())
            println()
        }
        validNumbers.forEach {
            kotlin.io.println(it)
        }
        return validNumbers.sum()
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
                print(symbol.toString() + ".")
                symbol?.let {
                    if (it == '*') engineSchematic.checkValidNumbers(rowIndex, symbolIndex)
                }
            }
            println()
        }

        return engineSchematic.calculateSumOfEngineGears()
    }

    val testInput = readInput("day03")
    println(part2(testInput))

}
