package thm.ap.hangman

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import thm.ap.hangman.models.*
import thm.ap.hangman.persistence.*
import thm.ap.hangman.persistence.PlayerDAO.Companion.TAG

class MainActivity : AppCompatActivity() {
    private val auth = Auth(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.authenticate()) {
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show()
        } else {
            auth.signUp()
            // TODO: sign in OR sign up
            //throw NotImplementedError();
        }

        /** How to use the competition subscriber ... */
        val cDAO = CompetitionDAO(this)

        PlayerDAO.playersObserver.observe(this, { players ->
            cDAO.addCompetition(Competition(roomCode = "roomC", playerA = players[0], playerB = players[1], gameInfos = MultiPlayerGame())).observe(this, { result ->
                when(result.status) {
                    Result.Status.IN_PROGRESS -> Log.e("Result", "in progress")
                    Result.Status.SUCCESS     -> Log.e("Result", "success " + result.data.toString())
                    Result.Status.FAILURE     -> Log.e("Result", result.error!!)
                }
            })
        })

        /*cDAO.getCompetetionsObserver().observe(this, { result ->
            when(result.status) {
                Result.Status.IN_PROGRESS -> Log.e("Result", "in progress")
                Result.Status.SUCCESS     -> Log.e("Result", "success " + result.data.toString())
                Result.Status.FAILURE     -> Log.e("Result", result.error!!)
            }
        })*/

        /*cDAO.subscribeCompetition("roomC").observe(this, { result ->
            when(result.status) {
                Result.Status.IN_PROGRESS -> Log.e("Result", "in progress")
                Result.Status.SUCCESS     -> Log.e("Result", "success " + result.data)
                Result.Status.FAILURE     -> Log.e("Result", result.error!!)
            }
        })*/

        /*

        */

        // cDAO.unsubscribeCompetition()

        //playerDAO.addPlayer(Player.new(Firebase.auth.currentUser!!.uid, "Moh", Statistic.empty(), Status.OFFLINE))

        setContentView(R.layout.activity_main)
    }
}