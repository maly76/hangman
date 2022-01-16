package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player

class PlayerDAO {
    val playersRef: CollectionReference = Firebase.firestore.collection("players")

    fun getPlayers(): MutableLiveData<List<Player>> {
        val players: MutableLiveData<List<Player>> by lazy {
            MutableLiveData<List<Player>>()
        }

        playersRef.get().addOnSuccessListener {
            val plrs = mutableListOf<Player>()
            it.forEach { doc ->
                val player = doc.toObject<Player>()
                plrs.add(player)
            }
            players.value = plrs
        }
        return players
    }

    fun addPlayer(player: Player) = playersRef.document().set(player)

    fun updatePlayer(player: Player) = playersRef.document(player.id).set(player)

    fun deletePlayer(player: Player) = playersRef.document(player.id).delete()
}