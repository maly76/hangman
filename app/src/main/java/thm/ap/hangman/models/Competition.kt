package thm.ap.hangman.models

import java.io.Serializable

data class Competition(
    var roomCode: String = "",
    var playerA: Player,
    var playerB: Player? = null,
    var gameInfos: MultiPlayerGame? = null
) : Serializable {
}