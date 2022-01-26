package thm.ap.hangman.persistence

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result
import java.io.Serializable

/**
 * CompetitionDAO for managing the competitions in the database
 * @param owner the activity which calls the service because the lifecycle is required on observing
 * */
class CompetitionDAO(private val owner: LifecycleOwner) {
    private val competitionsRef: CollectionReference = Firebase.firestore.collection(TAG)
    private lateinit var competitionRegistration: ListenerRegistration

    private val competitionObserver = MutableLiveData<Result<Competition>>()

    private val competitionsObserver = MutableLiveData<Result<List<Competition>>>()
    private val playersRef: CollectionReference = Firebase.firestore.collection(PlayerDAO.TAG)

    /**
     * It can be observed to always receive a notification if the competitions are locally changed
     * @return the competitionsObserver
     * */
    fun getCompetitionsObserver(): MutableLiveData<Result<List<Competition>>> {
        refreshCompetitions()
        return competitionsObserver
    }

    /**
     * It will be called after changes on competitions
     * set the value of competitionObserver a Result in progress to notify the observer
     * set the value on Result with success if the request was successfully or on Result with failure if the request is failed
     * */
    private fun refreshCompetitions() {
        competitionsObserver.value = Result.inProgress()
        competitionsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                parseCompetitions(task.result).observe(owner, { competitions ->
                    competitionsObserver.value = Result.success(competitions)
                })
            } else {
                competitionObserver.value = Result.failure(task.exception!!.message!!)
            }
        }
    }

    /**
     * Subscribe a specified competition to be notified always after changes in the database
     * @param id of the specified competition to be subscribed
     * @return an observer to be observed
     * */
    fun subscribeCompetition(id: String): MutableLiveData<Result<Competition>> {
        competitionObserver.value = Result.inProgress()
        competitionRegistration = competitionsRef.document(id).addSnapshotListener { snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.toObject<CompetitionSnapshot>()
                parseCompetition(data!!).observe(owner, { comp ->
                    competitionObserver.value = Result.success(comp)
                })
            } else {
                competitionObserver.value = Result.failure("competition does not exist")
            }
        }
        return competitionObserver
    }

    /**
     * After reading a competition from the database
     * The players which are Document references must be also read from the database
     * @param snapshot the map which comes from the database
     * @return the competition
     * */
    private fun parseCompetition(snapshot: CompetitionSnapshot): MutableLiveData<Competition> {
        val observer = MutableLiveData<Competition>()

        snapshot.hostRef?.get()?.addOnSuccessListener { firstOne ->
            val comp = Competition(
                roomCode = snapshot.id,
                host = firstOne.toObject<Player>()!!,
                hostInfos = snapshot.hostInfos!!,
                guestInfos = snapshot.guestInfos!!
            )
            if (snapshot.guestRef != null) {
                snapshot.guestRef?.get()?.addOnSuccessListener { secondOne ->
                    comp.guest = secondOne.toObject<Player>()
                    observer.value = comp
                }
            } else {
                observer.value = comp
            }
        }

        return observer
    }

    /**
     * convert a list of CompetitionSnapshots to competitions
     * @param snapshots a list of the Snapshots
     * @return an observer to notify when the work is done
     * */
    private fun parseCompetitions(snapshots: QuerySnapshot): MutableLiveData<List<Competition>> {
        val observer = MutableLiveData<List<Competition>>()

        val compts = mutableListOf<Competition>()
        snapshots.forEachIndexed() { index, doc ->
            val data = doc.toObject<CompetitionSnapshot>()
            parseCompetition(data).observe(owner, { comp ->
                compts.add(comp)
                if (index == snapshots.size() - 1) {
                    compts.removeIf { competition -> competition.roomCode == "baseline" }
                    observer.value = compts
                }
            })
        }

        return observer
    }

    /**
     * get competition by id to the database
     * @param id is the specified competition id
     * @return an observer which will receive a notification:
     * 1- a Result with the competition if the request is successfully and the competition exists
     * 2- a Result with an error it is failed or the competition does not exist
     * */
    fun getCompetitionByID(id: String): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        competitionsRef.document(id).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val data = task.result.toObject<CompetitionSnapshot>()
                    parseCompetition(data!!).observe(owner, { comp ->
                        observer.value = Result.success(comp)
                    })
                } else {
                    observer.value = Result.failure("competition does not exist")
                }
            }

        return observer
    }

    /**
     * add competition to the database
     * @param competition is the specified competition which will be added to the database
     * @return an observer which will receive a notification:
     * 1- a Result with the competition if it is successfully added
     * 2- a Result with an error it is failed
     * */
    fun addCompetition(competition: Competition): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        competitionsRef.document(competition.roomCode).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    observer.value =
                        Result.failure("There is a competition with the same room code")
                } else {
                    val hostRef = playersRef.document(competition.host.id)
                    val guestRef =
                        if (competition.guest != null) playersRef.document(competition.guest!!.id) else null

                    competitionsRef.document(competition.roomCode).set(
                        CompetitionSnapshot.new(
                            competition.roomCode,
                            hostRef,
                            guestRef,
                            competition.hostInfos,
                            competition.guestInfos
                        )
                    )
                        .addOnSuccessListener {
                            refreshCompetitions()
                            observer.value = Result.success(competition)
                        }
                }
            }
        }

        return observer
    }

    /**
     * update a competition in the database
     * @param competition the specified category to update
     * @return an observer which will receive a notification:
     * 1- a Result with the competition if it is successfully updated
     * 2- a Result with an error it is failed
     * */
    fun updateCompetition(competition: Competition): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        val hostRef = playersRef.document(competition.host.id)
        val guestRef =
            if (competition.guest != null) playersRef.document(competition.guest!!.id) else null

        competitionsRef.document(competition.roomCode).set(
            CompetitionSnapshot.new(
                competition.roomCode,
                hostRef,
                guestRef,
                competition.hostInfos,
                competition.guestInfos
            )
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshCompetitions()
                    observer.value = Result.success(competition)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    /**
     * delete a competition from the database
     * @param competition the specified category to delete
     * @return an observer which will receive a notification:
     * 1- a Result with the competition if it is successfully deleted
     * 2- a Result with an error it is failed
     * */
    fun deleteCompetition(roomCode: String): MutableLiveData<Result<String>> {
        val observer = MutableLiveData<Result<String>>()

        observer.value = Result.inProgress()
        competitionsRef.document(roomCode).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshCompetitions()
                    observer.value = Result.success(roomCode)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    /**
     * To remove the subscription of a competition
     * Should be always called if the subscription is no longer needed
     * */
    fun unsubscripeCompetition() {
        competitionRegistration.remove()
    }

    companion object {
        const val TAG = "competitions"
    }

    /**
     * CompetitionSnapshot
     * is needed for saving the players as references which will be converted to Competition after reading from the database
     * */
    data class CompetitionSnapshot(
        @set:DocumentId
        var id: String = ""
    ) : Serializable {
        var hostRef: DocumentReference? = null
        var guestRef: DocumentReference? = null
        var hostInfos: Competition.PlayerInfos? = null
        var guestInfos: Competition.PlayerInfos? = null

        companion object {
            /**
             * @param roomCode of the competition
             * @param firstPlayer the document reference of the host
             * @param secondPlayer the document reference of the guest
             * @param gameInfos the informations of an multiplayergame
             * @return a new object with the specified properties
             * */
            fun new(
                roomCode: String,
                hostRef: DocumentReference?,
                guestRef: DocumentReference?,
                hostInfos: Competition.PlayerInfos?,
                guestInfos: Competition.PlayerInfos?
            ): CompetitionSnapshot {
                val compSnapshot = CompetitionSnapshot(roomCode)
                compSnapshot.hostRef = hostRef
                compSnapshot.guestRef = guestRef
                compSnapshot.hostInfos = hostInfos
                compSnapshot.guestInfos = guestInfos
                return compSnapshot
            }
        }
    }
}