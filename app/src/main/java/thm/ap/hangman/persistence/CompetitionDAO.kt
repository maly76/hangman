package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition

class CompetitionDAO {
    val CompetitionsRef: CollectionReference = Firebase.firestore.collection("competitions")

    fun getCompetitions(): MutableLiveData<List<Competition>> {
        val competitions: MutableLiveData<List<Competition>> by lazy {
            MutableLiveData<List<Competition>>()
        }

        CompetitionsRef.get().addOnSuccessListener {
            val compts = mutableListOf<Competition>()
            it.forEach { doc ->
                val competition = doc.toObject<Competition>()
                compts.add(competition)
            }
            competitions.value = compts
        }
        return competitions
    }

    fun addCompetition(competition: Competition) = CompetitionsRef.document().set(competition)

    fun updateCompetition(competition: Competition) = CompetitionsRef.document(competition.id).set(competition)

    fun deleteCompetition(competition: Competition) = CompetitionsRef.document(competition.id).delete()
}