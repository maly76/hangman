package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Statistic entity
 * The id is the primary key of the entity
 * */
data class Statistic(
    @set:DocumentId
    var id: String = ""
) : Serializable {

    val mpStats: Stats = Stats()
    val spStats: Stats = Stats()

    class Stats {
        var rates: MutableList<Rate> = mutableListOf()
        var longestWord: String = ""
        var winstreak: Int = 0
        var oldWinStreak: Int = 0
        var winStreakActive: Boolean = false

        fun getGeneralRate(): Rate {
            val generalRate = Rate.empty()
            generalRate.wins = rates.sumOf { rate -> rate.wins }
            generalRate.losses = rates.sumOf { rate -> rate.losses }
            generalRate.ties = rates.sumOf { rate -> rate.ties }
            return generalRate
        }
    }

    class Rate constructor(var categoryID: String? = null) {
        var wins: Int = 0
        var losses: Int = 0
        var ties: Int = 0

        fun getNumebrOfPlayedGames() = wins + losses + ties

        /**
         * calculate the rate
         * */
        fun getWinLosRate(): Double =
            when {
                wins == 0 -> 0.0
                losses == 0 -> 100.0
                else -> ((wins + 0.5 * (ties)) / getNumebrOfPlayedGames() * 100).roundTo(
                    2
                )
            }

        fun Double.roundTo(numFractionDigits: Int): Double {
            val factor = 10.0.pow(numFractionDigits.toDouble())
            return (this * factor).roundToInt() / factor
        }

        companion object {
            fun new(categoryID: String): Rate {
                return Rate(categoryID)
            }

            fun empty(): Rate {
                return Rate()
            }
        }
    }
}