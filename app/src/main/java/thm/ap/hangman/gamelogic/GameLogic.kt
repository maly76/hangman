package thm.ap.hangman.gamelogic

class GameLogic {
    val wordToGuess = "testword"
    var tries = 0
    val amountTries = 8

    val letters = List<Letter>(29) {
        var c = 'A'
        Letter(c + it)
    }

    fun checkLetter(letter: Char): Boolean {
        this.tries++
        if (tries >= amountTries) {
            print("Game Over!")
        }
        return wordToGuess.contains(letter, true)
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

    enum class LetterStatus {
        NOT_GUESSED,
        RIGHT,
        WRONG
    }
}