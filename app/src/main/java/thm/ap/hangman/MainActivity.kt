package thm.ap.hangman

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import thm.ap.hangman.models.*
import thm.ap.hangman.persistence.*
import thm.ap.hangman.persistence.PlayerDAO.Companion.TAG

class MainActivity : AppCompatActivity() {
    private val auth = Auth(this)

    private val playerDAO = PlayerDAO()
    private val categoryDAO = CategoryDAO()
    private val competitionDAO = CompetitionDAO(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.authenticate()) {
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show()
        } else {
            auth.signUp()
            // TODO: sign in OR sign up
            //throw NotImplementedError();
        }

        playerDAO.getPlayersObserver().observe(this, { result ->
            when(result.status) {
                Result.Status.IN_PROGRESS -> Log.i(TAG, "the request in progress")
                Result.Status.SUCCESS     -> {
                    Log.i(TAG, result.data!!.toString())
                }
                Result.Status.FAILURE     -> Log.i(TAG, result.error!!)
            }
        })

        competitionDAO.subscribeCompetition("234").observe(this, { result ->
            when(result.status) {
                Result.Status.IN_PROGRESS -> Log.i(TAG, "the request in progress")
                Result.Status.SUCCESS     -> {
                    Log.i(TAG, result.data!!.toString())
                }
                Result.Status.FAILURE     -> Log.i(TAG, result.error!!)
            }
        })

        setContentView(R.layout.activity_main)
    }
}