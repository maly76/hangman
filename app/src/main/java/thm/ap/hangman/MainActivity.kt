package thm.ap.hangman

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import thm.ap.hangman.models.Category
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val auth = Auth()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth.authenticate()

        /** register */
        //auth.add(true, null, null)

        /*val categoryDAO = CategoryDAO()

        val category = Category()
        category.catName = "Spiel"
        categoryDAO.addCategory(category)

        categoryDAO.getCategories().observe(this, {
            Log.e("Test", it[0].id)
        })

        val competitionDAO = CompetitionDAO()
        val competition = Competition()
        competition.roomCode = "ABC"
        competitionDAO.addCompetition(competition)

        val statsDAO = StatisticDAO()
        val stat = Statistic()
        stat.longestWord = "Immatrikulation"
        stat.losses = 4
        stat.wins = 5
        statsDAO.addStatistic(stat)*/
        //auth.add("example123@gmail.com", "ABCD")

        setContentView(R.layout.activity_main)
    }
}