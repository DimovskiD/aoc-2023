import CamelPokerRules.Companion.cards

val JOKER_CARD = CamelCard('J')

data class CamelPokerRules(val isJokerMode: Boolean) {
    companion object {
        val cards = mutableListOf(
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
        )
    }

    init {
        if (isJokerMode) cards.add(JOKER_CARD)
        else cards.add(3, JOKER_CARD)
    }

    private val handOfCardsTypes = listOf(
        HandOfCardsType(
            type = "5oK",
            improverCard = { card -> if (card.cards.first() == JOKER_CARD) cards[0] else null }
        ) { it.mappedCards.size == 1 },
        HandOfCardsType(
            type = "4oK",
            improverCard = { handOfCards ->
                val improver = handOfCards.mappedCards.find { it.second == 4 }?.first
                if (improver == JOKER_CARD) {
                    handOfCards.mappedCards.find { it.first != JOKER_CARD }?.first
                } else improver
            }
        ) { handOfCards ->
            handOfCards.cards.distinct().any { _ ->
                handOfCards.mappedCards.any { it.second == 4 }
            }
        },
        HandOfCardsType(
            type = "FH",
            improverCard = { handOfCards ->
                val improver = handOfCards.mappedCards.find { it.second == 3 }?.first
                if (improver == JOKER_CARD) {
                    handOfCards.mappedCards.find { it.second == 2 }?.first
                } else improver
            }
        ) { handOfCards ->
            handOfCards.mappedCards.size == 2 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.any { it.second == 2 }
        },
        HandOfCardsType(
            type = "3oK",
            improverCard = { handOfCards ->
                val improver = handOfCards.mappedCards.find { it.second == 3 }?.first
                if (improver == JOKER_CARD) {
                    handOfCards.mappedCards.map { it.first }.maxOf { it }
                } else improver
            }
        ) { handOfCards ->
            handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.any { it.second == 3 } && handOfCards.mappedCards.none { it.second == 2 }
        },
        HandOfCardsType(
            type = "2P",
            improverCard = { handOfCards ->
                val firstPair = handOfCards.mappedCards.first { it.second == 2 }.first
                val secondPair = handOfCards.mappedCards.last { it.second == 2 }.first
                if (secondPair == JOKER_CARD || firstPair != JOKER_CARD && firstPair < secondPair) {
                    firstPair
                } else secondPair
            }
        ) { handOfCards ->
            handOfCards.mappedCards.size == 3 && handOfCards.mappedCards.count { it.second == 2 } == 2
        },
        HandOfCardsType(
            type = "1P",
            improverCard = { handOfCards ->
                val improver = handOfCards.mappedCards.find { it.second == 2 }?.first
                if (improver == JOKER_CARD) {
                    handOfCards.mappedCards.map { it.first }.maxOf { it }
                } else improver
            }
        ) { handOfCards ->
            handOfCards.mappedCards.size == 4 && handOfCards.mappedCards.count { it.second == 2 } == 1
        },
        HandOfCardsType(
            type = "HC",
            improverCard = { handOfCards -> handOfCards.mappedCards.map { it.first }.maxOf { it } }
        ) { handOfCards ->
            handOfCards.mappedCards.size == 5
        }
    )

    private fun getTypeOfHand(handOfCards: HandOfCards) = handOfCardsTypes.find { it.match(handOfCards) }

    fun compareHands(thisHand: HandOfCards, otherHand: HandOfCards): Int {
        val indexOfThisImprovedHand = handOfCardsTypes.indexOf(getTypeOfHand(bestHand(thisHand)))
        val indexOfOtherImprovedHand = handOfCardsTypes.indexOf(getTypeOfHand(bestHand(otherHand)))

        if (indexOfOtherImprovedHand == indexOfThisImprovedHand) {
            thisHand.cards.zip(otherHand.cards) { thisCard, otherCard ->
                if (thisCard != otherCard) return@compareHands thisCard.compareTo(otherCard)
            }
        }
        return indexOfOtherImprovedHand
            .compareTo(indexOfThisImprovedHand)
    }

    private fun bestHand(handOfCards: HandOfCards): HandOfCards {
        if (!handOfCards.cards.contains(JOKER_CARD) || !isJokerMode) return handOfCards
        val improverCard = this.getTypeOfHand(handOfCards)?.improverCard?.invoke(handOfCards)
        return if (improverCard != null) {
            val newCards = handOfCards.cards.toMutableList()
            newCards.replaceAll { if (it == JOKER_CARD) improverCard else it }
            HandOfCards(newCards, handOfCards.bid, handOfCards.rules)
        } else handOfCards
    }

}

data class CamelCard(val label: Char) : Comparable<CamelCard> {

    override fun compareTo(other: CamelCard): Int {
        return cards.indexOf(other).compareTo(cards.indexOf(this))
    }

    override fun toString(): String {
        return label.toString()
    }
}

data class HandOfCards(val cards: List<CamelCard>, val bid: Int, val rules: CamelPokerRules) : Comparable<HandOfCards> {
    val mappedCards = cards.distinct().map { card -> card to cards.count { it.label == card.label } }

    override fun compareTo(other: HandOfCards): Int {
        return rules.compareHands(this, other)
    }

    override fun toString(): String {
        return cards.toString()
    }
}

data class HandOfCardsType(
    val type: String,
    val improverCard: (HandOfCards) -> CamelCard?,
    private val condition: (HandOfCards) -> Boolean
) {
    fun match(handOfCards: HandOfCards) = condition(handOfCards)
}

fun main() {

    fun calculateStrengthOfCards(rules: CamelPokerRules, input: List<String>): Int {
        val handsOfCards = input.map { handOfCard ->
            val split = handOfCard.split(" ")
            val cards = split[0].map { char -> CamelCard(char) }
            HandOfCards(cards, split[1].toInt(), rules)
        }
        val sortedCards = handsOfCards.sorted()
        return sortedCards.sumOf { it.bid * (sortedCards.indexOf(it) + 1) }
    }

    fun part1(input: List<String>): Int {
        val rules = CamelPokerRules(false)
        return calculateStrengthOfCards(rules, input)
    }

    fun part2(input: List<String>): Int {
        val rules = CamelPokerRules(true)
        return calculateStrengthOfCards(rules, input)
    }

    val testInput = readInput("day07")
    println(part2(testInput))

}
