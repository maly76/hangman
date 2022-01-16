package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Statistic

class StatisticDAO {
    val statisticRef: CollectionReference = Firebase.firestore.collection("statistics")

    fun getPlayers(): MutableLiveData<List<Statistic>> {
        val statistics: MutableLiveData<List<Statistic>> by lazy {
            MutableLiveData<List<Statistic>>()
        }

        statisticRef.get().addOnSuccessListener {
            val stats = mutableListOf<Statistic>()
            it.forEach { doc ->
                val statistic = doc.toObject<Statistic>()
                stats.add(statistic)
            }
            statistics.value = stats
        }
        return statistics
    }

    fun addStatistic(statistic: Statistic) = statisticRef.document().set(statistic)

    fun updateStatistic(statistic: Statistic) = statisticRef.document(statistic.id).set(statistic)

    fun deleteStatistic(statistic: Statistic) = statisticRef.document(statistic.id).delete()
}