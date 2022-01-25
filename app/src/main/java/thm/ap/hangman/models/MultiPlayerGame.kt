package thm.ap.hangman.models

/**
 * The infos of a multiplayer game
 * Is needed in the multiplayergame
 * */
class MultiPlayerGame {
    val host: PlayerInfos
    val guest: PlayerInfos

    init {
        host = PlayerInfos()
        guest = PlayerInfos()
    }

    /**
     * The infos of a player in a multiplayer game
     * wordToGuess: which word has the player to guess
     * tries: which number of tries he needed
     * hiddenWord: which word is still hidden
     * status: WON, LOSED, PLAYING or OFFLINE
     * */
    class PlayerInfos {
        var wortToGuess: String? = null
        var tries: Int = 0
        var hiddenWord: String? = null
        var status: Status = Status.OFFLINE
    }
}