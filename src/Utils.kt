import helpers.Coordinates
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("input/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Boolean
): Long? {
    var begin = lowerBound
    var end = upperBound
    var result: Long? = null
    while (begin <= end) {
        val mid = (begin + end) / 2L
        if (evaluation(mid)) {
            result = mid
            if (inverted) {
                end = mid - 1
            } else {
                begin = mid + 1
            }
        } else {
            if (inverted) {
                begin = mid + 1
            } else {
                end = mid - 1
            }
        }
    }
    return result
}


fun lcm(a: Long, b: Long): Long {
    var ma = a
    var mb = b
    var remainder: Long

    while (mb != 0L) {
        remainder = ma % mb
        ma = mb
        mb = remainder
    }

    return a * b / ma
}

fun countOccurrences(str: String, searchStr: String): Int {
    var count = 0
    var startIndex = 0

    while (startIndex < str.length) {
        val index = str.indexOf(searchStr, startIndex)
        if (index >= 0) {
            count++
            startIndex = index + searchStr.length
        } else {
            break
        }
    }

    return count
}

fun longestRepeatingSubsequence(originalList: List<Int>, list: List<Int>): List<Int> {
    val string = originalList.joinToString(",")
    return if (list.isEmpty()) emptyList() else if (countOccurrences(string, list.drop(1).joinToString(",")) > 1) {
        list.drop(1)
    } else longestRepeatingSubsequence(originalList, list.drop(1))
}

fun<T> MutableList<MutableList<T>>.reversedColumns(): MutableList<MutableList<T>> {
    return this.map {
        it.reversed().toMutableList()
    }.toMutableList()
}

fun shoelaceArea(v: List<Coordinates>): Double {
    val n = v.size
    var a = 0.0
    for (i in 0 until n - 1) {
        val l1: Long = v[i].x.toLong() * v[i + 1].y
        val l2: Long = v[i + 1].x.toLong() * v[i].y
        a += l1 - l2
    }
    return abs(a + v[n - 1].x * v[0].y - v[0].x * v[n -1].y) / 2.0
}

