package thm.ap.hangman

import android.os.Bundle
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

        val playerDAO = PlayerDAO()

        //playerDAO.addPlayer(Player.new(Firebase.auth.currentUser!!.uid, "Moh", Statistic.empty(), Status.OFFLINE))

        setContentView(R.layout.activity_main)
    }
}