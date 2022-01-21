package thm.ap.hangman

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import thm.ap.hangman.persistence.*

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
        val cDAO = CompetitionDAO()
        cDAO.subscribeCompetition("TIBkbrPTvR7Tyjm2RK5P").observe(this, { competition ->
            Log.e("Competition changed", competition.toString())
        })

        //playerDAO.addPlayer(Player.new(Firebase.auth.currentUser!!.uid, "Moh", Statistic.empty(), Status.OFFLINE))

        setContentView(R.layout.activity_main)
    }
}