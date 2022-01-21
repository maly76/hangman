package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition

class CompetitionDAO {
    private val competitionsRef: CollectionReference = Firebase.firestore.collection("competitions")
    val competitionsObserver: MutableLiveData<List<Competition>> by lazy {
        refreshCompetitions()
        MutableLiveData<List<Competition>>()
    }

    private fun refreshCompetitions() {
        competitionsRef.get().addOnSuccessListener {
            val compts = mutableListOf<Competition>()
            it.forEach { doc ->
                val competition = doc.toObject<Competition>()
                compts.add(competition)
            }
            competitionsObserver.value = compts
        }
    }

    fun subscribeCompetition(id: String): MutableLiveData<Competition> {

        val competitionObserver: MutableLiveData<Competition> by lazy {
            MutableLiveData<Competition>()
        }
        competitionsRef.document(id).addSnapshotListener{snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                competitionObserver.value = snapshot.toObject<Competition>()
            } else {
                competitionObserver.value = null
            }
        }
        return competitionObserver
    }

    fun addCompetition(competition: Competition) {
        competitionsRef.document().set(competition).addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun updateCompetition(competition: Competition) {
        competitionsRef.document(competition.id).set(competition).addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun deleteCompetition(competition: Competition) {
        competitionsRef.document(competition.id).delete().addOnSuccessListener {
            refreshCompetitions()
        }
    }
}