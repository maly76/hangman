package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class Player(
    @set:DocumentId
    var id: String = ""
): Serializable {
    var userName: String? = null
    var statitic: Statistic? = null
}