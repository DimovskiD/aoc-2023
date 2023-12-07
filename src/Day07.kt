val JOKER_CARD = CamelCard('J')

val cards = listOf(
    CamelCard('A'),
    CamelCard('K'),
    CamelCard('Q'),
    CamelCard('T'),
    CamelCard('9'),
    CamelCard('8'),
    CamelCard('7'),
    CamelCard('6'),
    CamelCard('5'),
    CamelCard('4'),
    CamelCard('3'),
    CamelCard('2'),
    CamelCard('J'),
)

val handOfCardsTypes = listOf(
    HandOfCardsType(
        type = "5oK",
        improver = { card -> if (card.cards.first() == JOKER_CARD) CamelCard('A') else null }
    ) { it.mappedCards.size == 1 },
    HandOfCardsType(
        type = "4oK",
        improver = { handOfCards ->
            val improver = handOfCards.mappedCards.find { it.second == 4 }?.first
            if (improver == JOKER_CARD) handOfCards.mappedCards.find { it.first != JOKER_CARD }?.first else improver
        }) { handOfCards ->
        handOfCards.cards.distinct().any { _ ->
            handOfCards.mappedCards.any { it.second == 4 }
        }
    },
    HandOfCardsType(
        type = "FH",
        improver = { handOfCards ->
            val improver = handOfCards.mappedCards.find { it.second == 3 }?.first
            if (improver == JOKER_CARD) handOfCards.mappedCards.find { it.second == 2 }?.first else improver
        }
    ) { handOfCards ->
        handOfCards.mappedCards.size == 2 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.any { it.second == 2 }
    },
    HandOfCardsType(
        type = "3oK",
        improver = { handOfCards ->
            val improver = handOfCards.mappedCards.find { it.second == 3 }?.first
            if (improver == JOKER_CARD) handOfCards.mappedCards.map { it.first }.maxOf { it } else improver
        }
    ) { handOfCards ->
        handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.none { it.second == 2 }
    },
    HandOfCardsType(
        type = "2P",
        improver = { handOfCards ->
            val firstPair = handOfCards.mappedCards.first { it.second == 2 }.first
            val secondPair = handOfCards.mappedCards.last { it.second == 2 }.first
            if (secondPair == JOKER_CARD) firstPair else if (firstPair == JOKER_CARD) secondPair
            else if (firstPair < secondPair) firstPair else secondPair
        }
    ) { handOfCards ->
        handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.count { it.second == 2 } == 2
    },
    HandOfCardsType("1P", { handOfCards ->
        val improver = handOfCards.mappedCards.find { it.second == 2 }?.first
        if (improver == JOKER_CARD) handOfCards.mappedCards.map { it.first }.maxOf { it } else improver
    }) { handOfCards ->
        handOfCards.mappedCards.size == 4 && handOfCards.mappedCards.count { it.second == 2 } == 1
    },
    HandOfCardsType("HC", { handOfCards -> handOfCards.mappedCards.map { it.first }.maxOf { it } }) { handOfCards ->
        handOfCards.mappedCards.size == 5
    }
)


data class CamelCard(val label: Char) : Comparable<CamelCard> {
    override fun compareTo(other: CamelCard): Int {
        return cards.indexOf(other).compareTo(cards.indexOf(this))
    }

    override fun toString(): String {
        return label.toString()
    }
}

data class HandOfCards(val cards: List<CamelCard>, val bid: Int) : Comparable<HandOfCards> {
    val mappedCards = cards.distinct().map { card -> card to cards.count { it.label == card.label } }
    private fun getTypeOfHand() = handOfCardsTypes.find { it.match(this) }
    override fun compareTo(other: HandOfCards): Int {
        val indexOfThis = handOfCardsTypes.indexOf(this.bestHand().getTypeOfHand())
        val indexOfOther = handOfCardsTypes.indexOf(other.bestHand().getTypeOfHand())
        if (indexOfOther == indexOfThis) {
            cards.zip(other.cards) { thisCard, otherCard ->
                if (thisCard != otherCard) return@compareTo thisCard.compareTo(otherCard)
            }
        }
        return handOfCardsTypes.indexOf(other.bestHand().getTypeOfHand())
            .compareTo(handOfCardsTypes.indexOf(this.bestHand().getTypeOfHand()))
    }

    private fun bestHand(): HandOfCards {
        if (!this.cards.contains(JOKER_CARD)) return this
        val improverCard = this.getTypeOfHand()?.improver?.invoke(this)
        return if (improverCard != null) {
            val newCards = cards.toMutableList()
            newCards.replaceAll { if (it.label == 'J') improverCard else it }
            HandOfCards(newCards, bid)
        } else this
    }

    override fun toString(): String {
        return cards.toString()
    }
}

data class HandOfCardsType(
    val type: String,
    val improver: (HandOfCards) -> CamelCard?,
    private val condition: (HandOfCards) -> Boolean
) {
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
        println(sortedCards)
        return sortedCards.sumOf { it.bid * (sortedCards.indexOf(it) + 1) }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("day07")
    println(part1(testInput))

}
