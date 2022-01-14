package thm.ap.hangman.gamelogic

import java.util.*


// function for testing and playing on console
//fun main() {
//    val gameLogic = GameLogic()
//    print("Word: " + gameLogic.getHiddenWord() + "\n")
//
//    while (gameLogic.tries < gameLogic.amountTries) {
//        print("Please guess a letter: ")
//        if (gameLogic.checkLetter(readLine()!![0])) {
//            println(gameLogic.getHiddenWord())
//        } else {
//            println("Try " + gameLogic.tries + " of " + gameLogic.amountTries)
//        }
//    }
//    println("Game Over!\nThe word was " + gameLogic.wordToGuess)
//}

class GameLogic {
//    private val wordToGuess = "The quick brown fox jumps over the lazy dog (äöü)"
    private val wordToGuess = "Du bist die Beste"
    private lateinit var hiddenWord: String
    private var tries = 0
    private val amountTries = 8

    init {
        generateHiddenWord()
    }

    private val letters = List<Letter>(29) {
        var c = 'A'
        when (it) {
            26 -> c = 'Ä'
            27 -> c = 'Ö'
            28 -> c = 'Ü'
            else -> c += it // Normal Letters A-Z
        }
        Letter(c)
    }

    fun getHiddenWord(): String {
        val sb = StringBuilder()
        for (element in hiddenWord) {
            sb.append(element)
            sb.append(" ")
        }
        return sb.toString()
    }

    fun getTries(): Int {
        return tries
    }

    fun getAmountTries(): Int {
        return amountTries
    }

    fun guessWord(word: String): Boolean {
        println("guessing word: $word solution: $wordToGuess")
        return if (word.lowercase(Locale.getDefault()) == wordToGuess.lowercase(Locale.getDefault())) {
            hiddenWord = wordToGuess
            true
        } else {
            tries++
            false
        }
    }

    fun checkIfWon(): Boolean {
        return hiddenWord == wordToGuess
    }

    private fun generateHiddenWord() {
        this.hiddenWord = this.wordToGuess.replace(Regex("""[a-zA-Z'äöüÄÖÜ]"""), "_")
    }

    private fun updateHiddenWord(c: Char) {
        //get index of chars from original
        var index: Int = this.wordToGuess.indexOf(c)
        while (index >= 0) {
            // replace all matching indices with the correct char
            val sb = StringBuilder(hiddenWord).also { it.setCharAt(index, c)}
            this.hiddenWord = sb.toString()
            index = this.wordToGuess.indexOf(c, index + 1)
        }
    }

    fun checkLetter(c: Char): Boolean {
        val char = c.uppercaseChar()
        val letter = this.letters.find { it.letter == char} ?: return false
        return if (wordToGuess.contains(letter.letter, true)) {
            letter.setStatus(true)
            updateHiddenWord(c.lowercaseChar())
            updateHiddenWord(c.uppercaseChar())
            true
        } else {
            letter.setStatus(false)
            this.tries++
            false
        }
    }
}

class Letter(val letter: Char) {
    private var status: LetterStatus

    init {
        this.status = LetterStatus.NOT_GUESSED
    }

    fun setStatus(right: Boolean) {
        if (right) {
            this.status = LetterStatus.RIGHT
        } else {
            this.status = LetterStatus.WRONG
        }
    }

    fun getStatus(): String {
        return when (this.status) {
            LetterStatus.NOT_GUESSED -> "not guessed"
            LetterStatus.RIGHT -> "right"
            LetterStatus.WRONG -> "wrong"
        }
    }

    enum class LetterStatus {
        NOT_GUESSED,
        RIGHT,
        WRONG
    }
}