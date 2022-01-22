package thm.ap.hangman.models

import org.jetbrains.annotations.NotNull

data class MultiPlayerGame (@NotNull val roomCode: String) {
    var WortToGuessA: String? = null
    var WortToGuessB: String? = null
    var TriesA: Int = 0
    var TriesB: Int = 0
    var HiddenWordA: String? = null
    var HiddenWordB: String? = null
    var StatusA: Status = Status.OFFLINE
    var StatusB: Status = Status.OFFLINE
}