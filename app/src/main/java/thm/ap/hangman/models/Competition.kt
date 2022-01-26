package thm.ap.hangman.models

/**
 *  The competition for the multiplayer mode
 * */
data class Competition(
    var roomCode: String,
    var host: Player,
    var guest: Player? = null,
    var hostInfos: PlayerInfos = PlayerInfos(),
    var guestInfos: PlayerInfos = PlayerInfos()
) {
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
        var status: Player.Status = Player.Status.OFFLINE
    }
}