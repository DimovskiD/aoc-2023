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