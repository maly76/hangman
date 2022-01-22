package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player

class PlayerDAO {
    companion object {
        const val TAG = "players"

        private val playersRef: CollectionReference = Firebase.firestore.collection(TAG)

        val playersObserver: MutableLiveData<List<Player>> by lazy {
            refreshPlayers()
            MutableLiveData<List<Player>>()
        }

        private fun refreshPlayers() {
            playersRef.get().addOnSuccessListener {
                val plrs = mutableListOf<Player>()
                it.forEach { doc ->
                    val player = doc.toObject<Player>()
                    plrs.add(player)
                }
                plrs.removeIf { player -> player.id == "baseline" }
                playersObserver.value = plrs
            }
        }

        fun getPlayerByID(id: String): MutableLiveData<Player> {
            val playerObserver: MutableLiveData<Player> by lazy {
                MutableLiveData<Player>()
            }

            playersRef.document(id).get().addOnCompleteListener() { task ->
                playerObserver.value = if (task.isSuccessful) task.result.toObject<Player>() else null
            }

            return playerObserver
        }

        fun addPlayer(player: Player) {
            if (player.id.isNotEmpty()) {
                playersRef.document(player.id).set(player).addOnSuccessListener {
                    refreshPlayers()
                }
            }
        }

        fun incrementTries(docId: String) {
            val washingtonRef = playersRef.document(docId)

            // Atomically increment the tries of the player by 1.
            washingtonRef.update("tries", FieldValue.increment(1))
        }

        fun updatePlayer(player: Player) {
            playersRef.document(player.id).set(player).addOnSuccessListener {
                refreshPlayers()
            }
        }

        fun deletePlayer(player: Player) {
            playersRef.document(player.id).delete().addOnSuccessListener {
                refreshPlayers()
            }
        }
    }
}