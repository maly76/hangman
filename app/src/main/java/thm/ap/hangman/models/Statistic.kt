package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Statistic entity
 * The id is the primary key of the entity
 * */
data class Statistic(
    @set:DocumentId
    var id: String = ""
) : Serializable {
    var wins: Int = 0
    var losses: Int = 0
    var winLosRate: Double = 0.0
    var longestWord: String? = null
    var winstreak: Int = 0

    /**
     * calculate the rate
     * */
    fun calculateRate() {
        winLosRate = (wins / losses).toDouble()
    }
}