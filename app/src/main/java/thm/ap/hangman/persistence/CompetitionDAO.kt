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
import java.io.Serializable

class CompetitionDAO (private val owner: AppCompatActivity) {

    data class CompetitionSnapshot(
        @set:DocumentId
        var id: String = ""
    ): Serializable {
        var playerA: DocumentReference? = null
        var playerB: DocumentReference? = null
        var gameInfos: MultiPlayerGame? = null

        companion object {
            fun new(roomCode: String, firstPlayer: DocumentReference?, secondPlayer: DocumentReference?, gameInfos: MultiPlayerGame?): CompetitionSnapshot {
                val compSnapshot = CompetitionSnapshot(roomCode)
                compSnapshot.playerA = firstPlayer
                compSnapshot.playerB = secondPlayer
                compSnapshot.gameInfos = gameInfos
                return compSnapshot
            }
        }
    }

    private val competitionsRef: CollectionReference = Firebase.firestore.collection("competitions")
    private val competitionObserver: MutableLiveData<Competition> by lazy {
        MutableLiveData<Competition>()
    }
    private lateinit var competitionRegistration: ListenerRegistration
    val competitionsObserver: MutableLiveData<List<Competition>> by lazy {
        refreshCompetitions()
        MutableLiveData<List<Competition>>()
    }
    private val playersRef: CollectionReference = Firebase.firestore.collection(PlayerDAO.TAG)

    private fun refreshCompetitions() {
        competitionsRef.get().addOnSuccessListener { snapshots ->
            parseCompetitions(snapshots).observe(owner, { competitions ->
                competitionsObserver.value = competitions
            })
        }
    }

    fun subscribeCompetition(id: String): MutableLiveData<Competition> {
        competitionRegistration = competitionsRef.document(id).addSnapshotListener{snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.toObject<CompetitionSnapshot>()
                parseCompetition(data!!).observe(owner, { comp ->
                    competitionObserver.value = comp
                })
            } else {
                competitionObserver.value = null
            }
        }
        return competitionObserver
    }

    private fun parseCompetition(snapshot: CompetitionSnapshot): MutableLiveData<Competition> {
        val observer = MutableLiveData<Competition>()
        val comp = Competition(roomCode = snapshot.id)

        comp.gameInfos = snapshot.gameInfos
        snapshot.playerA?.get()?.addOnSuccessListener { firstOne ->
            comp.playerA = firstOne.toObject<Player>()
            snapshot.playerB?.get()?.addOnSuccessListener { secondOne ->
                comp.playerB = secondOne.toObject<Player>()
                observer.setValue(comp)
            }
        }

        return observer
    }

    private fun parseCompetitions (snapshots: QuerySnapshot): MutableLiveData<List<Competition>> {
        val observer = MutableLiveData<List<Competition>>()

        val compts = mutableListOf<Competition>()
        snapshots.forEachIndexed() { index, doc ->
            val data = doc.toObject<CompetitionSnapshot>()
            parseCompetition(data).observe(owner, { comp ->
                compts.add(comp)
                if (index == snapshots.size() - 1) {
                    observer.value = compts
                }
            })
        }

        return observer
    }

    fun addCompetition(competition: Competition) {
        competitionsRef.document(competition.roomCode).get().addOnCompleteListener{ task ->
            if (task.isSuccessful && !task.result.exists()) {
                val playerARef =  playersRef.document(competition.playerA!!.id)
                val playerBRef =  playersRef.document(competition.playerB!!.id)

                competitionsRef.document(competition.roomCode).set(CompetitionSnapshot.new(competition.roomCode, playerARef, playerBRef, MultiPlayerGame()))
                    .addOnSuccessListener {
                        refreshCompetitions()
                    }
            }
        }
    }

    fun updateCompetition(competition: Competition) {
        val playerARef = competitionsRef.document(competition.playerA!!.id)
        val playerBRef = competitionsRef.document(competition.playerB!!.id)

        competitionsRef.document(competition.roomCode).set(CompetitionSnapshot.new(competition.roomCode, playerARef, playerBRef, competition.gameInfos))
            .addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun deleteCompetition(competition: Competition) {
        competitionsRef.document(competition.roomCode).delete()
            .addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun removeRegistrations() {
        competitionRegistration.remove()
    }
}