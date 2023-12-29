data class MachinePart(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun sum(): Int = x + m + a + s
}

data class MachinePartRanges(var x: IntRange, var m: IntRange, var a: IntRange, var s: IntRange) {
    fun getOnePart() = MachinePart(x.last, m.last, a.last, s.last)

    fun getRangeForProperty(char: Char) = when (char) {
        'x' -> x
        'm' -> m
        'a' -> a
        else -> s
    }

    fun copyAndSet(property: Char, range: IntRange): MachinePartRanges {
        return when (property) {
            'x' -> copy(x = range)
            'm' -> copy(m = range)
            'a' -> copy(a = range)
            else -> copy(s = range)
        }
    }
}

data class Rule(
    val property: Char,
    val operation: Char,
    val value: Int,
    val result: String,
    val isDefault: Boolean = false
) {
    fun check(it: MachinePart): String? {
        return if (isDefault) result
        else if (property == 'x' && (operation == '<' && it.x < value || operation == '>' && it.x > value)) result
        else if (property == 'a' && (operation == '<' && it.a < value || operation == '>' && it.a > value)) result
        else if (property == 'm' && (operation == '<' && it.m < value || operation == '>' && it.m > value)) result
        else if (property == 's' && (operation == '<' && it.s < value || operation == '>' && it.s > value)) result
        else null
    }
}

data class Workflow(val rules: List<Rule>)

fun main() {
    val workflows: HashMap<String, Workflow> = hashMapOf()
    val valid = mutableListOf<MachinePartRanges>()

    fun executeWorkflow(
        nextWorkFlowName: String,
        part: MachinePart,
    ): String? {
        val workflow = workflows[nextWorkFlowName]!!
        val rules = workflow.rules

        for (checkedRules in rules.indices) {
            val checked = rules[checkedRules].check(part)
            if (checked == "A" || checked == "R") return checked
            else if (checked != null) return executeWorkflow(checked, part)
        }
        return null
    }

    fun subtract(from: IntRange, subtractThis: IntRange): IntRange {
        val subtracted = from.subtract(subtractThis)
        if (subtracted.isNotEmpty())
            return subtracted.first()..subtracted.last()
        return from
    }

    fun getNextRange(property: Char, part: MachinePartRanges, list: MutableList<MachinePartRanges>): IntRange {
        var nextRange = part.getRangeForProperty(property)

        list.forEach {
            nextRange = subtract(nextRange, it.getRangeForProperty(property))
        }
        return nextRange
    }

    fun executeWorkflow(
        nextWorkFlowName: String,
        firstPart: MachinePartRanges,
    ) {
        val list = mutableListOf<MachinePartRanges>()
        var part = firstPart
        var nextPart = firstPart

        val workflow = workflows[nextWorkFlowName] ?: return
        val rules = workflow.rules

        for (rule in rules) {
            val property = rule.property

            if (rule.isDefault) {
                list.add(nextPart)
            } else {
                var nextRange = getNextRange(property, part, list)
                val range = if (rule.operation == '<') {
                    part.getRangeForProperty(property).first until rule.value
                } else {
                    rule.value + 1..part.getRangeForProperty(property).last
                }

                part = part.copyAndSet(property, range)

                nextRange = subtract(nextRange, part.getRangeForProperty(property))
                nextPart = part.copyAndSet(property, nextRange)

                list.add(part)
                part = nextPart
            }
        }

        list.forEachIndexed { index, it ->
            rules[index].check(it.getOnePart())?.let { res ->
                if (res == "A") valid.add(it)
                executeWorkflow(res, it)
            }
        }
    }

    fun extractWorkflows(input: List<String>) {
        input.subList(0, input.indexOf("")).map { workflow ->
            val name = workflow.substring(0, workflow.indexOf('{'))
            val rules = workflow.substring(workflow.indexOf('{') + 1, workflow.indexOf('}')).split(",")
            val mappedRules = rules.dropLast(1).map { rule ->
                val property = rule[0]
                val operation = rule[1]
                val value = rule.substring(2, rule.indexOf(':')).toInt()
                val workflowName = rule.substring(rule.indexOf(':') + 1)

                Rule(property, operation, value, workflowName)
            }
            val default = Rule(' ', ' ', 0, rules.last(), true)
            workflows[name] = Workflow(mappedRules + default)
        }
    }

    fun extractParts(input: List<String>): List<MachinePart> {
        return input.subList(input.indexOf("") + 1, input.size).map {
            val x = it.substring(it.indexOf("x=") + 2, it.indexOf("m=") - 1).toInt()
            val m = it.substring(it.indexOf("m=") + 2, it.indexOf("a=") - 1).toInt()
            val a = it.substring(it.indexOf("a=") + 2, it.indexOf("s=") - 1).toInt()
            val s = it.substring(it.indexOf("s=") + 2, it.indexOf("}")).toInt()
            MachinePart(x, m, a, s)
        }
    }

    fun part1(input: List<String>): Int {
        extractWorkflows(input)
        val parts = extractParts(input)

        return parts.sumOf { part ->
            if (executeWorkflow("in", part) == "A") part.sum() else 0
        }
    }

    fun part2(input: List<String>): Long {
        extractWorkflows(input)
        executeWorkflow("in", MachinePartRanges(1..4000, 1..4000, 1..4000, 1..4000))

        return valid.toSet().sumOf {
            it.a.toSet().size.toLong() * it.x.toSet().size.toLong() * it.m.toSet().size.toLong() * it.s.toSet().size.toLong()
        }
    }

    val testInput = readInput("day19")
    println(part2(testInput))

}
