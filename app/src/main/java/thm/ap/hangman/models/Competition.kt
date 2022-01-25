package thm.ap.hangman.models

import java.io.Serializable

/**
 *  The competition for the multiplayer mode
 * */
data class Competition(
    var roomCode: String = "",
    var host: Player,
    var guest: Player? = null,
    var gameInfos: MultiPlayerGame? = null
) : Serializable {
}