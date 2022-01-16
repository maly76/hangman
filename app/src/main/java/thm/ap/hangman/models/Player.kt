package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Player(
    @set:DocumentId
    var id: String
): Serializable {
    var userName: String? = null
    var statistic: Statistic? = null
}