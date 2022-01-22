package thm.ap.hangman.models

import java.io.Serializable

data class Competition(
    var roomCode: String = "",
    var playerA: Player? = null,
    var playerB: Player? = null
): Serializable {
}