package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Player(
    @set:DocumentId
    var id: String = ""
) : Serializable {
    var userName: String? = null
    var statistic: Statistic? = null

    companion object {
        fun new(id: String = "", username: String, statistic: Statistic): Player {
            val player = Player()
            player.userName = username
            player.statistic = statistic
            player.id = id
            return player
        }

        fun empty(): Player {
            return Player()
        }
    }
}