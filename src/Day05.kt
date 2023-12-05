class Almanac(input: List<String>, inverse: Boolean = false) {

    val mappings: MutableList<MappingGroup> = mutableListOf()
    val seeds: List<Long>
    val seedRanges: List<LongRange>

    init {
        seeds = input[0].substringAfter(":").toListOfNumbers()
        seedRanges = seeds.chunked(2).map { (start, len) -> LongRange(start, start + len - 1) }

        var mappingsList = mutableListOf<Mapping>()
        input.subList(1, input.size - 1).forEach { line ->
            if (line.toListOfNumbers().isEmpty()) {
                if (mappingsList.isNotEmpty()) {
                    mappings += MappingGroup(
                        mappings = mappingsList
                    )
                }
                mappingsList = mutableListOf()
            } else {
                val (dest, source, length) = line.toListOfNumbers()
                mappingsList += Mapping(
                    source = if (inverse) dest else source,
                    dest = if (inverse) source else dest,
                    length = length,
                )
            }
        }
        if (mappingsList.isNotEmpty()) {
            mappings += MappingGroup(
                mappings = mappingsList
            )
        }
        if (inverse) mappings.reverse()
    }

    fun map(value: Long): Long {
        var result = value
        for (mapping in mappings) {
            result = mapping.mappedValue(result)
        }
        return result
    }
}

data class Mapping(val source: Long, val dest: Long, val length: Long) {
    fun mappedValue(value: Long): Long? {
        return if (value in source until source + length) dest + (value - source) else null
    }
}

data class MappingGroup(val mappings: List<Mapping>) {
    fun mappedValue(value: Long): Long {
        return mappings.firstNotNullOfOrNull { it.mappedValue(value) } ?: value
    }

    fun maxValue(): Long = mappings.maxOf {
        if (it.dest > it.source) it.dest else it.source
    }

}

fun main() {

    fun part1(input: List<String>): Long {
        val almanac = Almanac(input)
        return almanac.seeds.minOf { seed -> almanac.map(seed) }
    }

    fun part2(input: List<String>): Long {
        val almanac = Almanac(input, inverse = true)

        var result = almanac.mappings.maxOf { it.maxValue() }

        var invertBinarySearch = true
        while (true) {
            result =
                binarySearch(lowerBound = 0, upperBound = result - 1, inverted = invertBinarySearch) { value: Long ->
                    almanac.seedRanges.any { it.contains(almanac.map(value)) }
                } ?: break
            invertBinarySearch = !invertBinarySearch
        }

        return result
    }

    val testInput = readInput("day05")
    println(part2(testInput))

}
