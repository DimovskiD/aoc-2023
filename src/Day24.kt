import com.microsoft.z3.Context
import com.microsoft.z3.Status
import java.math.BigDecimal

data class HailstoneProgression(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
    val dx: BigDecimal,
    val dy: BigDecimal,
    val dz: BigDecimal,
) {
    val calc = dy * x - dx * y
    val dxNegative = dx.negate()

    fun intersectWith(other: HailstoneProgression): Pair<BigDecimal, BigDecimal> {
        val x = (calc * other.dxNegative - other.calc * dxNegative) / (dy * other.dxNegative - other.dy * dxNegative)
        val y = (other.calc * dy - calc * other.dy) / (dy * other.dxNegative - other.dy * dxNegative)
        return x to y
    }
}

fun main() {
    val testInput = readInput("day24")

    val range = BigDecimal(200000000000000)..BigDecimal(400000000000000)
    val hailstones = testInput.map {
        val split = it.split("@")
        val (x, y, z) = split[0].split(",").map { num -> num.trim().toBigDecimal() }
        val (dx, dy, dz) = split[1].split(",").map { num -> num.trim().toBigDecimal() }
        HailstoneProgression(x, y, z, dx, dy, dz)
    }

    fun part1(): Int = hailstones.uniquePairs(hailstones).count { pair ->
        if (pair.first.dy * pair.second.dxNegative != pair.first.dxNegative * pair.second.dy) {
            val (x, y) = pair.first.intersectWith(pair.second)
            x in range && y in range && listOf(
                pair.first,
                pair.second
            ).all { (x - it.x) * it.dx >= BigDecimal.ZERO && (y - it.y) * it.dy >= BigDecimal.ZERO }
        } else false
    } / 2

    fun part2(): Long {
        val ctx = Context()
        val solver = ctx.mkSolver()
        val mx = ctx.mkRealConst("mx")
        val m = ctx.mkRealConst("m")
        val mz = ctx.mkRealConst("mz")
        val mxv = ctx.mkRealConst("mxv")
        val mv = ctx.mkRealConst("mv")
        val mzv = ctx.mkRealConst("mzv")
        repeat(3) {
            val (sx, sy, sz, sxv, syv, szv) = hailstones[it]
            val t = ctx.mkRealConst("t$it")
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(mx, ctx.mkMul(mxv, t)),
                    ctx.mkAdd(ctx.mkReal(sx.toString()), ctx.mkMul(ctx.mkReal(sxv.toString()), t))
                )
            )
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(m, ctx.mkMul(mv, t)),
                    ctx.mkAdd(ctx.mkReal(sy.toString()), ctx.mkMul(ctx.mkReal(syv.toString()), t))
                )
            )
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(mz, ctx.mkMul(mzv, t)),
                    ctx.mkAdd(ctx.mkReal(sz.toString()), ctx.mkMul(ctx.mkReal(szv.toString()), t))
                )
            )
        }
        if (solver.check() == Status.SATISFIABLE) {
            val model = solver.model
            val solution = listOf(mx, m, mz).sumOf { model.eval(it, false).toString().toDouble() }
            return solution.toLong()
        }

        return 0
    }

    println(part2())
}
