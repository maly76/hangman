package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * The Player entity
 * The id is the primary key
 * each player has also a statistic and a username
 * */
data class Player(
    @set:DocumentId
    var id: String = ""
) : Serializable {
    var userName: String? = null
    var statistic: Statistic? = null

    companion object {
        /**
         * static method for creating a player with the specified parameters
         * @param id the id of the player and it should be the id of the user, because every user has an player profile
         * @param username of the player
         * @param statistic of the player
         * @return a new object of Player
         * */
        fun new(id: String, username: String?, statistic: Statistic): Player {
            val player = Player()
            player.userName = username
            player.statistic = statistic
            player.id = id
            return player
        }

        /**
         * @return a new empty player
         * */
        fun empty(): Player {
            return Player()
        }
    }

    enum class Status {
        WON,
        LOSED,
        PLAYING,
        OFFLINE,
        READY,
        AGAIN
    }
}