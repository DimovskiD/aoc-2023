
data class Lens(val label: String, val focalLength: Int?)
enum class LensOperation(val sign: Char) { SET('='), REMOVE('-') }
class Step(private val input: String) {
    val label: String
    val operation: LensOperation = if (input.contains(LensOperation.REMOVE.sign)) LensOperation.REMOVE else LensOperation.SET

    init {
        label = input.substringBefore(operation.sign)
    }
    fun toLens() = Lens(label, input.substringAfter(operation.sign).toIntOrNull())
}
fun main() {

    fun hash(string: String): Int {
        var currentValue = 0
        string.forEach { char ->
            currentValue += char.code
            currentValue *= 17
            currentValue %= 256
        }
        return currentValue
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {string ->
            hash(string)
        }
    }

    fun part2(input: List<String>): Int {
        val boxes = hashMapOf<Int, MutableList<Lens>>()
        input.forEach { string ->
            val step = Step(string)
            when (step.operation) {
                LensOperation.REMOVE -> {
                    boxes[hash(step.label)]?.removeIf { it.label == step.label }
                }
                LensOperation.SET -> {
                    val lens = step.toLens()
                    val boxNumber = hash(lens.label)
                    if (boxes[boxNumber].isNullOrEmpty()) {
                        boxes[boxNumber] = mutableListOf(lens)
                    } else {
                        val existingLens = boxes[boxNumber]?.firstOrNull { it.label == lens.label }
                        if (existingLens != null) {
                            boxes[boxNumber]?.indexOf(existingLens)?.let { boxes[boxNumber]?.set(it, lens) }
                        } else boxes[boxNumber]?.add(lens)
                    }
                }
            }
        }
        return boxes.keys.sumOf {
            boxes[it]?.mapIndexed { index, lens ->
                (1 + it) * (index + 1) * (lens.focalLength ?: 1)
            }?.sum() ?: 0
        }
    }

    val testInput = readInput("day15").single().split(",")
    println(part2(testInput))
}