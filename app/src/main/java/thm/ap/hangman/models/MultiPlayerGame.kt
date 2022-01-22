package thm.ap.hangman.models

class MultiPlayerGame {
    var WortToGuessA: String? = null
    var WortToGuessB: String? = null
    var TriesA: Int = 0
    var TriesB: Int = 0
    var HiddenWordA: String? = null
    var HiddenWordB: String? = null
    var StatusA: Status = Status.OFFLINE
    var StatusB: Status = Status.OFFLINE
}