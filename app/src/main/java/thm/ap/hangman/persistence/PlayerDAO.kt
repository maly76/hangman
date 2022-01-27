package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result

/**
 * The player service for managing the players in the database
 * */
class PlayerDAO {
    private val playersRef: CollectionReference = Firebase.firestore.collection(TAG)
    private val playersObserver = MutableLiveData<Result<List<Player>>>()

    /**
     * It can be observed to always receive a notification if the players are locally changed
     * @return the playersObserver
     * */
    fun getPlayersObserver(): MutableLiveData<Result<List<Player>>> {
        refreshPlayers()
        return playersObserver
    }

    /**
     * It will be called after changes on players
     * set the value of playersObserver a Result in progress to notify the observer
     * set the value on Result with success if the request was successfully or on Result with failure if the request is failed
     * */
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
                    playersObserver.value = Result.success(plrs)
                } else {
                    playersObserver.value = Result.failure(task.exception!!.message!!)
                }
            }
    }

    /**
     * get the player with the specified id
     * @param id of the player is needed
     * @return an observer which will receive a notification:
     * 1- a Result with the player if it is successfully founded
     * 2- a Result with an error it is failed or the player is not found
     * */
    fun getPlayerByID(id: String): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        observer.value = Result.inProgress()
        playersRef.document(id).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful && task.result.exists()) {
                    observer.value = Result.success(task.result.toObject<Player>())
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    /**
     * add player to the database
     * @param player is the specified player which will be added to the database
     * The id of a player should not be empty
     * @return an observer which will receive a notification:
     * 1- a Result with the player if it is successfully added
     * 2- a Result with an error it is failed or the id of the specified player is empty
     * */
    fun addPlayer(player: Player): MutableLiveData<Result<Player>> {
        val observer = MutableLiveData<Result<Player>>()

        playersRef.document(player.id).get().addOnCompleteListener { task ->
            if (task.isSuccessful && !task.result!!.exists()) {
                observer.value = Result.inProgress()
                if (player.id == Firebase.auth.currentUser!!.uid) {
                    playersRef.document(player.id).set(player)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                refreshPlayers()
                                observer.value = Result.success(player)
                            } else {
                                observer.value = Result.failure(task2.exception!!.message!!)
                            }
                        }
                } else {
                    observer.value = Result.failure("ID must be the same user ID")
                }
            } else {
                observer.value = Result.failure("Player already exists")
            }
        }

        return observer
    }

    /**
     * update a player in the database
     * @param player the specified player to update
     * @return an observer which will receive a notification:
     * 1- a Result with the player if it is successfully updated
     * 2- a Result with an error it is failed
     * */
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

    /**
     * delete a player from the database
     * @param player the specified player to delete
     * @return an observer which will receive a notification:
     * 1- a Result with the player if it is successfully deleted
     * 2- a Result with an error it is failed
     * */
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