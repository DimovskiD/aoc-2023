data class CamelCard(val label: Char): Comparable<CamelCard> {
    override fun compareTo(other: CamelCard): Int {
        return cards.indexOf(other).compareTo(cards.indexOf(this))
    }
    override fun toString(): String {
        return label.toString()
    }
}

val cards = listOf(
    CamelCard('A'),
    CamelCard('K'),
    CamelCard('Q'),
    CamelCard('J'),
    CamelCard('T'),
    CamelCard('9'),
    CamelCard('8'),
    CamelCard('7'),
    CamelCard('6'),
    CamelCard('5'),
    CamelCard('4'),
    CamelCard('3'),
    CamelCard('2')
)

val handOfCardsTypes = listOf(
    HandOfCardsType("5oK") { it.mappedCards.size == 1 },
    HandOfCardsType("4oK") { handOfCards ->
        handOfCards.cards.distinct().any { _ -> handOfCards.mappedCards.any { it.second == 4 }}
    },
    HandOfCardsType("FH") { handOfCards ->
        handOfCards.mappedCards.size == 2 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.any { it.second == 2}
    },
    HandOfCardsType("3oK") { handOfCards ->
        handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.none { it.second == 2}
    },
    HandOfCardsType("2P") { handOfCards ->
        handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.count { it.second == 2 } == 2
    },
    HandOfCardsType("1P") { handOfCards ->
        handOfCards.mappedCards.size == 4 && handOfCards.mappedCards.count { it.second == 2 } == 1
    },
    HandOfCardsType("HC") { handOfCards ->
        handOfCards.mappedCards.size == 5
    }
)

data class HandOfCards(val cards: List<CamelCard>, val bid: Int) : Comparable<HandOfCards> {
    val mappedCards = cards.distinct().map { card -> card to cards.count { it.label == card.label } }

    private fun getTypeOfHand()  = handOfCardsTypes.find { it.match(this) }
    override fun compareTo(other: HandOfCards): Int {
        val indexOfThis = handOfCardsTypes.indexOf(this.getTypeOfHand())
        val indexOfOther = handOfCardsTypes.indexOf(other.getTypeOfHand())
        if (indexOfOther == indexOfThis) {
            cards.zip(other.cards) { thisCard, otherCard ->
                if (thisCard != otherCard) return@compareTo thisCard.compareTo(otherCard)
            }
        }
        return handOfCardsTypes.indexOf(other.getTypeOfHand()).compareTo(handOfCardsTypes.indexOf(this.getTypeOfHand()))
    }

    override fun toString(): String {
        return cards.toString()
    }
}

data class HandOfCardsType(val type: String, val condition: (HandOfCards) -> Boolean) {

    fun match(handOfCards: HandOfCards) = condition(handOfCards)
}

fun main() {

    fun part1(input: List<String>): Int {
        val handsOfCards = input.map {
            val split = it.split(" ")
            val cards = split[0].map { char -> CamelCard(char) }
            HandOfCards(cards, split[1].toInt())
        }
        val sortedCards = handsOfCards.sorted()
        return sortedCards.sumOf { it.bid * (sortedCards.indexOf(it) + 1) }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day07")
    println(part1(testInput))

}
