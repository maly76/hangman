package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition

class CompetitionDAO {
    private val CompetitionsRef: CollectionReference = Firebase.firestore.collection("competitions")
    val competitionsObserver: MutableLiveData<List<Competition>> by lazy {
        refreshCompetitions()
        MutableLiveData<List<Competition>>()
    }

    private fun refreshCompetitions() {
        CompetitionsRef.get().addOnSuccessListener {
            val compts = mutableListOf<Competition>()
            it.forEach { doc ->
                val competition = doc.toObject<Competition>()
                compts.add(competition)
            }
            competitionsObserver.value = compts
        }
    }

    fun addCompetition(competition: Competition) {
        CompetitionsRef.document().set(competition).addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun updateCompetition(competition: Competition) {
        CompetitionsRef.document(competition.id).set(competition).addOnSuccessListener {
            refreshCompetitions()
        }
    }

    fun deleteCompetition(competition: Competition) {
        CompetitionsRef.document(competition.id).delete().addOnSuccessListener {
            refreshCompetitions()
        }
    }
}