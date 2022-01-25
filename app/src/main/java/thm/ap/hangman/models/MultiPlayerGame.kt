package thm.ap.hangman.models


class MultiPlayerGame {
    val host: PlayerInfos
    val guest: PlayerInfos

    init {
        host = PlayerInfos()
        guest = PlayerInfos()
    }

    class PlayerInfos {
        var wortToGuess: String? = null
        var tries: Int = 0
        var hiddenWord: String? = null
        var status: Status = Status.OFFLINE
    }
}