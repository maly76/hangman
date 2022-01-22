package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result

class PlayerDAO {
    private val playersRef: CollectionReference = Firebase.firestore.collection(TAG)
    private val playersObserver = MutableLiveData<Result<List<Player>>>()

    fun getPlayersObserver(): MutableLiveData<Result<List<Player>>> {
        refreshPlayers()
        return playersObserver
    }

    private fun refreshPlayers() {
        playersObserver.value = Result.inProgress()
        playersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val plrs = mutableListOf<Player>()
                    task.result.forEach { doc ->
                        val player = doc.toObject<Player>()
                        plrs.add(player)
                    }
                    plrs.removeIf { player -> player.id == "baseline" }
                    playersObserver.value = Result.success(plrs)
                } else {
                    playersObserver.value = Result.failure(task.exception!!.message!!)
                }
            }
    }

    fun getPlayerByID(id: String): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        observer.value = Result.inProgress()
        playersRef.document(id).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    observer.value = Result.success(task.result.toObject<Player>())
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    fun addPlayer(player: Player): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        observer.value = Result.inProgress()
        if (player.id.isNotEmpty()) {
            playersRef.document(player.id).set(player)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        refreshPlayers()
                        observer.value = Result.success(player)
                    } else {
                        observer.value = Result.failure(task.exception!!.message!!)
                    }
                }
        } else {
            observer.value = Result.failure("ID cannot be empty")
        }

        return observer
    }

    fun updatePlayer(player: Player): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        observer.value = Result.inProgress()
        playersRef.document(player.id).set(player)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshPlayers()
                    observer.value = Result.success(player)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    fun deletePlayer(player: Player): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        observer.value = Result.inProgress()
        playersRef.document(player.id).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshPlayers()
                    observer.value = Result.success(player)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    companion object {
        const val TAG = "players"
    }
}