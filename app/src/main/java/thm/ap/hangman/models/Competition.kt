package thm.ap.hangman.models

import java.io.Serializable

data class Competition(
    var roomCode: String = "",
    var host: Player,
    var guest: Player? = null,
    var gameInfos: MultiPlayerGame? = null
) : Serializable {
}