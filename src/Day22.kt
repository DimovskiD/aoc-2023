data class Brick(val id: Char, val x: IntRange, val y: IntRange, val z: IntRange) {
    val supports = mutableListOf<Brick>()
    val supportedBy = mutableListOf<Brick>()

    fun calculateJengaEffect(bricks: List<Brick>, removedBricks: MutableList<Brick>): Int {
        val mutableBricks = bricks.toMutableList()
        mutableBricks.remove(this)
        removedBricks += this

        val unsupportedBricks = mutableBricks.filter {
            it.supportedBy.isNotEmpty() && it.supportedBy.all { support ->
                removedBricks.contains(support)
            }
        }

        if (unsupportedBricks.isEmpty()) return removedBricks.size - 1

        mutableBricks.removeAll(unsupportedBricks)
        removedBricks.addAll(unsupportedBricks)

        return calculateJengaEffect(mutableBricks, removedBricks) - 1
    }
}

fun main() {

    fun processInput(input: List<String>): List<Brick> {
        var char = Char(65)
        return input.map { line ->
            val split = line.split("~")
            val first = split[0].split(",").map { it.toInt() }
            val second = split[1].split(",").map { it.toInt() }
            Brick(char++, first[0]..second[0], first[1]..second[1], first[2]..second[2])
        }.sortedBy { it.z.first }
    }

    fun letBricksFall(bricks: List<Brick>): List<Brick> {
        val bricksInPlace = mutableListOf<Brick>()
        bricks.forEach { brick ->
            var fallingBrick = brick
            var isSupported = false
            while (!isSupported) {
                val supportingBricks = bricksInPlace.filter {
                    it.x.intersect(fallingBrick.x).isNotEmpty() && it.y.intersect(fallingBrick.y)
                        .isNotEmpty() && it.z.last == fallingBrick.z.first - 1
                }
                isSupported = supportingBricks.isNotEmpty() || fallingBrick.z.first == 0
                if (isSupported) {
                    supportingBricks.forEach {
                        it.supports.add(fallingBrick)
                        fallingBrick.supportedBy.add(it)
                    }
                    bricksInPlace.add(fallingBrick)
                } else {
                    val nextZ =
                        bricksInPlace.filter { it.z.last < fallingBrick.z.first - 1 }
                            .maxByOrNull { it.z.last }?.z?.last?.let { it + 1 } ?: 0
                    val height = fallingBrick.z.last - fallingBrick.z.first
                    fallingBrick = Brick(
                        fallingBrick.id, fallingBrick.x, fallingBrick.y, nextZ..nextZ + height
                    )
                }
            }
        }
        return bricksInPlace
    }

    fun part1(input: List<String>): Int {
        val bricks = processInput(input)

        val bricksInPlace = letBricksFall(bricks)

        return bricksInPlace.count { brickToTake -> !brickToTake.supports.any { it.supportedBy.size == 1 } }
    }

    fun part2(input: List<String>): Int {
        val bricks = letBricksFall(processInput(input))
        val bricksSupportingOtherBricks = bricks.filter { brickToTake -> brickToTake.supports.isNotEmpty() }

        return bricksSupportingOtherBricks.sumOf { brick ->
            brick.calculateJengaEffect(bricks, mutableListOf(brick)) - 1
        }
    }

    val testInput = readInput("day22")
    println(part2(testInput))
}