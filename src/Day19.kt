data class MachinePart(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun sum(): Int = s + x + m + a

}

data class Rule(
    val property: Char,
    val operation: Char,
    val value: Int,
    val result: String,
    val isDefault: Boolean = false
) {

    fun check(it: MachinePart): String? {
        return if (isDefault) result else if (property == 'a' && (operation == '<' && it.a < value || operation == '>' && it.a > value)) result
        else if (property == 'x' && (operation == '<' && it.x < value || operation == '>' && it.x > value)) result
        else if (property == 'm' && (operation == '<' && it.m < value || operation == '>' && it.m > value)) result
        else if (property == 's' && (operation == '<' && it.s < value || operation == '>' && it.s > value)) result
        else null
    }

}

data class Workflow(val rules: List<Rule>)

fun main() {

    val workflows: HashMap<String, Workflow> = hashMapOf()

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

    fun part1(input: List<String>): Int {
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
        val parts = input.subList(input.indexOf("") + 1, input.size).map {
            val x = it.substring(it.indexOf("x=") + 2, it.indexOf("m=") - 1).toInt()
            val m = it.substring(it.indexOf("m=") + 2, it.indexOf("a=") - 1).toInt()
            val a = it.substring(it.indexOf("a=") + 2, it.indexOf("s=") - 1).toInt()
            val s = it.substring(it.indexOf("s=") + 2, it.indexOf("}")).toInt()
            MachinePart(x, m, a, s)
        }

        return parts.sumOf { part ->
            if (executeWorkflow("in", part) == "A") part.sum() else 0
        }
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("day19")
    println(part1(testInput))

}