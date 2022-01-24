package thm.ap.hangman.persistence

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.MultiPlayerGame
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result
import java.io.Serializable

class CompetitionDAO(private val owner: AppCompatActivity) {
    private val competitionsRef: CollectionReference = Firebase.firestore.collection(TAG)
    private lateinit var competitionRegistration: ListenerRegistration

    private val competitionObserver = MutableLiveData<Result<Competition>>()

    private val competitionsObserver = MutableLiveData<Result<List<Competition>>>()
    private val playersRef: CollectionReference = Firebase.firestore.collection(PlayerDAO.TAG)

    fun getCompetitionsObserver(): MutableLiveData<Result<List<Competition>>> {
        refreshCompetitions()
        return competitionsObserver
    }

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

    private fun parseCompetition(snapshot: CompetitionSnapshot): MutableLiveData<Competition> {
        val observer = MutableLiveData<Competition>()

        snapshot.playerA?.get()?.addOnSuccessListener { firstOne ->
            val comp = Competition(roomCode = snapshot.id, playerA = firstOne.toObject<Player>()!!)
            comp.gameInfos = snapshot.gameInfos
            if (snapshot.playerB != null) {
                snapshot.playerB?.get()?.addOnSuccessListener { secondOne ->
                    comp.playerB = secondOne.toObject<Player>()
                    observer.value = comp
                }
            } else {
                observer.value = comp
            }
        }

        return observer
    }

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

    fun addCompetition(competition: Competition): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        competitionsRef.document(competition.roomCode).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    observer.value =
                        Result.failure("There is a competition with the same room code")
                } else {
                    val playerARef = playersRef.document(competition.playerA.id)
                    val playerBRef = playersRef.document(competition.playerB!!.id)

                    competitionsRef.document(competition.roomCode).set(
                        CompetitionSnapshot.new(
                            competition.roomCode,
                            playerARef,
                            playerBRef,
                            MultiPlayerGame()
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

    fun updateCompetition(competition: Competition): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        val playerARef = playersRef.document(competition.playerA.id)
        val playerBRef =
            if (competition.playerB != null) playersRef.document(competition.playerB!!.id) else null

        competitionsRef.document(competition.roomCode).set(
            CompetitionSnapshot.new(
                competition.roomCode,
                playerARef,
                playerBRef,
                competition.gameInfos
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

    fun deleteCompetition(competition: Competition): MutableLiveData<Result<Competition>> {
        val observer = MutableLiveData<Result<Competition>>()

        observer.value = Result.inProgress()
        competitionsRef.document(competition.roomCode).delete()
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

    fun unsubscribeCompetition() {
        competitionRegistration.remove()
    }

    companion object {
        const val TAG = "competitions"
    }

    data class CompetitionSnapshot(
        @set:DocumentId
        var id: String = ""
    ) : Serializable {
        var playerA: DocumentReference? = null
        var playerB: DocumentReference? = null
        var gameInfos: MultiPlayerGame? = null

        companion object {
            fun new(
                roomCode: String,
                firstPlayer: DocumentReference?,
                secondPlayer: DocumentReference?,
                gameInfos: MultiPlayerGame?
            ): CompetitionSnapshot {
                val compSnapshot = CompetitionSnapshot(roomCode)
                compSnapshot.playerA = firstPlayer
                compSnapshot.playerB = secondPlayer
                compSnapshot.gameInfos = gameInfos
                return compSnapshot
            }
        }
    }
}