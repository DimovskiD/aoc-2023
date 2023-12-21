enum class Pulse{ HIGH, LOW }

val pulsesQueue = mutableListOf<() -> Pair<String, Pulse>>()
sealed class Module(val name: String) {

    var destinationModules: List<Module> = emptyList()
    var totalLowSent = 0
    var totalHighSent = 0
    abstract fun receivePulse(pulse: Pulse, from: Module?)

    override fun toString(): String {
        return name
    }

    fun sendPulse(pulse: Pulse) {
        destinationModules.forEach {
            if (pulse == Pulse.HIGH) totalHighSent++
            else totalLowSent++
            println("SENDING $pulse from $this to $it")
            pulsesQueue.add {
                it.receivePulse(pulse, this)
                return@add it.name to pulse
            }
        }
    }

    class FlipFlopModule(name: String) : Module(name) {
        private var isTurnedOn = false
        override fun receivePulse(pulse: Pulse, from: Module?) {
            if (pulse == Pulse.LOW) {
                isTurnedOn = !isTurnedOn
                sendPulse(if (isTurnedOn) Pulse.HIGH else Pulse.LOW) }
            }
    }

    class ConjunctionModule(name: String): Module(name) {
        private val receivedPulses: HashMap<Module, Pulse> = hashMapOf()

        fun addInput(module: Module) {
            receivedPulses[module] = Pulse.LOW
        }

        override fun receivePulse(pulse: Pulse, from: Module?){
            if (from != null) {
                receivedPulses[from] = pulse
            }

            if (receivedPulses.all { it.value == Pulse.HIGH }) sendPulse(Pulse.LOW)
            else sendPulse(Pulse.HIGH)
        }
    }

    class BroadcastModule(name: String) : Module(name) {
        override fun receivePulse(pulse: Pulse, from: Module?) {
            sendPulse(pulse)
        }

    }

    class DummyModule(name: String) : Module(name) {
        override fun receivePulse(pulse: Pulse, from: Module?) { /* NO-OP */}

    }

    class ButtonModule(private val broadcastModule: BroadcastModule){
        fun pressButton() =
            pulsesQueue.add {
                broadcastModule.receivePulse(Pulse.LOW, null)
                return@add "button" to Pulse.LOW
            }
    }

}

fun main() {

    var buttonModule: Module.ButtonModule? = null

    fun extractModules(input: List<String>): List<Module> {

        val modules = input.mapNotNull {
            val split = it.split("->")
            val typeAndName = split[0].trim()
            if (typeAndName.startsWith('%')) {
                Module.FlipFlopModule(typeAndName.substringAfter('%'))
            } else if (typeAndName.startsWith('&')) {
                Module.ConjunctionModule(typeAndName.substringAfter('&'))
            } else if (typeAndName == "broadcaster") {
                val broadcastModule = Module.BroadcastModule("broadcaster")
                buttonModule = Module.ButtonModule(broadcastModule)
                broadcastModule
            } else null
        }
        input.forEach { line ->
            val split = line.split("->")
            val name = if (split[0].trim() != "broadcaster") split[0].substring(1).trim() else split[0].trim()
            val destinationModules =split[1].split(",")
            val foundModule = modules.first { it.name == name }
            foundModule.destinationModules = destinationModules.map { dest ->
                val destination = modules.firstOrNull() { it.name == dest.trim() }
                if (destination is Module.ConjunctionModule) {
                    destination.addInput(foundModule)
                }
                destination?: Module.DummyModule(dest)
            }

        }
        return modules
    }

    fun part1(input: List<String>): Long {
        val modules = extractModules(input)
        modules.println()

        val btnPresses = 1000
        var count = 0
        while (true) {
            if (pulsesQueue.isEmpty()) {
                buttonModule?.pressButton()
                count++
            } else {
                pulsesQueue[0].invoke()
                pulsesQueue.removeAt(0)
            }
            if (count == 1000 && pulsesQueue.isEmpty()) break
        }

        val totalLow = modules.sumOf { it.totalLowSent } + btnPresses
        val totalHigh = modules.sumOf { it.totalHighSent }.toLong()

        return totalLow * totalHigh
    }

    fun part2(input: List<String>): Long {
        val modules = extractModules(input)
        val nextToLast = modules.filter { it.destinationModules.any { dest -> dest.name.trim() == "rx"}}[0]
        val twiceRemoved =  modules.filter { it.destinationModules.any { dest -> dest == nextToLast }}
        val map = hashMapOf<String, Int>()
        var count = 0

        while (true) {
            if (pulsesQueue.isEmpty()) {
                buttonModule?.pressButton()
                count++
            } else {
                val (moduleName, pulse) = pulsesQueue[0].invoke()
                val moduleInMap = twiceRemoved.filter { it.name == moduleName }
                moduleInMap.println()
                if (moduleInMap.firstOrNull() != null && pulse == Pulse.LOW ) {
                    map[moduleInMap.first().name] = count
                }
                pulsesQueue.removeAt(0)
            }
            if (map.values.all { it != -1 }) break
        }
        return lcm(map.values.map { it.toLong() })
    }

    val testInput = readInput("day20")
    println(part2(testInput))
}