import Color.Companion.colorNameToColor


private val maxCubes = mapOf(
    Color.RED to 12, Color.GREEN to 13, Color.BLUE to 14
)
enum class Color(private val colorName: String) {
    BLUE("blue"), RED("red"), GREEN("green");

    companion object {
        fun colorNameToColor(name: String): Color? = when (name) {
            BLUE.colorName -> BLUE
            GREEN.colorName -> GREEN
            RED.colorName -> RED
            else -> null
        }
    }
}
data class GameConfig(
    val id: Int,
    val numberOfCubes: Map<Color, Int>
) {
    fun isValidConfig() = (maxCubes[Color.RED] ?: 0) >= (numberOfCubes[Color.RED] ?: 0) &&
                (maxCubes[Color.BLUE] ?: 0) >= (numberOfCubes[Color.BLUE] ?: 0) &&
                (maxCubes[Color.GREEN] ?: 0) >= (numberOfCubes[Color.GREEN] ?: 0)

    fun getPower(): Int {
        var result = 1
        numberOfCubes.values.forEach {
            result *= it
        }
        return result
    }
}

fun main() {

    fun getGameConfigsForInput(input: List<String>): List<GameConfig> {
        return input.map { inputLine ->
            val id = inputLine.substringBefore(":").filter { char -> char.isDigit() }.toInt()
            val cubes = mutableMapOf<Color, Int>()
            inputLine.substringAfter(":").split(";").forEach { game ->
                val colors = game.split(",")
                colors.forEach { color ->
                    val trimmedColor = color.trim(',')

                    val num = trimmedColor.replace(Regex("[^0-9]"), "").toInt()
                    val colorName = trimmedColor.replace(Regex("[0-9]"), "").trim()

                    colorNameToColor(colorName)?.let {
                        val existingQuantity = cubes[it] ?: 0
                        if (num > existingQuantity) {
                            cubes[it] = num
                        }
                    }
                }
            }
            GameConfig(id, cubes)
        }
    }

    fun part1(input: List<String>): Int {
        val gameConfigs = getGameConfigsForInput(input)
        return gameConfigs.sumOf { if (it.isValidConfig()) it.id else 0 }
    }

    fun part2(input: List<String>): Int {
        val gameConfigs = getGameConfigsForInput(input)
        return gameConfigs.sumOf { it.getPower() }
    }

    val testInput = readInput("day02")
    println(part2(testInput))

}
