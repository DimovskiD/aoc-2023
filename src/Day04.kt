data class Card(val id: Int, val winningNumbers: List<Int>, val myNumbers: List<Int>) {

    fun matchedNumbers(): Int =
        winningNumbers.intersect(myNumbers.toSet()).count()

    fun calculatePoints(): Int {
        var points = 0
        val intersection = winningNumbers.intersect(myNumbers.toSet())
        intersection.forEach { _ ->
            if (points == 0) points += 1
            else points *= 2
        }
        return points
    }
}

fun main() {

    fun getCardsForInput(input: List<String>): List<Card> {
        return input.map {
            val id = it.substringBefore(":").filter { char -> char.isDigit() }.toInt()
            val numbers = it.substringAfter(":").split("|")
            val winningNumbers = numbers[0].stringToListOfNumbers()
            val myNumbers = numbers[1].stringToListOfNumbers()
            Card(id, winningNumbers, myNumbers)
        }
    }

    fun getCards(cards: List<Card>, allCards: List<Card>): List<Card> {
        val totalCards = mutableListOf<Card>()
        totalCards.addAll(cards)

        cards.forEach { card ->
            val index = allCards.indexOf(card)
            val copiedCardsNumber = card.matchedNumbers()
            if (copiedCardsNumber > 0) {
                totalCards.addAll(getCards(allCards.subList(index + 1, index + copiedCardsNumber + 1), allCards))
            }
        }
        return totalCards
    }

    fun part1(input: List<String>): Int {
        val cards = getCardsForInput(input)
        return cards.sumOf { it.calculatePoints() }
    }

    fun part2(input: List<String>): Int {
        val cards = getCardsForInput(input)
        return getCards(cards, cards).count()
    }

    val testInput = readInput("day04")
    println(part2(testInput))
}

fun String.stringToListOfNumbers() = this.trim().split(' ').mapNotNull { number ->
    if (number.isNotEmpty())
        number.toInt()
    else null
}