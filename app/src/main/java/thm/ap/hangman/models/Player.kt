package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Player (
    @set:DocumentId
    var id: String = ""
): Serializable {
    var userName: String? = null
    var statistic: Statistic? = null
    var status: Status? = null
    var wordToGuess: String? = null
    var tries: Int = 0
    var hiddenWord: String? = null

    companion object {
        fun new(id: String = "", username: String, statistic: Statistic, status: Status): Player {
            val player = Player()
            player.userName = username
            player.statistic = statistic
            player.status = status
            player.id = id
            return player
        }

        fun empty(): Player {
            return Player()
        }
    }
}

enum class Status {
    WON,
    LOSED,
    PLAYING,
    OFFLINE
}