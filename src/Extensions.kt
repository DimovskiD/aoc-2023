fun String.replaceText(keys: Map<String, String>): String =
    keys.entries.fold(this) { acc, (key, value) -> acc.replace(key, value) }

fun String.replaceCustom(wordToNumber: Map<String, String>): String {
    var min: Int? = null
    var minSubstring: String? = null
    var max: Int? = null
    var maxSubstring: String? = null
    wordToNumber.keys.forEach {
        if (it in this) {
            val firstIndex = this.indexOf(it, ignoreCase = true)
            val lastIndex = this.lastIndexOf(it, ignoreCase = true)
            if (min == null || firstIndex < min!! ) {
                min = firstIndex
                minSubstring = it
            }
            if (max == null || lastIndex > max!!) {
                max = lastIndex
                maxSubstring = it
            }
        }
    }

    var final = this
    minSubstring?.let {
        final = final.replace(it, wordToNumber[it]!!)
    }
    maxSubstring?.let {
        final = final.replace(it, wordToNumber[it]!!)
    }
    return final
}

fun String.toListOfNumbers() = this.trim().split(' ').mapNotNull { number ->
    val filteredNumber = number.replace(Regex("[^0-9]"), "")
    if (filteredNumber.isNotEmpty())
        filteredNumber.toLong()
    else null
}

fun String.toNumber() = this.replace(Regex("^[0-9]"), "").replace(" ","").trim().toLong()

fun <T: Number> List<T>.multiplyContent(): Float {
    var total = 1f
    this.forEach {
        total *= it.toFloat()
    }
    return total
}

fun <T> List<List<T>>.println() {
    forEach {
        it.forEach {
            print("$it ")
        }
        kotlin.io.println()
    }
}

fun <T> List<List<T>>.println(whatToPrint: (T) -> String) {
    forEach {
        it.forEach {
            print("${whatToPrint(it)} ")
        }
        kotlin.io.println()
    }
}