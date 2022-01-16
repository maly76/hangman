package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Competition(
    @set:DocumentId
    var id: String = ""
): Serializable {
    var roomCode: String? = null
    var firstPlayer: Player? = null
    var secondPlayer: Player? = null
}