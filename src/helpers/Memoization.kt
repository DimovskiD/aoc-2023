package helpers
class MemoizeOne<in T, in X, out R>(val f: (T, X) -> R) : (T, X) -> R {
    private val values = mutableMapOf<Pair<T, X>, R>()
    override fun invoke(t: T, x: X): R {
        return values.getOrPut(t to x) { f(t, x) }
    }
}

fun <T, X, R> ((T, X)-> R).memoize(): (T, X) -> R = MemoizeOne(this)
