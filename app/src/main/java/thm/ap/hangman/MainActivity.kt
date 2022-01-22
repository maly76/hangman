package thm.ap.hangman

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import thm.ap.hangman.models.*
import thm.ap.hangman.persistence.*

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

        setContentView(R.layout.activity_main)
    }
}